package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.Transfer;
import org.apache.commons.dbutils.DbUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.logging.Logger;

/**
 * Transfer DAO and Implementation class
 */
public class TransferDao {
    private static final Logger log = Logger.getLogger("TransferDao");
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:moneytransferapp;DB_CLOSE_DELAY=-1";

    //  Database credentials
    private static final String USER = "sa";
    private static final String PASS = "sa";

    private static final String SELECT_ALL = "SELECT * FROM TRANSFER";
    private static final String INSERT_TRANSFER = "INSERT INTO TRANSFER values (TRANSFER_SEQ.nextVal, ?, ?, ?, ?, 'PENDING', null, SYSDATE, null);";
    private static final String UPDATE_PROCESSED_TRANSFER = "UPDATE TRANSFER SET STATUS = 'PROCESSED', LAST_UPDATED_DT = SYSDATE WHERE ID = ?";
    private static final String UPDATE_FAILED_TRANSFER = "UPDATE TRANSFER SET STATUS = 'FAILED', REMARKS = ?, LAST_UPDATED_DT = SYSDATE WHERE ID = ?";
    private static final String SELECT_BY_TO_ACCOUNT_NO = "SELECT * FROM TRANSFER WHERE TO_ACCOUNT_NO = ?";
    private static final String SELECT_BY_FROM_ACCOUNT_NO = "SELECT * FROM TRANSFER WHERE FROM_ACCOUNT_NO = ?";

    private static final AccountDao accountDao = new AccountDao();


    public List<Transfer> getAllTransfers() throws Exception {
        List<Transfer> transferList = new ArrayList<>();

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try(Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_ALL)){
            if (rs == null)
                throw new SQLException("SQL Exception while executing : " + SELECT_ALL);

            while (rs.next()) {
                Transfer transfer = new Transfer(rs.getInt("ID"), rs.getLong("FROM_ACCOUNT_NO"), rs.getLong("TO_ACCOUNT_NO"), rs.getBigDecimal("AMOUNT"), rs.getString("CURRENCY_CODE"), rs.getString("STATUS"), rs.getString("REMARKS"), rs.getDate("CREATED_DT"), rs.getDate("LAST_UPDATED_DT"));
                transferList.add(transfer);
            }
            return transferList;
        } catch(SQLException se) {
            log.severe("SQL Exception while executing : " + SELECT_ALL);
            throw new SQLException(se);
        } catch(Exception e) {
            throw new Exception(e);
        }
    }

    public List<Transfer> getTransfersByAccountNo(Long accountNo, String accountType) throws Exception {
        log.info("getTransfersByAccountNo : " + accountNo);
        ResultSet rs = null;
        List<Transfer> transfersList = new ArrayList<>();

        //Load Driver
        Class.forName(JDBC_DRIVER);
        String sqlQuery;
        if (accountType.equalsIgnoreCase("TO"))
            sqlQuery = SELECT_BY_TO_ACCOUNT_NO;
        else
            sqlQuery = SELECT_BY_FROM_ACCOUNT_NO;
        // Try with resource to ensure resources are closed on exit
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {
            stmt.setLong(1, accountNo);
            // Execute a query
            rs = stmt.executeQuery();
            while (rs.next()) {
                Transfer transfer = new Transfer(rs.getInt("ID"), rs.getLong("FROM_ACCOUNT_NO"), rs.getLong("TO_ACCOUNT_NO"), rs.getBigDecimal("AMOUNT"), rs.getString("CURRENCY_CODE"), rs.getString("STATUS"), rs.getString("REMARKS"), rs.getDate("CREATED_DT"), rs.getDate("LAST_UPDATED_DT"));
                transfersList.add(transfer);
            }
            return transfersList;
        } catch (SQLException se) {
            log.severe("SQL Exception while executing : " + sqlQuery + " - accountNo : " + accountNo);
            throw new SQLException(se);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            DbUtils.closeQuietly(rs);
            // finally block used to close remaining resources
            // end finally try
        }
    }

    /**
     * @param transfer transfer to be processed
     * @return Transfer.transferResponse
     * @throws Exception e
     */
    public Transfer.transferResponse processTransfer(Transfer transfer) throws Exception {
        log.info("@@@ processTransfer");
        int response = 0;
        int transferId = 0;
        Connection conn = null;
        ResultSet rs;
        PreparedStatement insertStmt;
        PreparedStatement updateStmt = null;

        //Load Driver
        Class.forName(JDBC_DRIVER);

        try{
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            //set autocommit false to control the rollback in case of exception
            conn.setAutoCommit(false);

            // Insert transfer record to log SUCCESS/FAILURE
            insertStmt = conn.prepareStatement(INSERT_TRANSFER);
            insertStmt.setLong(1, transfer.getSourceAccountNo());
            insertStmt.setLong(2, transfer.getDestinationAccountNo());
            insertStmt.setBigDecimal(3, transfer.getAmount());
            insertStmt.setString(4, transfer.getCurrencyCode());

            //Execute Insert query
            log.info("@@@ before Execute insert");
            int insertNo = insertStmt.executeUpdate();
            log.info("@@@ " + insertNo + " transfer inserted.");
            if (insertNo != 1) {
                throw new Exception("Transfer insertion failed.");
            }
            rs = insertStmt.getGeneratedKeys();
            if (rs.next())
                transferId = rs.getInt(1);
            log.info("Transfer successfully inserted in PENDING status. ID : " + transferId);
            conn.commit();

            //Lock both accounts until transaction completed
            Account fromAccount = accountDao.lockAccountByNumber(transfer.getSourceAccountNo());
            Account toAccount = accountDao.lockAccountByNumber(transfer.getDestinationAccountNo());

            // validation method to call before any fund movement
            Transfer.transferResponse validTransactionResponse = transactionValidations(transfer);
            log.info("transactionValidations : " + validTransactionResponse.getErrorMessage());

            if (!validTransactionResponse.equals(Transfer.transferResponse.SUCCESS)) {
                log.info("Transfer validation error -- Update transfer record to failed.");
                updateStmt = conn.prepareStatement(UPDATE_FAILED_TRANSFER);
                updateStmt.setInt(1, transferId);
                updateStmt.setString(2, validTransactionResponse.getErrorMessage());
                updateStmt.executeUpdate();
                conn.commit();
                return validTransactionResponse;
            }

            log.info("Transfer validation success -- Processing transfer.");

            Transfer.transferResponse transferFundResponse = accountDao.transferFund(fromAccount, toAccount, transfer.getAmount(), transfer.getCurrencyCode());

            if (!transferFundResponse.equals(Transfer.transferResponse.SUCCESS)) {
                log.info("Transfer fund error -- Update transfer record to failed.");
                updateStmt = conn.prepareStatement(UPDATE_FAILED_TRANSFER);
                updateStmt.setInt(1, transferId);
                updateStmt.setString(2, transferFundResponse.getErrorMessage());
                updateStmt.executeUpdate();
                conn.commit();
                return transferFundResponse;
            }

            log.info("Transfer fund success -- Update transfer record to success.");

            updateStmt = conn.prepareStatement(UPDATE_PROCESSED_TRANSFER);
            updateStmt.setInt(1, transferId);
            updateStmt.executeUpdate();
            conn.commit();
            return Transfer.transferResponse.SUCCESS;
        } catch(SQLException se) {
            log.severe("@@@ SQLException : " + se.getMessage());
            if (conn != null) {
                //FIXME : In case of unexpected exception, rollback transfer fund and update transfer record as failed.
                conn.rollback();
                updateStmt = conn.prepareStatement(UPDATE_FAILED_TRANSFER);
                if (updateStmt != null) {
                    updateStmt.setInt(1, transferId);
                    updateStmt.setString(2, se.getMessage());
                    updateStmt.executeUpdate();
                }
                conn.commit();
            }
            throw new SQLException(se);
        } catch(Exception e) {
            log.severe("@@@ Exception : " + e.getMessage() + conn + updateStmt);
            if (conn != null) {
                //FIXME : In case of unexpected exception, rollback transfer fund and update transfer record as failed.
                conn.rollback();
                updateStmt = conn.prepareStatement(UPDATE_FAILED_TRANSFER);
                if (updateStmt != null) {
                    updateStmt.setInt(1, transferId);
                    updateStmt.setString(2, e.getMessage());
                    updateStmt.executeUpdate();
                }
                conn.commit();
            }
            throw new Exception(e);
        }
    }

    /**
     * Validation check method - to be called before taking/crediting amount from/to account
     *
     * @param transfer transfer object
     * @return Transfer.transferResponse
     * @throws Exception e
     */
    private Transfer.transferResponse transactionValidations(Transfer transfer) throws Exception {
        log.info("transactionValidations");
        Account fromAccount = accountDao.getAccountByAccountNo(transfer.getSourceAccountNo());
        if (fromAccount == null) {
            return Transfer.transferResponse.INVALID_FROM_ACC;
        }
        log.info("Valid Source Account.");
        Account toAccount = accountDao.getAccountByAccountNo(transfer.getDestinationAccountNo());
        if (toAccount == null) {
            return Transfer.transferResponse.INVALID_TO_ACC;
        }
        log.info("Valid Destination Account.");

        //Validating currency codes
        String transferCurrencyCode = transfer.getCurrencyCode();
        try {
            Currency instance = Currency.getInstance(transferCurrencyCode);
            //noinspection ResultOfMethodCallIgnored
            instance.getCurrencyCode().equals(transferCurrencyCode);
        } catch (Exception e) {
            log.info("Invalid currency code : " + transferCurrencyCode);
            return Transfer.transferResponse.INVALID_CURRENCY_TRANSFER;
        }
        log.info("Valid Transfer Currency.");

        String fromCurrencyCode = fromAccount.getCurrencyCode();
        try {
            Currency instance = Currency.getInstance(fromCurrencyCode);
            //noinspection ResultOfMethodCallIgnored
            instance.getCurrencyCode().equals(fromCurrencyCode);
        } catch (Exception e) {
            log.info("Invalid currency code : " + fromCurrencyCode);
            return Transfer.transferResponse.INVALID_CURRENCY_FROM_ACC;
        }
        log.info("Valid Source Account Currency.");

        String toCurrencyCode = toAccount.getCurrencyCode();
        try {
            Currency instance = Currency.getInstance(toCurrencyCode);
            //noinspection ResultOfMethodCallIgnored
            instance.getCurrencyCode().equals(toCurrencyCode);
        } catch (Exception e) {
            log.info("Invalid currency code : " + toCurrencyCode);
            return Transfer.transferResponse.INVALID_CURRENCY_TO_ACC;
        }
        log.info("Valid Destination Account Currency.");

        if (!transferCurrencyCode.equalsIgnoreCase(fromCurrencyCode) && !transferCurrencyCode.equalsIgnoreCase(toCurrencyCode)) {
            log.info("Transfer currency doesn't correspond to either account currencies.");
            return Transfer.transferResponse.TRANSFER_CURRENCY_MISMATCH;
        }


        //TODO CONVERTER


        //Validating sufficient fund
        BigDecimal newBalance = fromAccount.getBalance().subtract(transfer.getAmount());
        //check account balance
        if (newBalance.compareTo(new BigDecimal(0)) < 0) {
            log.info("Insufficient balance on account : " + fromAccount.getAccountNo());
            return Transfer.transferResponse.INSUFFICIENT_FUND;
        }
        log.info("All transfer validations passed.");
        return Transfer.transferResponse.SUCCESS;
    }
}

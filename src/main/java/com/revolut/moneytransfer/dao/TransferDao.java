package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.Rate;
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

    private static final String SELECT_ALL = "SELECT * FROM TRANSFERS";
    private static final String INSERT_TRANSFER = "INSERT INTO TRANSFERS values (TRANSFERS_SEQ.nextVal, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', SYSDATE, null);";
    private static final String UPDATE_TRANSFER = "UPDATE TRANSFERS SET DEBITED_AMOUNT = ?, DEBITED_CURRENCY_CODE = ?, CREDITED_AMOUNT = ?, CREDITED_CURRENCY_CODE = ?, RATE = ?, STATUS = ?, LAST_UPDATED_DT = SYSDATE WHERE ID = ?";
    private static final String SELECT_BY_TO_ACCOUNT_NO = "SELECT * FROM TRANSFERS WHERE TO_ACCOUNT_NO = ?";
    private static final String SELECT_BY_FROM_ACCOUNT_NO = "SELECT * FROM TRANSFERS WHERE FROM_ACCOUNT_NO = ?";

    private static final AccountDao accountDao = new AccountDao();
    private static final RateDao rateDao = new RateDao();


    /**
     * @return all transfers
     * @throws Exception e
     */
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
                Transfer transfer = new Transfer(rs.getInt("ID"), rs.getLong("FROM_ACCOUNT_NO"), rs.getLong("TO_ACCOUNT_NO"), rs.getBigDecimal("DEBITED_AMOUNT"), rs.getString("DEBITED_CURRENCY_CODE"), rs.getBigDecimal("TRANSFER_AMOUNT"), rs.getString("TRANSFER_CURRENCY_CODE"), rs.getBigDecimal("CREDITED_AMOUNT"), rs.getString("CREDITED_CURRENCY_CODE"), rs.getBigDecimal("RATE"), rs.getString("STATUS"), rs.getDate("CREATED_DT"), rs.getDate("LAST_UPDATED_DT"));
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

    /**
     * @param accountNo   account no
     * @param accountType TO or FROM account
     * @return Transfers by account
     * @throws Exception e
     */
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
                Transfer transfer = new Transfer(rs.getInt("ID"), rs.getLong("FROM_ACCOUNT_NO"), rs.getLong("TO_ACCOUNT_NO"), rs.getBigDecimal("DEBITED_AMOUNT"), rs.getString("DEBITED_CURRENCY_CODE"), rs.getBigDecimal("TRANSFER_AMOUNT"), rs.getString("TRANSFER_CURRENCY_CODE"), rs.getBigDecimal("CREDITED_AMOUNT"), rs.getString("CREDITED_CURRENCY_CODE"), rs.getBigDecimal("RATE"), rs.getString("STATUS"), rs.getDate("CREATED_DT"), rs.getDate("LAST_UPDATED_DT"));
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
     * Process transfer between two accounts
     * @param transfer transfer to be processed
     * @return Transfer.transferResponse
     * @throws Exception e
     */
    public Transfer.transferResponse processTransfer(Transfer transfer) throws Exception {
        log.info("@@@ processTransfer");
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
            insertStmt.setBigDecimal(3, null);
            insertStmt.setString(4, null);
            insertStmt.setBigDecimal(5, transfer.getTransferAmount());
            insertStmt.setString(6, transfer.getTransferCurrencyCode());
            insertStmt.setBigDecimal(7, null);
            insertStmt.setString(8, null);
            insertStmt.setBigDecimal(9, null);

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
            transfer = transactionValidations(transfer);
            log.info("transactionValidations : " + transfer.getResponse().getErrorMessage());

            if (!transfer.getResponse().equals(Transfer.transferResponse.SUCCESS)) {
                log.info("Transfer validation error -- Update transfer record to failed.");
                updateStmt = conn.prepareStatement(UPDATE_TRANSFER);
                updateStmt.setBigDecimal(1, transfer.getDebitedAmount());
                updateStmt.setString(2, transfer.getSourceCurrencyCode());
                updateStmt.setBigDecimal(3, transfer.getCreditedAmount());
                updateStmt.setString(4, transfer.getDestinationCurrencyCode());
                updateStmt.setBigDecimal(5, transfer.getRate());
                updateStmt.setString(6, transfer.getResponse().name());
                updateStmt.setInt(7, transferId);
                updateStmt.executeUpdate();
                conn.commit();
                return transfer.getResponse();
            }

            log.info("Transfer validation success -- Processing transfer.");

            Transfer.transferResponse transferFundResponse = accountDao.transferFund(fromAccount, toAccount, transfer);

            if (!transferFundResponse.equals(Transfer.transferResponse.SUCCESS)) {
                log.info("Transfer fund error -- Update transfer record to failed.");
                updateStmt = conn.prepareStatement(UPDATE_TRANSFER);
                updateStmt.setBigDecimal(1, transfer.getDebitedAmount());
                updateStmt.setString(2, transfer.getSourceCurrencyCode());
                updateStmt.setBigDecimal(3, transfer.getCreditedAmount());
                updateStmt.setString(4, transfer.getDestinationCurrencyCode());
                updateStmt.setBigDecimal(5, transfer.getRate());
                updateStmt.setString(6, transferFundResponse.name());
                updateStmt.setInt(7, transferId);
                updateStmt.executeUpdate();
                conn.commit();
                return transferFundResponse;
            }

            log.info("Transfer fund success -- Update transfer record to success.");

            updateStmt = conn.prepareStatement(UPDATE_TRANSFER);
            updateStmt.setBigDecimal(1, transfer.getDebitedAmount());
            updateStmt.setString(2, transfer.getSourceCurrencyCode());
            updateStmt.setBigDecimal(3, transfer.getCreditedAmount());
            updateStmt.setString(4, transfer.getDestinationCurrencyCode());
            updateStmt.setBigDecimal(5, transfer.getRate());
            updateStmt.setString(6, transferFundResponse.name());
            updateStmt.setInt(7, transferId);
            updateStmt.executeUpdate();
            conn.commit();
            return transferFundResponse;
        } catch(SQLException se) {
            log.severe("@@@ SQLException : " + se.getMessage());
            if (conn != null) {
                conn.rollback();
            }
            throw new SQLException(se);
        } catch(Exception e) {
            log.severe("@@@ Exception : " + e.getMessage() + conn + updateStmt);
            if (conn != null) {
                conn.rollback();
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
    private Transfer transactionValidations(Transfer transfer) throws Exception {
        log.info("transactionValidations");
        Account fromAccount = accountDao.getAccountByAccountNo(transfer.getSourceAccountNo());
        if (fromAccount == null) {
            transfer.setResponse(Transfer.transferResponse.INVALID_FROM_ACC);
            return transfer;
        }
        log.info("Valid Source Account.");
        Account toAccount = accountDao.getAccountByAccountNo(transfer.getDestinationAccountNo());
        if (toAccount == null) {
            transfer.setResponse(Transfer.transferResponse.INVALID_TO_ACC);
            return transfer;
        }
        log.info("Valid Destination Account.");

        //Validating currency codes
        String transferCurrencyCode = transfer.getTransferCurrencyCode();
        try {
            Currency instance = Currency.getInstance(transferCurrencyCode);
            //noinspection ResultOfMethodCallIgnored
            instance.getCurrencyCode().equals(transferCurrencyCode);
        } catch (Exception e) {
            log.info("Invalid currency code : " + transferCurrencyCode);
            transfer.setResponse(Transfer.transferResponse.INVALID_CURRENCY_TRANSFER);
            return transfer;
        }
        log.info("Valid Transfer Currency.");

        String fromCurrencyCode = fromAccount.getCurrencyCode();
        try {
            Currency instance = Currency.getInstance(fromCurrencyCode);
            //noinspection ResultOfMethodCallIgnored
            instance.getCurrencyCode().equals(fromCurrencyCode);
        } catch (Exception e) {
            log.info("Invalid currency code : " + fromCurrencyCode);
            transfer.setResponse(Transfer.transferResponse.INVALID_CURRENCY_FROM_ACC);
            return transfer;
        }
        log.info("Valid Source Account Currency.");
        transfer.setSourceCurrencyCode(fromCurrencyCode);

        String toCurrencyCode = toAccount.getCurrencyCode();
        try {
            Currency instance = Currency.getInstance(toCurrencyCode);
            //noinspection ResultOfMethodCallIgnored
            instance.getCurrencyCode().equals(toCurrencyCode);
        } catch (Exception e) {
            log.info("Invalid currency code : " + toCurrencyCode);
            transfer.setResponse(Transfer.transferResponse.INVALID_CURRENCY_TO_ACC);
            return transfer;
        }
        log.info("Valid Destination Account Currency.");
        transfer.setDestinationCurrencyCode(toCurrencyCode);


        //If the transfer currency doesn't match either accounts currencies : error
        if (!transferCurrencyCode.equalsIgnoreCase(fromCurrencyCode) && !transferCurrencyCode.equalsIgnoreCase(toCurrencyCode)) {
            log.info("Transfer currency doesn't correspond to either account currencies.");
            transfer.setResponse(Transfer.transferResponse.TRANSFER_CURRENCY_MISMATCH);
            return transfer;
        }

        Rate rate;
        if (fromCurrencyCode.equalsIgnoreCase(toCurrencyCode)) {
            log.info("Source and Destination currencies are the same , rate = 1");
            rate = new Rate();
            rate.setRate(new BigDecimal(1).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        } else {
            rate = rateDao.getRateBySourceAndDestCurrency(fromCurrencyCode, toCurrencyCode);
        }
        if (rate == null) {
            log.info("Rate not found for the source and destination currencies");
            transfer.setResponse(Transfer.transferResponse.RATE_NOT_FOUND);
            return transfer;
        }
        log.info("Valid Rate : " + rate.getRate());
        transfer.setRate(rate.getRate());
        BigDecimal debitedAmount;
        BigDecimal creditedAmount;

        if (transferCurrencyCode.equalsIgnoreCase(fromCurrencyCode)) {
            debitedAmount = transfer.getTransferAmount().setScale(2, BigDecimal.ROUND_HALF_EVEN);
        } else {
            debitedAmount = transfer.getTransferAmount().setScale(2, BigDecimal.ROUND_HALF_EVEN).divide(rate.getRate(), 2, BigDecimal.ROUND_HALF_UP);
        }
        if (transferCurrencyCode.equalsIgnoreCase(toCurrencyCode)) {
            creditedAmount = transfer.getTransferAmount().setScale(2, BigDecimal.ROUND_HALF_EVEN);
        } else {
            creditedAmount = transfer.getTransferAmount().setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(rate.getRate().setScale(2, BigDecimal.ROUND_HALF_EVEN));
        }

        //Validating sufficient fund
        BigDecimal newBalance = fromAccount.getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN).subtract(debitedAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
        //check account balance
        if (newBalance.compareTo(new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_EVEN)) < 0) {
            log.info("Insufficient balance on account : " + fromAccount.getAccountNo());
            transfer.setResponse(Transfer.transferResponse.INSUFFICIENT_FUND);
            return transfer;
        }
        log.info("debitedAmount : " + debitedAmount);
        transfer.setDebitedAmount(debitedAmount);
        log.info("creditedAmount : " + creditedAmount);
        transfer.setCreditedAmount(creditedAmount);
        transfer.setResponse(Transfer.transferResponse.SUCCESS);
        log.info("All transfer validations passed.");
        return transfer;
    }
}

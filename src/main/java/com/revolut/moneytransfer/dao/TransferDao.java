package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Transfer;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
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
    private static final String INSERT_TRANSFER = "INSERT INTO TRANSFER values (TRANSFER_SEQ.nextVal, ?, ?, ?, ?, 'PENDING');";
    private static final String UPDATE_PROCESSED_TRANSFER = "UPDATE TRANSFER SET STATUS = 'PROCESSED' WHERE ID = ?";
    private static final String UPDATE_FAILED_TRANSFER = "UPDATE TRANSFER SET STATUS = 'FAILED' WHERE ID = ?";
    private static final String SELECT_BY_TO_ACCOUNT_NO = "SELECT * FROM TRANSFER WHERE TO_ACCOUNT_NO = ?";
    private static final String SELECT_BY_FROM_ACCOUNT_NO = "SELECT * FROM TRANSFER WHERE FROM_ACCOUNT_NO = ?";


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
                Transfer transfer = new Transfer(rs.getInt("ID"), rs.getLong("FROM_ACCOUNT_NO"), rs.getLong("TO_ACCOUNT_NO"), rs.getBigDecimal("AMOUNT"), rs.getString("CURRENCY_CODE"), rs.getString("STATUS"));
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
        List<Transfer> transfersList;

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
            transfersList = new ArrayList<>();
            while (rs.next()) {
                Transfer transfer = new Transfer(rs.getInt("ID"), rs.getLong("FROM_ACCOUNT_NO"), rs.getLong("TO_ACCOUNT_NO"), rs.getBigDecimal("AMOUNT"), rs.getString("CURRENCY_CODE"), rs.getString("STATUS"));
                transfersList.add(transfer);
            }
            return transfersList;
        } catch (SQLException se) {
            log.severe("SQL Exception while executing : " + sqlQuery + " - accountNo : " + accountNo);
            throw new SQLException(se);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            try {
                //Clean-up environment
                if (rs != null)
                    rs.close();
            } catch (SQLException se) {
                log.severe("Unable to close ResultSet : " + se.getMessage());
            }
            // finally block used to close remaining resources
            // end finally try
        }
    }

    public int executeTransfer(Transfer transfer) throws Exception {
        log.info("@@@ executeTransfer");
        int response = 0;
        int transferId = 0;
        Connection conn = null;
        ResultSet rs;
        PreparedStatement insertStmt;
        PreparedStatement updateStmt;

        //Load Driver
        Class.forName(JDBC_DRIVER);

        try{
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //set autocommit false to control the rollback in case of exception
            conn.setAutoCommit(false);


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

            AccountDao accountDao = new AccountDao();
            int fromAccountResponse = accountDao.updateAccountBalance(transfer.getSourceAccountNo(), transfer.getAmount().multiply(new BigDecimal(-1)));
            if(fromAccountResponse == 1) {
                updateStmt = conn.prepareStatement(UPDATE_FAILED_TRANSFER);
                updateStmt.setInt(1, transferId);
                updateStmt.executeUpdate();
                conn.commit();
                return 1;
            }
            else if(fromAccountResponse == 2){
                updateStmt = conn.prepareStatement(UPDATE_FAILED_TRANSFER);
                updateStmt.setInt(1, transferId);
                updateStmt.executeUpdate();
                conn.commit();
                return 4;
            }
            else if(fromAccountResponse == 3){
                updateStmt = conn.prepareStatement(UPDATE_FAILED_TRANSFER);
                updateStmt.setInt(1, transferId);
                updateStmt.executeUpdate();
                conn.commit();
                return 3;
            }
            int toAccountResponse = accountDao.updateAccountBalance(transfer.getDestinationAccountNo(), transfer.getAmount());
            if(toAccountResponse == 1) {
                updateStmt = conn.prepareStatement(UPDATE_FAILED_TRANSFER);
                updateStmt.setInt(1, transferId);
                updateStmt.executeUpdate();
                conn.commit();
                return 2;
            }
            else if(toAccountResponse == 2){
                updateStmt = conn.prepareStatement(UPDATE_FAILED_TRANSFER);
                updateStmt.setInt(1, transferId);
                updateStmt.executeUpdate();
                conn.commit();
                return 5;
            }
            updateStmt = conn.prepareStatement(UPDATE_PROCESSED_TRANSFER);
            updateStmt.setInt(1, transferId);
            updateStmt.executeUpdate();
            conn.commit();
            return response;
        } catch(SQLException se) {
            log.info("@@@ SQLException");
            if (conn != null)
                conn.rollback();
            throw new SQLException(se);
        } catch(Exception e) {
            log.info("@@@ Exception");
            if (conn != null)
                conn.rollback();
            throw new Exception(e);
        }
    }
}

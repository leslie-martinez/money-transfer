package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Account;
import org.apache.commons.dbutils.DbUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.logging.Logger;

/**
 * Account DAO and Implementation Class
 */
public class AccountDao {
    private static final Logger log = Logger.getLogger("AccountDao");
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:moneytransferapp;DB_CLOSE_DELAY=-1";

    //  Database credentials
    private static final String USER = "sa";
    private static final String PASS = "sa";

    // SQL STATEMENTS
    private static final String SELECT_ALL = "SELECT * FROM ACCOUNT";
    private static final String SELECT_BY_ID =  "SELECT * FROM ACCOUNT WHERE ID = ? ";
    private static final String SELECT_BY_ACCOUNT_NO =  "SELECT * FROM ACCOUNT WHERE ACCOUNT_NUMBER = ? ";
    private static final String GET_BALANCE_BY_ACCOUNT_NO =  "SELECT BALANCE FROM ACCOUNT WHERE ACCOUNT_NUMBER = ? ";
    private final static String LOCK_ACCOUNT_BY_NUMBER = "SELECT * FROM ACCOUNT WHERE ACCOUNT_NUMBER = ? FOR UPDATE";
    private final static String UPDATE_ACCOUNT_BALANCE = "UPDATE ACCOUNT SET BALANCE = ? WHERE ACCOUNT_NUMBER = ?";


    /**
     * @return All accounts
     * @throws Exception e
     */
    public List<Account> getAllAccounts() throws Exception {
        List<Account> accountList = null;

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try(Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_ALL)){
            if(rs != null)
                accountList = new ArrayList<>();
            while (rs.next()) {
                Account acc = new Account(rs.getInt("ID"), rs.getInt("ACCOUNT_OWNER_ID"), rs.getLong("ACCOUNT_NUMBER"), rs.getBigDecimal("BALANCE"), rs.getString("CURRENCY_CODE"));
                accountList.add(acc);
            }
            return accountList;
        } catch(SQLException se) {
            log.severe("SQL Exception while executing : " + SELECT_ALL);
            throw new SQLException(se);
        } catch(Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * @param accountId account unique identifier
     * @return Account
     * @throws Exception e
     */
    public Account getAccountById(int accountId) throws Exception {
        log.info("getAccountById : "+ accountId);
        ResultSet rs = null;
        Account account = null;

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try(Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
            PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)){
            stmt.setInt(1, accountId);
            // Execute a query
            rs = stmt.executeQuery();
            if (rs.next()) {
                account = new Account(rs.getInt("ID"), rs.getInt("ACCOUNT_OWNER_ID"), rs.getLong("ACCOUNT_NUMBER"), rs.getBigDecimal("BALANCE"), rs.getString("CURRENCY_CODE"));
            }
            return account;
        } catch(SQLException se) {
            log.severe("SQL Exception while executing : " + SELECT_BY_ID + " - id : " + accountId);
            throw new SQLException(se);
        } catch(Exception e) {
            throw new Exception(e);
        } finally {
            try {
                //Clean-up environment
                if(rs != null)
                    rs.close();
            } catch(SQLException se) {
                // Handle errors for JDBC
                throw new Exception(se);
            }
            // finally block used to close remaining resources
            // end finally try
        }
    }

    /**
     * @param accountNo account number
     * @return Account
     * @throws Exception e
     */
    public Account getAccountByAccountNo(Long accountNo) throws Exception {
        log.info("getAccountByAccountNo : "+ accountNo);
        ResultSet rs = null;
        Account account = null;

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try(Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
            PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ACCOUNT_NO)){
            stmt.setLong(1, accountNo);
            //Execute a query
            rs = stmt.executeQuery();
            if (rs.next()) {
                account = new Account(rs.getInt("ID"), rs.getInt("ACCOUNT_OWNER_ID"), rs.getLong("ACCOUNT_NUMBER"), rs.getBigDecimal("BALANCE"), rs.getString("CURRENCY_CODE"));
            }
            return account;
        } catch(SQLException se) {
            log.severe("SQL Exception while executing : " + SELECT_BY_ID + " - accountNo : " + accountNo);
            throw new SQLException(se);
        } catch(Exception e) {
            throw new Exception(e);
        } finally {
            try {
                //Clean-up environment
                if(rs != null)
                    rs.close();
            } catch(SQLException se) {
                // Handle errors for JDBC
                throw new Exception(se);
            }
            // finally block used to close remaining resources
            // end finally try
        }
    }

    /**
     * @param accountNo account number
     * @return Account Balance
     * @throws Exception e
     */
    public BigDecimal getAccountBalance(Long accountNo) throws Exception {
        log.info("getAccountBalance : "+ accountNo);
        ResultSet rs = null;
        BigDecimal balance = null;

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try(Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
            PreparedStatement stmt = conn.prepareStatement(GET_BALANCE_BY_ACCOUNT_NO)){
            stmt.setLong(1, accountNo);
            //Execute a query
            rs = stmt.executeQuery();
            if (rs.next()) {
                balance = rs.getBigDecimal("BALANCE");
            }
            return balance;
        } catch(SQLException se) {
            log.severe("SQL Exception while executing : " + GET_BALANCE_BY_ACCOUNT_NO + " - accountNo : " + accountNo);
            throw new SQLException(se);
        } catch(Exception e) {
            throw new Exception(e);
        } finally {
            try {
                //Clean-up environment
                if(rs != null)
                    rs.close();
            } catch(SQLException se) {
                // Handle errors for JDBC
                throw new Exception(se);
            }
            // finally block used to close remaining resources
            // end finally try
        }
    }

    /**
     * @param accountNo account number
     * @param amount new account balance
     * @return number of account successfully updated : 0 for failed / 1 for success
     * @throws Exception e
     */
    public int updateAccountBalance(Long accountNo, BigDecimal amount) throws Exception {
        log.info("updateAccountBalance : "+ accountNo + " amount : " + amount);
        ResultSet rs = null;
        int response = 0;
        Connection conn = null;
        PreparedStatement lockStmt = null;
        PreparedStatement updateStmt = null;

        //Load Driver
        Class.forName(JDBC_DRIVER);
        try{
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            //set autocommit false to control the rollback in case of exception
            conn.setAutoCommit(false);

            lockStmt = conn.prepareStatement(LOCK_ACCOUNT_BY_NUMBER);
            lockStmt.setLong(1, accountNo);
            //Execute Lock query
            rs = lockStmt.executeQuery();
            Account account = null;
            if (rs.next()) {
                account = new Account(rs.getInt("ID"), rs.getInt("ACCOUNT_OWNER_ID"), rs.getLong("ACCOUNT_NUMBER"), rs.getBigDecimal("BALANCE"), rs.getString("CURRENCY_CODE"));
            }
            if(account == null){
                return 1;
            }
            //Validating currency code
            String currencyCode = account.getCurrencyCode();
            try {
                Currency instance = Currency.getInstance(currencyCode);
                instance.getCurrencyCode().equals(currencyCode);
            } catch (Exception e) {
                return 2;
            }
            //Validating new amount
            BigDecimal newBalance = account.getBalance().add(amount);
            //On successful lock, check account balance
            if(newBalance.compareTo(new BigDecimal(0)) < 0){
                return 3;
            }
            //On Validation successful, update balance
            updateStmt = conn.prepareStatement(UPDATE_ACCOUNT_BALANCE);
            updateStmt.setBigDecimal(1, newBalance);
            updateStmt.setLong(2, accountNo);
            updateStmt.executeUpdate();
            conn.commit();
        } catch(SQLException se) {
            log.severe("SQL Exception while executing updateAccountBalance : "+ accountNo + " amount : " + amount);
            if(conn != null)
                conn.rollback();
            throw new SQLException(se);
        } catch(Exception e) {
            if(conn != null)
                conn.rollback();
            throw new Exception(e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStmt);
            DbUtils.closeQuietly(updateStmt);
        }
        return response;
    }

    private enum AccountResponse{
        SUCCESS(0),
        INVALID_ACC(1),
        INVALID_CURRENCY(2),
        INSUFFICIENT_FUND(3);

        private int code;

        AccountResponse(int code){
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}

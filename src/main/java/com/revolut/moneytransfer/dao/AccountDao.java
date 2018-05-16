package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.Transfer;
import org.apache.commons.dbutils.DbUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.*;
import java.util.ArrayList;
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
     * Get all accounts list
     * @return List of Accounts
     * @throws Exception e
     */
    public List<Account> getAllAccounts() throws Exception {
        List<Account> accountList = new ArrayList<>();

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try(Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_ALL)){
            if (rs == null)
                throw new SQLException("SQL Exception while executing : " + SELECT_ALL);
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
     * Get account by uid
     * @param accountId account uid
     * @return Account
     * @throws Exception e
     */
    private Account getAccountById(int accountId) throws Exception {
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
            DbUtils.closeQuietly(rs);
            // finally block used to close remaining resources
            // end finally try
        }
    }

    /**
     * Get account by unique account number
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
            DbUtils.closeQuietly(rs);
            // finally block used to close remaining resources
            // end finally try
        }
    }

    /**
     * Get account balance by unique account number
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
            DbUtils.closeQuietly(rs);
            // finally block used to close remaining resources
            // end finally try
        }
    }

    /**
     * Lock account by account number for update
     * @param accountNo Account number to be locked
     * @return Account locked for update
     * @throws Exception e
     */
    protected Account lockAccountByNumber(Long accountNo) throws Exception {
        log.info("lockAccountByNumber : " + accountNo);
        ResultSet rs = null;
        BigDecimal balance = null;

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement lockStmt = conn.prepareStatement(LOCK_ACCOUNT_BY_NUMBER)) {

            lockStmt.setLong(1, accountNo);
            //Execute Lock query
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("ID"), rs.getInt("ACCOUNT_OWNER_ID"), rs.getLong("ACCOUNT_NUMBER"), rs.getBigDecimal("BALANCE"), rs.getString("CURRENCY_CODE"));
            }
            return null;
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            DbUtils.closeQuietly(rs);
        }

    }

    /**
     * Proceed with the fund transfer - to be called after verification and accouts lock for update
     *
     * @param fromAccount  source account
     * @param toAccount    destination account
     * @param amount       transaction amount
     * @param currencyCode transation currency code
     * @return Transfer.transferResponse
     * @throws Exception e
     */
    public Transfer.transferResponse transferFund(Account fromAccount, Account toAccount, BigDecimal amount, String currencyCode) throws Exception {
        log.info("transferFund from : " + fromAccount.getAccountNo() + " to : " + toAccount.getAccountNo() + " of amount : " + amount + currencyCode);
        Connection conn = null;
        PreparedStatement updateStmt = null;

        //TODO change currency rate by real calculation
        BigDecimal sourceCurrencyRate = new BigDecimal(Math.random(), MathContext.DECIMAL64); // based on fromCurrencyCode to transfer currencyCode
        log.info("sourceCurrencyRate : " + sourceCurrencyRate);
        BigDecimal destinationCurrencyRate = new BigDecimal(Math.random(), MathContext.DECIMAL64);// based on transfer currencyCode to toCurrencyCode
        log.info("destinationCurrencyRate : " + destinationCurrencyRate);

        //Load Driver
        Class.forName(JDBC_DRIVER);
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            //set autocommit false to control the rollback in case of exception
            conn.setAutoCommit(false);
            updateStmt = conn.prepareStatement(UPDATE_ACCOUNT_BALANCE);

            //Converting currency for Source account
            String fromCurrencyCode = fromAccount.getCurrencyCode();
            BigDecimal amountToDeduct = amount.multiply(sourceCurrencyRate);
            BigDecimal sourceNewBalance = fromAccount.getBalance().subtract(amountToDeduct);

            //Proceed with update source account
            updateStmt.setBigDecimal(1, sourceNewBalance);
            updateStmt.setLong(2, fromAccount.getAccountNo());
            updateStmt.addBatch();

            //Converting currency for Destination account
            //TODO check that transfer currency is either equal to source or destination currency
            String toCurrencyCode = toAccount.getCurrencyCode();
            BigDecimal amountToCredit = amount.multiply(destinationCurrencyRate);
            BigDecimal destinationNewBalance = toAccount.getBalance().add(amountToCredit);

            //Proceed with update destination account
            updateStmt.setBigDecimal(1, destinationNewBalance);
            updateStmt.setLong(2, toAccount.getAccountNo());
            updateStmt.addBatch();

            //Execute batch update
            updateStmt.executeBatch();

            //Commit DB transaction
            conn.commit();
        } catch (SQLException se) {
            log.severe("SQL Exception while transferring fund from : " + fromAccount.getAccountNo() + " to : " + toAccount.getAccountNo() + " of amount : " + amount + currencyCode);
            if (conn != null)
                conn.rollback();
            throw new SQLException(se);
        } catch (Exception e) {
            if (conn != null)
                conn.rollback();
            throw new Exception(e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(updateStmt);
        }
        return null;
    }
}

package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Customer;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Customer DAO and Implementation Class
 */
public class CustomerDao {
    private static final Logger log = Logger.getLogger("CustomerDao");
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:moneytransferapp;DB_CLOSE_DELAY=-1";

    //  Database credentials
    private static final String USER = "sa";
    private static final String PASS = "sa";

    private static final String SELECT_ALL = "SELECT * FROM CUSTOMERS";
    private static final String SELECT_BY_ACCOUNT_NO = "SELECT CUSTOMERS.* FROM CUSTOMERS LEFT JOIN ACCOUNTS ON ACCOUNTS.CUSTOMER_ID = CUSTOMERS.ID WHERE ACCOUNTS.ACCOUNT_NUMBER = ?";
    private static final String SELECT_BY_FROM_ACCOUNT_NO = "SELECT * FROM CUSTOMERS WHERE FROM_ACCOUNT_NO = ?";


    /**
     * Get customer by account no
     *
     * @param accountNo account no
     * @return customer
     * @throws Exception e
     */
    public Customer getCustomerByAccountNo(Long accountNo) throws Exception {
        log.info("getCustomersByAccountNo : " + accountNo);
        ResultSet rs = null;

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ACCOUNT_NO)) {
            stmt.setLong(1, accountNo);
            // Execute a query
            rs = stmt.executeQuery();
            Customer customer = null;
            if (rs.next()) {
                customer = new Customer(rs.getInt("ID"), rs.getString("NAME"), rs.getString("ADDRESS"), rs.getDate("CREATED_DT"), rs.getDate("LAST_UPDATED_DT"));
            }
            return customer;
        } catch (SQLException se) {
            log.severe("SQL Exception while executing : " + SELECT_BY_ACCOUNT_NO + " - accountNo : " + accountNo);
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
     * Get All customers
     *
     * @return Customers
     * @throws Exception e
     */
    public List<Customer> getAllCustomers() throws Exception {

        List<Customer> customersList = new ArrayList<>();

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            if (rs == null)
                throw new SQLException("SQL Exception while executing : " + SELECT_ALL);
            while (rs.next()) {
                Customer customer = new Customer(rs.getInt("ID"), rs.getString("NAME"), rs.getString("ADDRESS"), rs.getDate("CREATED_DT"), rs.getDate("LAST_UPDATED_DT"));
                customersList.add(customer);
            }
            return customersList;
        } catch (SQLException se) {
            log.severe("SQL Exception while executing : " + SELECT_ALL);
            throw new SQLException(se);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}

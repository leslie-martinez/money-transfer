package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.User;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserDao {
    private static final Logger log = Logger.getLogger("UserDao");
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:moneytransferapp;DB_CLOSE_DELAY=-1";

    //  Database credentials
    private static final String USER = "sa";
    private static final String PASS = "sa";

    private static final String SELECT_ALL = "SELECT * FROM ACCOUNT_USER";
    private static final String SELECT_BY_ACCOUNT_NO = "SELECT ACCOUNT_USER.* FROM ACCOUNT_USER LEFT JOIN ACCOUNT ON ACCOUNT.ACCOUNT_OWNER_ID = ACCOUNT_USER.ID WHERE ACCOUNT.ACCOUNT_NUMBER = ?";
    private static final String SELECT_BY_FROM_ACCOUNT_NO = "SELECT * FROM ACCOUNT_USER WHERE FROM_ACCOUNT_NO = ?";


    public User getUserByAccountNo(Long accountNo) throws Exception {
        log.info("getUsersByUserNo : " + accountNo);
        ResultSet rs = null;

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ACCOUNT_NO)) {
            stmt.setLong(1, accountNo);
            // Execute a query
            rs = stmt.executeQuery();
            User user = null;
            if (rs.next()) {
                user = new User(rs.getInt("ID"), rs.getString("NAME"), rs.getString("ADDRESS"));
            }
            return user;
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

    public List<User> getAllUsers() throws Exception {

        List<User> usersList = new ArrayList<>();

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            if (rs == null)
                throw new SQLException("SQL Exception while executing : " + SELECT_ALL);
            while (rs.next()) {
                User user = new User(rs.getInt("ID"), rs.getString("NAME"), rs.getString("ADDRESS"));
                usersList.add(user);
            }
            return usersList;
        } catch (SQLException se) {
            log.severe("SQL Exception while executing : " + SELECT_ALL);
            throw new SQLException(se);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}

package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Rate;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Rate DAO and Implementation Class
 */
public class RateDao {
    private static final Logger log = Logger.getLogger("RateDao");
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:moneytransferapp;DB_CLOSE_DELAY=-1";

    //  Database credentials
    private static final String USER = "sa";
    private static final String PASS = "sa";

    // SQL STATEMENTS
    private static final String SELECT_ALL = "SELECT * FROM RATES";
    private static final String SELECT_ALL_EFFECTIVE = "SELECT * FROM RATES WHERE ? >= EFFECTIVE_DT";
    private static final String SELECT_RATE_BY_ID = "SELECT * FROM RATES WHERE ID = ?";
    private static final String SELECT_BY_ID = "SELECT * FROM RATES WHERE ID = ? ";
    private static final String SELECT_BY_SOURCE_DEST_CURRENCY = "SELECT * FROM RATES WHERE SOURCE_CURRENCY_CODE = ? AND DESTINATION_CURRENCY_CODE = ? AND ? >= EFFECTIVE_DT ORDER BY EFFECTIVE_DT DESC";
    private static final String UPDATE_RATE_BY_ID = "UPDATE RATES SET RATE = ?, LAST_UPDATED_DT = SYSDATE, EFFECTIVE_DT = ? WHERE ID = ? ";

    /**
     * Get all rates list
     *
     * @return List of Rates
     * @throws Exception e
     */
    public List<Rate> getAllRates() throws Exception {
        List<Rate> ratesList = new ArrayList<>();

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            if (rs == null)
                throw new SQLException("SQL Exception while executing : " + SELECT_ALL);
            while (rs.next()) {
                Rate rate = new Rate(rs.getInt("ID"), rs.getString("SOURCE_CURRENCY_CODE"), rs.getString("DESTINATION_CURRENCY_CODE"), rs.getBigDecimal("RATE"), rs.getDate("EFFECTIVE_DT"), rs.getDate("CREATED_DT"), rs.getDate("LAST_UPDATED_DT"));
                ratesList.add(rate);
            }
            return ratesList;
        } catch (SQLException se) {
            log.severe("SQL Exception while executing : " + SELECT_ALL);
            throw new SQLException(se);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Get all rates effective list
     *
     * @return List of Rates
     * @throws Exception e
     */
    public List<Rate> getAllEffectiveRates() throws Exception {
        List<Rate> ratesList = new ArrayList<>();
        ResultSet rs = null;

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_EFFECTIVE)) {
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            // Execute a query
            rs = stmt.executeQuery();
            if (rs == null)
                throw new SQLException("SQL Exception while executing : " + SELECT_ALL_EFFECTIVE);
            while (rs.next()) {
                Rate rate = new Rate(rs.getInt("ID"), rs.getString("SOURCE_CURRENCY_CODE"), rs.getString("DESTINATION_CURRENCY_CODE"), rs.getBigDecimal("RATE"), rs.getDate("EFFECTIVE_DT"), rs.getDate("CREATED_DT"), rs.getDate("LAST_UPDATED_DT"));
                ratesList.add(rate);
            }
            return ratesList;
        } catch (SQLException se) {
            log.severe("SQL Exception while executing : " + SELECT_ALL_EFFECTIVE);
            throw new SQLException(se);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            DbUtils.closeQuietly(rs);
        }
    }

    /**
     * Get the effective rate by source and destination currency
     *
     * @param sourceCurrencyCode source currency code
     * @param destCurrencyCode   destination currency code
     * @return Rate
     * @throws Exception e
     */
    public Rate getRateBySourceAndDestCurrency(String sourceCurrencyCode, String destCurrencyCode) throws Exception {
        log.info("getRateById : " + sourceCurrencyCode + " to " + destCurrencyCode);
        ResultSet rs = null;
        Rate account = null;

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_SOURCE_DEST_CURRENCY)) {
            stmt.setString(1, sourceCurrencyCode);
            stmt.setString(2, destCurrencyCode);
            // Get only the effective rates
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            // Execute a query
            rs = stmt.executeQuery();

            // Only Take the first one , latest effective
            if (rs.next()) {
                account = new Rate(rs.getInt("ID"), rs.getString("SOURCE_CURRENCY_CODE"), rs.getString("DESTINATION_CURRENCY_CODE"), rs.getBigDecimal("RATE"), rs.getDate("EFFECTIVE_DT"), rs.getDate("CREATED_DT"), rs.getDate("LAST_UPDATED_DT"));
            }
            return account;
        } catch (SQLException se) {
            log.severe("SQL Exception while executing : " + SELECT_BY_SOURCE_DEST_CURRENCY + " - source : " + sourceCurrencyCode + " - dest : " + destCurrencyCode);
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
     * Update currency rate
     *
     * @param rateId rate uid
     * @param rate   rate object
     * @return updated rate object
     * @throws Exception e
     */
    public Rate updateCurrencyRate(Long rateId, Rate rate) throws Exception {
        ResultSet rs;

        //Load Driver
        Class.forName(JDBC_DRIVER);

        // Try with resource to ensure resources are closed on exit
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(UPDATE_RATE_BY_ID)) {
            conn.setAutoCommit(false);
            stmt.setBigDecimal(1, rate.getRate());
            stmt.setDate(2, rate.getEffectiveDt());
            stmt.setLong(3, rateId);
            int update = stmt.executeUpdate();
            log.info("@@@ " + update + " rate updated.");
            if (update != 1) {
                conn.rollback();
                throw new Exception("Update rate failed.");
            }
            conn.commit();
            PreparedStatement selectStmt = conn.prepareStatement(SELECT_BY_ID);
            selectStmt.setLong(1, rateId);
            rs = selectStmt.executeQuery();
            if (rs == null)
                throw new SQLException("SQL Exception while executing : " + UPDATE_RATE_BY_ID);
            if (rs.next()) {
                rate = new Rate(rs.getInt("ID"), rs.getString("SOURCE_CURRENCY_CODE"), rs.getString("DESTINATION_CURRENCY_CODE"), rs.getBigDecimal("RATE"), rs.getDate("EFFECTIVE_DT"), rs.getDate("CREATED_DT"), rs.getDate("LAST_UPDATED_DT"));
            }
        } catch (SQLException se) {
            log.severe("SQL Exception while executing : " + UPDATE_RATE_BY_ID);
            throw new SQLException(se);
        } catch (Exception e) {
            throw new Exception(e);
        }
        return rate;
    }
}

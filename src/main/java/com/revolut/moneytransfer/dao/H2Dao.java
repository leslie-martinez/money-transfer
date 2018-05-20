package com.revolut.moneytransfer.dao;

import org.apache.commons.dbutils.DbUtils;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class H2Dao {
    static final private Logger log = Logger.getLogger("H2Dao");
    // JDBC driver name and database URL
    static final private String JDBC_DRIVER = "org.h2.Driver";
    static final private String DB_URL = "jdbc:h2:mem:moneytransferapp;DB_CLOSE_DELAY=-1";

    //  Database credentials
    static final private String USER = "sa";
    static final private String PASS = "sa";

    private final CustomerDao customerDao = new CustomerDao();
    private final AccountDao accountDao = new AccountDao();
    private final TransferDao transferDao = new TransferDao();
    private final RateDao rateDao = new RateDao();


    public H2Dao() {
        // init: load driver
        log.info("Initializing Driver ...");
        DbUtils.loadDriver(JDBC_DRIVER);
        log.info("... Driver Initialisation completed.");
    }

    private static Connection getConnection() throws SQLException {
        log.info("Connexion to H2 database");
        return DriverManager.getConnection(DB_URL, USER, PASS);

    }

    public CustomerDao getCustomerDao() {
        return customerDao;
    }

    public AccountDao getAccountDAO() {
        return accountDao;
    }

    public TransferDao getTransferDAO() {
        return transferDao;
    }

    public RateDao getRateDao() {
        return rateDao;
    }


    public void loadH2Database() {
        log.info("Start loadH2Database ...");
        Connection conn = null;
        try {
            log.info("Connexion to H2 database ...");
            conn = H2Dao.getConnection();
            log.info("... Connexion established.");
            log.info("Execution of Loading script ...");
            RunScript.execute(conn, new FileReader("src/main/resources/db.sql"));
            log.info("... Loading script executed successfully.");
        } catch (SQLException e) {
            log.severe("Error while executing loading script.");
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            log.severe("Loading script not found.");
            throw new RuntimeException(e);
        } finally {
            log.info("Closing H2 database connexion ...");
            DbUtils.closeQuietly(conn);
            log.info("... Connexion closed quietly.");
        }
    }
}

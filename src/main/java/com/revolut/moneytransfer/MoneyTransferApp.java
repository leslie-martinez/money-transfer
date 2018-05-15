package com.revolut.moneytransfer;

import com.revolut.moneytransfer.dao.H2Dao;
import com.revolut.moneytransfer.service.AccountService;
import com.revolut.moneytransfer.service.ServiceExceptionMapper;
import com.revolut.moneytransfer.service.TransferService;
import com.revolut.moneytransfer.service.UserService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.omg.IOP.TransactionService;

import java.util.logging.Logger;

/**
 * Money Transfer App
 */
class MoneyTransferApp
{

    private static final Logger log = Logger.getLogger("MoneyTransferApp");

    /**
     * MoneyTransferApp Main method
     * Load test data in H2 database and start embedded server
     * @param args main arguments
     * @throws Exception e
     */
    public static void main( String[] args ) throws Exception {
        // Initialize H2 database with demo data
        log.info("Configure demo .....");
        H2Dao h2Dao = new H2Dao();
        h2Dao.loadH2Database();
        log.info("Configuration Ended....");
        // Host service on jetty
        startRestFulApp();
    }


    /**
     * Start the embedded Server and deploy REST API
     * @throws Exception e
     */
    private static void startRestFulApp() throws Exception {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                    UserService.class.getCanonicalName() + ","
                        + AccountService.class.getCanonicalName() + ","
                        + TransferService.class.getCanonicalName() + ","
                        + ServiceExceptionMapper.class.getCanonicalName() + ","
                        + TransactionService.class.getCanonicalName());
        try {
            server.start();
            server.join();
        } finally {
            server.stop();
            server.destroy();
        }
    }
}

package com.revolut.moneytransfer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.moneytransfer.dao.H2Dao;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class TestService {
    private static final H2Dao h2Dao = new H2Dao();
    private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
    static HttpClient client;
    private static Server server = null;
    final ObjectMapper mapper = new ObjectMapper();
    final URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost:8080");

    @BeforeClass
    public static void setUp() throws Exception {
        //Prepare in memory database
        //data loaded from db.sql file
        h2Dao.loadH2Database();
        //Start server
        startRestFulApp();
        connManager.setDefaultMaxPerRoute(100);
        connManager.setMaxTotal(200);
        client = HttpClients.custom()
                .setConnectionManager(connManager)
                .setConnectionManagerShared(true)
                .build();
    }

    @AfterClass
    public static void tearDown() {
        HttpClientUtils.closeQuietly(client);
    }


    /**
     * Start the embedded Server and deploy REST API
     *
     * @throws Exception e
     */
    private static void startRestFulApp() throws Exception {
        if (server == null) {
            server = new Server(8080);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);
            ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
            servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                    CustomerService.class.getCanonicalName() + "," +
                            AccountService.class.getCanonicalName() + "," +
                            ServiceExceptionMapper.class.getCanonicalName() + "," +
                            RateService.class.getCanonicalName() + "," +
                            TransferService.class.getCanonicalName());
            server.start();
        }
    }
}

package com.revolut.moneytransfer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Before;
import org.junit.Test;

public class AccountServiceTest {
    private static Server server;

    @Before
    public void setUp() throws Exception{
        if(server == null)
            server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        server.start();
    }

    /*@After
    public void tearDown() throws Exception{
        server.
    }*/

    @Test
    public void testGetAccounts(){
    }
}

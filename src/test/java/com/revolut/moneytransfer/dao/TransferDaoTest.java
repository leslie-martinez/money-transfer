package com.revolut.moneytransfer.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TransferDaoTest {
    private static final H2Dao h2Dao = new H2Dao();
    private static final TransferDao transferDao = h2Dao.getTransferDAO();

    @Before
    public void setUp() {
        //Prepare in memory database
        //data loaded from db.sql file
        h2Dao.loadH2Database();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getAllTransfers() {
    }

    @Test
    public void getTransfersByAccountNo() {
    }

    @Test
    public void processTransfer() {
    }
}
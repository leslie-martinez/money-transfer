package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.H2Dao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TransferServiceTest {
    private static final H2Dao h2Dao = new H2Dao();

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
    public void getTransfers() {
    }

    @Test
    public void getTransfersByToAccountNo() {
    }

    @Test
    public void getTransfersByFromAccountNo() {
    }

    @Test
    public void transferAmount() {
    }
}
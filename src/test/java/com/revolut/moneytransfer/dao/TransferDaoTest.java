package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Transfer;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransferDaoTest {
    private static final H2Dao h2Dao = new H2Dao();
    private static final TransferDao transferDao = h2Dao.getTransferDAO();

    @Before
    public void setUp() {
        //Prepare in memory database
        //data loaded from db.sql file
        h2Dao.loadH2Database();
    }

    @Test
    public void getAllTransfers() {
        List<Transfer> allTransfers = null;
        try {
            allTransfers = transferDao.getAllTransfers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(Objects.requireNonNull(allTransfers).size() >= 1);
    }

    @Test
    public void getValidTransfersByAccountNo() {
        List<Transfer> allTransfers = null;
        try {
            //Using String + Long.parseLong because literal numbers in java are by default ints
            //Range -2147483648 to  2147483647 inclusive (too small for this case)
            String accountNoStr = "12345678901";
            long accountNo = Long.parseLong(accountNoStr);
            allTransfers = transferDao.getTransfersByAccountNo(accountNo, "FROM");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(Objects.requireNonNull(allTransfers).size() >= 1);
    }

    @Test
    public void getInvalidAccountTransfersByAccountNo() {
        List<Transfer> allTransfers = null;
        try {
            //Using String + Long.parseLong because literal numbers in java are by default ints
            //Range -2147483648 to  2147483647 inclusive (too small for this case)
            String accountNoStr = "12345678900";
            long accountNo = Long.parseLong(accountNoStr);
            allTransfers = transferDao.getTransfersByAccountNo(accountNo, "FROM");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(0, Objects.requireNonNull(allTransfers).size());
    }

    @Test
    public void getValidAccountNoRecordTransfersByAccountNo() {
        List<Transfer> allTransfers = null;
        try {
            //Using String + Long.parseLong because literal numbers in java are by default ints
            //Range -2147483648 to  2147483647 inclusive (too small for this case)
            String accountNoStr = "90123456789";
            long accountNo = Long.parseLong(accountNoStr);
            allTransfers = transferDao.getTransfersByAccountNo(accountNo, "FROM");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(0, Objects.requireNonNull(allTransfers).size());
    }

    @Test
    public void validProcessTransfer() throws Exception {
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "12345678901";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "23456789012";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, new BigDecimal(200), "EUR");
        Transfer.transferResponse response = transferDao.processTransfer(transfer);
        assertEquals(Transfer.transferResponse.SUCCESS, response);
    }

    @Test
    public void invalidFromAccountProcessTransfer() throws Exception {
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "512345678901";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "23456789012";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, new BigDecimal(200), "EUR");
        Transfer.transferResponse response = transferDao.processTransfer(transfer);
        assertEquals(Transfer.transferResponse.INVALID_FROM_ACC, response);
    }

    @Test
    public void invalidToAccountProcessTransfer() throws Exception {
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "12345678901";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "234567890126";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, new BigDecimal(200), "EUR");
        Transfer.transferResponse response = transferDao.processTransfer(transfer);
        assertEquals(Transfer.transferResponse.INVALID_TO_ACC, response);
    }

    @Test
    public void insufficientFundProcessTransfer() throws Exception {
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "12345678901";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "23456789012";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, new BigDecimal(100000), "EUR");
        Transfer.transferResponse response = transferDao.processTransfer(transfer);
        assertEquals(Transfer.transferResponse.INSUFFICIENT_FUND, response);
    }

    @Test
    public void invalidTransferCurrencyProcessTransfer() throws Exception {
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "12345678901";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "23456789012";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, new BigDecimal(100), "aaa");
        Transfer.transferResponse response = transferDao.processTransfer(transfer);
        assertEquals(Transfer.transferResponse.INVALID_CURRENCY_TRANSFER, response);
    }

    @Test
    public void currencyMismatchProcessTransfer() throws Exception {
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "12345678901";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "23456789012";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, new BigDecimal(100), "CHF");
        Transfer.transferResponse response = transferDao.processTransfer(transfer);
        assertEquals(Transfer.transferResponse.TRANSFER_CURRENCY_MISMATCH, response);
    }

    @Test
    public void rateNotFoundProcessTransfer() throws Exception {
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "800";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "23456789012";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, new BigDecimal(100), "USD");
        Transfer.transferResponse response = transferDao.processTransfer(transfer);
        assertEquals(Transfer.transferResponse.RATE_NOT_FOUND, response);
    }

    @Test
    public void invalidFromCurrencyProcessTransfer() throws Exception {
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "900";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "23456789012";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, new BigDecimal(100), "USD");
        Transfer.transferResponse response = transferDao.processTransfer(transfer);
        assertEquals(Transfer.transferResponse.INVALID_CURRENCY_FROM_ACC, response);
    }

    @Test
    public void invalidToCurrencyProcessTransfer() throws Exception {
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "12345678901";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "900";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, new BigDecimal(100000), "EUR");
        Transfer.transferResponse response = transferDao.processTransfer(transfer);
        assertEquals(Transfer.transferResponse.INVALID_CURRENCY_TO_ACC, response);
    }
}
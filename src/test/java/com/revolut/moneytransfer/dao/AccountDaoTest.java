package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.Transfer;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class AccountDaoTest {
    private static final H2Dao h2Dao = new H2Dao();
    private static final AccountDao accountDAO = h2Dao.getAccountDAO();
    @Before
    public void setUp() {
        //Prepare in memory database
        //data loaded from db.sql file
        h2Dao.loadH2Database();
    }

    @Test
    public void getAllAccounts() {
        List<Account> allAccounts = null;
        try {
            allAccounts = accountDAO.getAllAccounts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(Objects.requireNonNull(allAccounts).size() > 1);
    }

    @Test
    public void getExistingAccountByAccountNo() {
        Account account = null;
        try {
            //Using String + Long.parseLong because literal numbers in java are by default ints
            //Range -2147483648 to  2147483647 inclusive (too small for this case)
            String accountNoStr = "01234567890";
            long accountNo = Long.parseLong(accountNoStr);
            account = accountDAO.getAccountByAccountNo(accountNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(account);
        assertEquals("EUR", account.getCurrencyCode());
        assertEquals(1, account.getAccountOwnerId());
    }

    @Test
    public void getNonExistingAccountByAccountNo() {
        Account account = null;
        try {
            account = accountDAO.getAccountByAccountNo(100L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNull(account);
    }

    @Test
    public void getNonExistingAccountBalance() {
        BigDecimal balance = null;
        try {
            balance = accountDAO.getAccountBalance(100L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNull(balance);
    }

    @Test
    public void getExistingAccountBalance() {
        BigDecimal balance = null;
        try {
            //Using String + Long.parseLong because literal numbers in java are by default ints
            //Range -2147483648 to  2147483647 inclusive (too small for this case)
            String accountNoStr = "01234567890";
            long accountNo = Long.parseLong(accountNoStr);
            balance = accountDAO.getAccountBalance(accountNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(balance);
        balance = balance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(new BigDecimal(1351.12).setScale(2, BigDecimal.ROUND_HALF_EVEN), balance);
    }

    @Test
    public void nonExistingLockAccountByNumber() {
        Account account = null;
        try {
            account = accountDAO.lockAccountByNumber(100L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNull(account);
    }

    @Test
    public void existingLockAccountByNumber() {
        Account account = null;
        try {
            //Using String + Long.parseLong because literal numbers in java are by default ints
            //Range -2147483648 to  2147483647 inclusive (too small for this case)
            String accountNoStr = "89012345678";
            long accountNo = Long.parseLong(accountNoStr);
            account = accountDAO.lockAccountByNumber(accountNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(account);
        assertEquals("SGD", account.getCurrencyCode());
        assertEquals(3, account.getAccountOwnerId());
    }

    @Test
    public void transferFund() {
        Transfer.transferResponse response = null;
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "12345678901";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "23456789012";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        Transfer transfer = new Transfer(1, fromAccountNo, toAccountNo, new BigDecimal(200), "EUR", new BigDecimal(200), "EUR", new BigDecimal(235.81), "USD", new BigDecimal(1.18), Transfer.transferResponse.SUCCESS.name(), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()));
        Account fromAccount = new Account(1, 1, fromAccountNo, new BigDecimal(500.57), "EUR", new Date(System.currentTimeMillis()), null);
        Account toAccount = new Account(2, 1, toAccountNo, new BigDecimal(909.40), "USD", new Date(System.currentTimeMillis()), null);
        try {
            response = accountDAO.transferFund(fromAccount, toAccount, transfer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(Transfer.transferResponse.SUCCESS, response);
    }

    @Test
    public void deleteValidAccount() {
        Account.accountResponse response = null;
        try {
            response = accountDAO.deleteAccount(700L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(response);
        assertEquals(Account.accountResponse.SUCCESS, response);
    }

    @Test
    public void deleteInvalidAccount() {
        Account.accountResponse response = null;
        try {
            response = accountDAO.deleteAccount(100L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(response);
        assertEquals(Account.accountResponse.ACCOUNT_NOT_FOUND, response);
    }

    @Test
    public void deleteInvalidBalanceAccount() {
        Account.accountResponse response = null;
        try {
            response = accountDAO.deleteAccount(600L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(response);
        assertEquals(Account.accountResponse.BALANCE_NOT_ZERO, response);
    }
}
package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Customer;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class CustomerDaoTest {
    private static final H2Dao h2Dao = new H2Dao();
    private static final CustomerDao customerDao = h2Dao.getCustomerDao();

    @Before
    public void setUp() {
        //Prepare in memory database
        //data loaded from db.sql file
        h2Dao.loadH2Database();
    }

    @Test
    public void getValidCustomerByAccountNo() {
        Customer customer = null;
        try {
            //Using String + Long.parseLong because literal numbers in java are by default ints
            //Range -2147483648 to  2147483647 inclusive (too small for this case)
            String accountNoStr = "12345678901";
            long accountNo = Long.parseLong(accountNoStr);
            customer = customerDao.getCustomerByAccountNo(accountNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(customer);
    }

    @Test
    public void getInvalidCustomerByAccountNo() {
        Customer customer = null;
        try {
            //Using String + Long.parseLong because literal numbers in java are by default ints
            //Range -2147483648 to  2147483647 inclusive (too small for this case)
            String accountNoStr = "123456789014";
            long accountNo = Long.parseLong(accountNoStr);
            customer = customerDao.getCustomerByAccountNo(accountNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNull(customer);
    }

    @Test
    public void getAllCustomers() {
        List<Customer> allCustomers = null;
        try {
            allCustomers = customerDao.getAllCustomers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(Objects.requireNonNull(allCustomers).size() >= 1);
    }
}
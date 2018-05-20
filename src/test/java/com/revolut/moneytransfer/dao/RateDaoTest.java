package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Rate;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class RateDaoTest {
    private static final H2Dao h2Dao = new H2Dao();
    private static final RateDao rateDao = h2Dao.getRateDao();

    @Before
    public void setUp() {
        //Prepare in memory database
        //data loaded from db.sql file
        h2Dao.loadH2Database();
    }

    @Test
    public void getAllRates() {
        List<Rate> allRates = null;
        try {
            allRates = rateDao.getAllRates();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(Objects.requireNonNull(allRates).size() >= 1);
    }

    @Test
    public void getAllEffectiveRates() {
        List<Rate> allRates = null;
        try {
            allRates = rateDao.getAllEffectiveRates();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(Objects.requireNonNull(allRates).size() >= 1);
    }

    @Test
    public void getValidRateBySourceAndDestCurrency() {
        Rate rate = null;
        try {
            rate = rateDao.getRateBySourceAndDestCurrency("EUR", "SGD");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(rate);
    }

    @Test
    public void getInvalidRateBySourceAndDestCurrency() {
        Rate rate = null;
        try {
            rate = rateDao.getRateBySourceAndDestCurrency("EUR", "CUC");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNull(rate);
    }
}
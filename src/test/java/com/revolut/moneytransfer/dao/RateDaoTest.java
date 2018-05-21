package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Rate;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;
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

    @Test
    public void updateCurrencyRate() {
        Rate rate = new Rate();
        rate.setRate(new BigDecimal(2.34));
        rate.setEffectiveDt(new Date(1L));
        Rate newRate = null;
        try {
            newRate = rateDao.updateCurrencyRate(1L, rate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(newRate);
        assertEquals(new BigDecimal(2.34).setScale(2, BigDecimal.ROUND_HALF_EVEN), newRate.getRate().setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }

    @Test
    public void updateInvalidCurrencyRate() {
        Rate rate = new Rate();
        Rate newRate = null;
        rate.setRate(new BigDecimal(2.34));
        rate.setEffectiveDt(new Date(1L));
        try {
            newRate = rateDao.updateCurrencyRate(100L, rate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNull(newRate);
    }
}
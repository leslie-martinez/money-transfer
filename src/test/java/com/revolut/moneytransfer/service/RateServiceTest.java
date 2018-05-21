package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.model.Rate;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.sql.Date;

import static org.junit.Assert.*;

public class RateServiceTest extends TestService {

    @Test
    public void getRates() throws Exception {
        URI uri = builder.setPath("/rates").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Rate[] rates = mapper.readValue(json, Rate[].class);
        assertTrue(rates.length > 0);
    }

    @Test
    public void getEffectiveRates() throws Exception {
        URI uri = builder.setPath("/rates/effective").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Rate[] rates = mapper.readValue(json, Rate[].class);
        assertTrue(rates.length > 0);
    }

    @Test
    public void getValidRateBySourceAndDestCurrency() throws Exception {
        URI uri = builder.setPath("/rates/query")
                .setParameter("sourceCurrency", "EUR")
                .setParameter("destinationCurrency", "SGD")
                .build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Rate rate = mapper.readValue(json, Rate.class);
        assertNotNull(rate);
    }

    @Test
    public void getInvalidRateBySourceAndDestCurrency() throws Exception {
        URI uri = builder.setPath("/rates/query")
                .setParameter("sourceCurrency", "EUR")
                .setParameter("destinationCurrency", "CUC")
                .build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        assertEquals("No rate found, for EUR to CUC", json);
    }

    @Test
    public void updateValidCurrencyRate() throws Exception {
        URI uri = builder.setPath("/rates/1").build();
        Rate rate = new Rate();
        rate.setRate(new BigDecimal(2.34));
        rate.setEffectiveDt(new Date(1L));
        String jsonInString = mapper.writeValueAsString(rate);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Rate newRate = mapper.readValue(json, Rate.class);
        assertNotNull(newRate);
        assertEquals(new BigDecimal(2.34).setScale(2, BigDecimal.ROUND_HALF_EVEN), newRate.getRate().setScale(2, BigDecimal.ROUND_HALF_EVEN));

    }

    @Test
    public void updateInvalidCurrencyRate() throws Exception {
        URI uri = builder.setPath("/rates/100").build();
        Rate rate = new Rate();
        rate.setRate(new BigDecimal(2.34));
        rate.setEffectiveDt(new Date(1L));
        String jsonInString = mapper.writeValueAsString(rate);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(500, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        assertEquals("Update rate failed.", json);

    }
}
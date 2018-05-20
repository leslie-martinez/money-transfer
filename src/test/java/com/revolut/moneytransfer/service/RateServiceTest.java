package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.model.Rate;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.net.URI;

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
}
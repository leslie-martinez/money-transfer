package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.model.Transfer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransferServiceTest extends TestService {

    @Test
    public void getTransfers() throws Exception {
        URI uri = builder.setPath("/transfers").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Transfer[] transfers = mapper.readValue(json, Transfer[].class);
        assertTrue(transfers.length > 0);
    }

    @Test
    public void getValidTransfersByToAccountNo() throws Exception {
        URI uri = builder.setPath("/transfers/to/56789012345").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Transfer[] transfers = mapper.readValue(json, Transfer[].class);
        assertTrue(transfers.length > 0);
    }

    @Test
    public void getTransfersByFromAccountNo() throws Exception {
        URI uri = builder.setPath("/transfers/from/12345678901").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Transfer[] transfers = mapper.readValue(json, Transfer[].class);
        assertTrue(transfers.length > 0);
    }

    @Test
    public void validTransferAmount() throws Exception {
        URI uri = builder.setPath("/transfers").build();
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "23456789012";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "45678901234";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        BigDecimal amount = new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, amount, "AUD");

        String jsonInString = mapper.writeValueAsString(transfer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(201, statusCode);
    }

    @Test
    public void invalidFromAccountTransferAmount() throws Exception {
        URI uri = builder.setPath("/transfers").build();
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "234567890120";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "45678901234";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        BigDecimal amount = new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, amount, "AUD");

        String jsonInString = mapper.writeValueAsString(transfer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
        String json = EntityUtils.toString(response.getEntity());
        assertEquals("Invalid source account.", json);
    }

    @Test
    public void invalidToAccountTransferAmount() throws Exception {
        URI uri = builder.setPath("/transfers").build();
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "23456789012";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "456789012340";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        BigDecimal amount = new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, amount, "AUD");

        String jsonInString = mapper.writeValueAsString(transfer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
        String json = EntityUtils.toString(response.getEntity());
        assertEquals("Invalid destination account.", json);
    }

    @Test
    public void insufficientFundSourceAccountTransferAmount() throws Exception {
        URI uri = builder.setPath("/transfers").build();
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "23456789012";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "45678901234";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        BigDecimal amount = new BigDecimal(10000).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, amount, "AUD");

        String jsonInString = mapper.writeValueAsString(transfer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        String json = EntityUtils.toString(response.getEntity());
        assertEquals("Insufficient fund on source account.", json);
    }

    @Test
    public void invalidCurrencySourceAccountTransferAmount() throws Exception {
        URI uri = builder.setPath("/transfers").build();
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "900";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "45678901234";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        BigDecimal amount = new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, amount, "AUD");

        String jsonInString = mapper.writeValueAsString(transfer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        String json = EntityUtils.toString(response.getEntity());
        assertEquals("Invalid currency on source account.", json);
    }

    @Test
    public void invalidCurrencyDestAccountTransferAmount() throws Exception {
        URI uri = builder.setPath("/transfers").build();
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "23456789012";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "900";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        BigDecimal amount = new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, amount, "AUD");

        String jsonInString = mapper.writeValueAsString(transfer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        String json = EntityUtils.toString(response.getEntity());
        assertEquals("Invalid currency on destination account.", json);
    }

    @Test
    public void invalidTransferCurrencyTransferAmount() throws Exception {
        URI uri = builder.setPath("/transfers").build();
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "23456789012";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "45678901234";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        BigDecimal amount = new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, amount, "aaa");

        String jsonInString = mapper.writeValueAsString(transfer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        String json = EntityUtils.toString(response.getEntity());
        assertEquals("Invalid transfer currency.", json);
    }

    @Test
    public void mismatchCurrencyTransferAmount() throws Exception {
        URI uri = builder.setPath("/transfers").build();
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "23456789012";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "45678901234";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        BigDecimal amount = new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, amount, "SGD");

        String jsonInString = mapper.writeValueAsString(transfer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        String json = EntityUtils.toString(response.getEntity());
        assertEquals("Transfer currency doesn't correspond to either account currencies.", json);
    }

    @Test
    public void rateNotFoundTransferAmount() throws Exception {
        URI uri = builder.setPath("/transfers").build();
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "800";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "45678901234";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        BigDecimal amount = new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, amount, "AUD");

        String jsonInString = mapper.writeValueAsString(transfer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
        String json = EntityUtils.toString(response.getEntity());
        assertEquals("Rate not found for source and destination currencies", json);
    }
}
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

import static org.junit.Assert.*;

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
        URI uri = builder.setPath("/transfers/query")
                .setParameter("to", "56789012345")
                .build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Transfer[] transfers = mapper.readValue(json, Transfer[].class);
        assertTrue(transfers.length > 0);
    }

    @Test
    public void getValidTransfersByFromAccountNo() throws Exception {
        URI uri = builder.setPath("/transfers/query")
                .setParameter("from", "12345678901")
                .build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Transfer[] transfers = mapper.readValue(json, Transfer[].class);
        assertTrue(transfers.length > 0);
    }

    @Test
    public void getEmptyParamTransfersByAccountNo() throws Exception {
        URI uri = builder.setPath("/transfers/query")
                .build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(500, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        assertEquals("Source and Destination account numbers cannot be null.", json);
    }

    @Test
    public void getBothParamsTransfersByAccountNo() throws Exception {
        URI uri = builder.setPath("/transfers/query")
                .setParameter("from", "12345678901")
                .setParameter("to", "56789012345")
                .build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(500, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        assertEquals("Source and Destination account numbers cannot be passed at the same time.", json);
    }

    @Test
    public void validTransferAmount() throws Exception {
        URI uri = builder.setPath("/transfers").build();
        //Using String + Long.parseLong because literal numbers in java are by default ints
        //Range -2147483648 to  2147483647 inclusive (too small for this case)
        String fromAccountNoStr = "12345678901";
        long fromAccountNo = Long.parseLong(fromAccountNoStr);
        String toAccountNoStr = "56789012345";
        long toAccountNo = Long.parseLong(toAccountNoStr);
        BigDecimal amount = new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Transfer transfer = new Transfer(fromAccountNo, toAccountNo, amount, "EUR");

        String jsonInString = mapper.writeValueAsString(transfer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(201, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Transfer newTransfer = mapper.readValue(json, Transfer.class);
        assertNotNull(newTransfer);
        assertEquals(new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN), newTransfer.getTransferAmount().setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(new BigDecimal(1).setScale(2, BigDecimal.ROUND_HALF_EVEN), newTransfer.getRate().setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN), newTransfer.getDebitedAmount().setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN), newTransfer.getCreditedAmount().setScale(2, BigDecimal.ROUND_HALF_EVEN));

        URI uriBalance = builder.setPath("/accounts/12345678901/balance").build();
        HttpGet requestBalance = new HttpGet(uriBalance);
        HttpResponse responseBalance = client.execute(requestBalance);
        int statusCodeBalance = responseBalance.getStatusLine().getStatusCode();
        assertEquals(200, statusCodeBalance);

        String jsonBalance = EntityUtils.toString(responseBalance.getEntity());
        BigDecimal balance = mapper.readValue(jsonBalance, BigDecimal.class);
        assertNotNull(balance);
        assertEquals(new BigDecimal(490.57).setScale(2, BigDecimal.ROUND_HALF_EVEN), balance.setScale(2, BigDecimal.ROUND_HALF_EVEN));

        URI uriBalance2 = builder.setPath("/accounts/56789012345/balance").build();
        HttpGet requestBalance2 = new HttpGet(uriBalance2);
        HttpResponse responseBalance2 = client.execute(requestBalance2);
        int statusCodeBalance2 = responseBalance2.getStatusLine().getStatusCode();
        assertEquals(200, statusCodeBalance2);

        String jsonBalance2 = EntityUtils.toString(responseBalance2.getEntity());
        BigDecimal balance2 = mapper.readValue(jsonBalance2, BigDecimal.class);
        assertNotNull(balance2);
        assertEquals(new BigDecimal(1181.06).setScale(2, BigDecimal.ROUND_HALF_EVEN), balance2.setScale(2, BigDecimal.ROUND_HALF_EVEN));
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
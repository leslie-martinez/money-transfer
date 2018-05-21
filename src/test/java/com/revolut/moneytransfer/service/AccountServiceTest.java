package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.model.Account;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.net.URI;

import static org.junit.Assert.*;

public class AccountServiceTest extends TestService {

    @Test
    public void getAccounts() throws Exception {
        URI uri = builder.setPath("/accounts").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Account[] accounts = mapper.readValue(json, Account[].class);
        assertTrue(accounts.length > 0);
    }

    @Test
    public void getValidAccountByAccountNo() throws Exception {
        URI uri = builder.setPath("/accounts/12345678901").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Account account = mapper.readValue(json, Account.class);
        assertNotNull(account);
    }

    @Test
    public void getNotFoundAccountByAccountNo() throws Exception {
        URI uri = builder.setPath("/accounts/100").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        assertEquals("No account found, accountNo : 100", json);
    }

    @Test
    public void getValidAccountBalance() throws Exception {
        URI uri = builder.setPath("/accounts/12345678901/balance").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        BigDecimal balance = mapper.readValue(json, BigDecimal.class);
        assertEquals(new BigDecimal(500.57).setScale(2, BigDecimal.ROUND_HALF_EVEN), balance.setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }

    @Test
    public void getInvalidAccountBalance() throws Exception {
        URI uri = builder.setPath("/accounts/100/balance").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        assertEquals("No account found, accountNo : 100", json);
    }

    @Test
    public void deleteValidAccount() throws Exception {
        URI uri = builder.setPath("/accounts/700").build();
        HttpDelete request = new HttpDelete(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(204, statusCode);
    }

    @Test
    public void deleteInvalidAccount() throws Exception {
        URI uri = builder.setPath("/accounts/100").build();
        HttpDelete request = new HttpDelete(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        assertEquals("No account found, accountNo : 100", json);
    }

    @Test
    public void deleteInvalidBalanceAccount() throws Exception {
        URI uri = builder.setPath("/accounts/600").build();
        HttpDelete request = new HttpDelete(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        assertEquals("Account balance not zero.", json);
    }
}
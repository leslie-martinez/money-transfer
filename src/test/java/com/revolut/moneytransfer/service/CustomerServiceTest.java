package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.model.Customer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;

public class CustomerServiceTest extends TestService {

    @Test
    public void getCustomers() throws Exception {
        URI uri = builder.setPath("/customers").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Customer[] customers = mapper.readValue(json, Customer[].class);
        assertTrue(customers.length > 0);
    }

    @Test
    public void getCustomerByValidAccountNo() throws Exception {
        URI uri = builder.setPath("/customers/56789012345").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        Customer customer = mapper.readValue(json, Customer.class);
        assertNotNull(customer);
    }

    @Test
    public void getCustomerByInvalidAccountNo() throws Exception {
        URI uri = builder.setPath("/customers/567890123452").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);

        String json = EntityUtils.toString(response.getEntity());
        assertEquals("No account found, accountNo : 567890123452", json);
    }
}
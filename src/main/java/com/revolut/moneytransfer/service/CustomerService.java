package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.H2Dao;
import com.revolut.moneytransfer.model.Customer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerService {
    private static final Logger log = Logger.getLogger("CustomerService");
    private final H2Dao h2Dao = new H2Dao();

    /**
     * Method returning all customers available
     * @return All customers
     * @throws Exception Error while getting customers
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomers() throws Exception {
        log.info("REST : getCustomers");
        List<Customer> customers;
        try {
            customers = h2Dao.getCustomerDao().getAllCustomers();
            if (customers == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No customer found.").build();
            }
        } catch (Exception e){
            log.severe("Error while getting customers.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(customers).build();
    }

    /**
     * Service returning the Customer object
     * @param accountNo Account Number
     * @return Account owner
     * @throws Exception e
     */
    @GET
    @Path("/{accountNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerByAccountNo(@PathParam("accountNo") Long accountNo) throws Exception {
        log.info("REST : getCustomerByAccountNo");
        if (accountNo == null) {
            return Response.serverError().entity("accountNo cannot be null.").build();
        }
        Customer customer;
        try {
            customer = h2Dao.getCustomerDao().getCustomerByAccountNo(accountNo);
            if (customer == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No account found, accountNo : " + accountNo).build();
            }
        } catch (Exception e) {
            log.severe("Error while getting customer.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(customer).build();
    }
}

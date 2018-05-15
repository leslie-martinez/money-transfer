package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.H2Dao;
import com.revolut.moneytransfer.model.Account;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

/**
 * Account Service Class
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountService {
    private static final Logger log = Logger.getLogger("AccountService");
    private final H2Dao h2Dao = new H2Dao();

    /**
     * Method returning all accounts available
     * @return All accounts
     * @throws Exception Error while getting accounts
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts() throws Exception {
        log.info("REST : getAllAccounts");
        List<Account> accounts;
        try {
             accounts = h2Dao.getAccountDAO().getAllAccounts();
             if(accounts == null){
                 return Response.status(Response.Status.NOT_FOUND).entity("No account found.").build();
             }
        } catch (Exception e){
            log.severe("Error while getting accounts.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(accounts).build();
    }

    /**
     * Service returning the Account object
     * @param accountNo Account Number
     * @return Account
     * @throws Exception e
     */
    @GET
    @Path("/{accountNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountByAccountNo(@PathParam("accountNo") Long accountNo) throws Exception {
        log.info("REST : getAccountByAccountNo");
        if(accountNo == null){
            return Response.serverError().entity("accountId cannot be null.").build();
        }
        Account account;
        try {
            account = h2Dao.getAccountDAO().getAccountByAccountNo(accountNo);
            if(account == null){
                return Response.status(Response.Status.NOT_FOUND).entity("No account found, accountNo : " + accountNo).build();
            }
        } catch (Exception e){
            log.severe("Error while getting account.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(account).build();
    }

    /**
     * Service returning the balance available on the account
     * @param accountNo Account Number
     * @return Account
     * @throws Exception e
     */
    @GET
    @Path("/{accountNo}/balance")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountBalance(@PathParam("accountNo") Long accountNo) throws Exception {
        log.info("REST : getAccountBalance");
        if(accountNo == null){
            return Response.serverError().entity("accountId cannot be null.").build();
        }
        BigDecimal balance;
        try {
            balance = h2Dao.getAccountDAO().getAccountBalance(accountNo);
            if(balance == null){
                return Response.status(Response.Status.NOT_FOUND).entity("No account found, accountNo : " + accountNo).build();
            }
        } catch (Exception e){
            log.severe("Error while getting account.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(balance).build();
    }

}

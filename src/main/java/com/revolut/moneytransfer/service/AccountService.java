package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.H2Dao;
import com.revolut.moneytransfer.model.Account;

import javax.ws.rs.*;
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
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts() {
        log.info("REST : getAllAccounts");
        List<Account> accounts;
        try {
             accounts = h2Dao.getAccountDAO().getAllAccounts();
             if(accounts == null){
                 return Response.status(Response.Status.NOT_FOUND).entity("No account found.").build();
             }
        } catch (Exception e){
            log.severe(e.getMessage());
            return Response.serverError().entity("Error while getting accounts.").build();
        }
        return Response.status(Response.Status.OK).entity(accounts).build();
    }

    /**
     * Service returning the Account object
     * @param accountNo Account Number
     * @return Account
     */
    @GET
    @Path("/{accountNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountByAccountNo(@PathParam("accountNo") Long accountNo) {
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
            log.severe(e.getMessage());
            return Response.serverError().entity("Error while getting account.").build();
        }
        return Response.status(Response.Status.OK).entity(account).build();
    }

    /**
     * Service returning the balance available on the account
     * @param accountNo Account Number
     * @return Account
     */
    @GET
    @Path("/{accountNo}/balance")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountBalance(@PathParam("accountNo") Long accountNo) {
        log.info("REST : getAccountBalance");
        if(accountNo == null){
            return Response.serverError().entity("accountNo cannot be null.").build();
        }
        BigDecimal balance;
        try {
            balance = h2Dao.getAccountDAO().getAccountBalance(accountNo);
            if(balance == null){
                return Response.status(Response.Status.NOT_FOUND).entity("No account found, accountNo : " + accountNo).build();
            }
        } catch (Exception e){
            log.severe(e.getMessage());
            return Response.serverError().entity("Error while getting account.").build();
        }
        return Response.status(Response.Status.OK).entity(balance).build();
    }

    /**
     * Delete account by account number (will only delete if balance is zero)
     *
     * @param accountNo account number to delete
     * @return Response 204, 400, 404
     */
    @DELETE
    @Path("/{accountNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(@PathParam("accountNo") Long accountNo) {
        log.info("REST : deleteAccount");
        if (accountNo == null) {
            return Response.serverError().entity("accountNo cannot be null.").build();
        }
        try {
            Account.accountResponse response = h2Dao.getAccountDAO().deleteAccount(accountNo);
            if (response.equals(Account.accountResponse.ACCOUNT_NOT_FOUND)) {
                return Response.status(Response.Status.NOT_FOUND).entity("No account found, accountNo : " + accountNo).build();
            } else if (response.equals(Account.accountResponse.BALANCE_NOT_ZERO)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(response.getErrorMessage()).build();
            }
        } catch (Exception e) {
            log.severe(e.getMessage());
            return Response.serverError().entity("Error while deleting account.").build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}

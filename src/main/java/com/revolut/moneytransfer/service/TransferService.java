package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.H2Dao;
import com.revolut.moneytransfer.model.Transfer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Path("/transfers")
@Produces(MediaType.APPLICATION_JSON)
public class TransferService {
    private static final Logger log = Logger.getLogger("TransferService");
    private final H2Dao h2Dao = new H2Dao();

    /**
     * Method returning all transfers available
     * @return All transfers
     * @throws Exception Error while getting transfers
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransfers() throws Exception {
        log.info("REST : getTransfers");
        List<Transfer> transfers;
        try {
            transfers = h2Dao.getTransferDAO().getAllTransfers();
            if(transfers == null){
                return Response.status(Response.Status.NO_CONTENT).entity("No transfer found.").build();
            }
        } catch (Exception e){
            log.severe("Error while getting transfers.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(transfers).build();
    }

    /**
     * Service returning all transfers fro a specific account
     * @param accountNo Account Number
     * @param startDt query date range startDate
     * @param endDt query date range endDate
     * @param format format of dates passed
     * @return List of transfers
     * @throws Exception e
     */
    @GET
    @Path("/to/{accountNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransfersByToAccountNo(@PathParam("accountNo") Long accountNo, @QueryParam("startDt") String startDt, @QueryParam("endDt") String endDt, @QueryParam("format") String format) throws Exception {
        log.info("REST : getTransferByAccountNo");
        if (accountNo == null) {
            return Response.serverError().entity("accountNo cannot be null.").build();
        }
        List<Transfer> transfers;
        try {
            transfers = h2Dao.getTransferDAO().getTransfersByAccountNo(accountNo, "TO");
            if (transfers == null) {
                return Response.status(Response.Status.NO_CONTENT).entity("No transfer found for account : " + accountNo).build();
            }
        } catch (Exception e) {
            log.severe("Error while getting transfer.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(transfers).build();
    }

    /**
     * Service returning all transfers fro a specific account
     *
     * @param accountNo Account Number
     * @param startDt   query date range startDate
     * @param endDt     query date range endDate
     * @param format    format of dates passed
     * @return List of transfers
     * @throws Exception e
     */
    @GET
    @Path("/from/{accountNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransfersByFromAccountNo(@PathParam("accountNo") Long accountNo, @QueryParam("startDt") String startDt, @QueryParam("endDt") String endDt, @QueryParam("format") String format) throws Exception {
        log.info("REST : getTransferByAccountNo");
        if (accountNo == null) {
            return Response.serverError().entity("accountNo cannot be null.").build();
        }
        List<Transfer> transfers;
        try {
            transfers = h2Dao.getTransferDAO().getTransfersByAccountNo(accountNo, "FROM");
            if (transfers == null) {
                return Response.status(Response.Status.NO_CONTENT).entity("No transfer found for account : " + accountNo).build();
            }
        } catch (Exception e) {
            log.severe("Error while getting transfer.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(transfers).build();
    }


    /**
     * @param transfer Transfer object
     * @return Response
     */
    @POST
    @Path("")
    public Response transferAmount(Transfer transfer) {
        log.info("REST : transferAmount");
        log.info("transfer.getSourceAccountNo() : " + transfer.getSourceAccountNo());
        log.info("transfer.getDestinationAccountNo() : " + transfer.getDestinationAccountNo());
        log.info("transfer.getAmount() : " + transfer.getAmount());
        log.info("transfer.getCurrencyCode() : " + transfer.getCurrencyCode());

        if (transfer.getSourceAccountNo() == null) {
            return Response.serverError().entity("Source account cannot be null.").build();
        }
        else if (transfer.getDestinationAccountNo() == null) {
            return Response.serverError().entity("Destination account cannot be null.").build();
        }
        else if (transfer.getAmount() == null) {
            return Response.serverError().entity("Amount to transfer cannot be null.").build();
        }
        else if (transfer.getCurrencyCode() == null) {
            return Response.serverError().entity("Currency cannot be null.").build();
        }
        Transfer.transferResponse response;
        try {
            response = h2Dao.getTransferDAO().processTransfer(transfer);
            if (response != null) {
                if (response == Transfer.transferResponse.INVALID_FROM_ACC || response == Transfer.transferResponse.INVALID_TO_ACC) {
                    return Response.status(Response.Status.NOT_FOUND).entity(response.getErrorMessage() + transfer.getSourceAccountNo()).build();
                } else if (response == Transfer.transferResponse.INSUFFICIENT_FUND || response == Transfer.transferResponse.INVALID_CURRENCY_FROM_ACC || response == Transfer.transferResponse.INVALID_CURRENCY_TO_ACC) {
                    return Response.status(Response.Status.BAD_REQUEST).entity(response.getErrorMessage() + transfer.getSourceAccountNo()).build();
                }
            }
        } catch (Exception e) {
            log.severe("Error while getting transfer : " + e.getMessage());
            return Response.serverError().entity("Error while getting transfer : " + e.getMessage()).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }
}

package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.H2Dao;
import com.revolut.moneytransfer.model.Transfer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
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
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransfers() {
        log.info("REST : getTransfers");
        List<Transfer> transfers;
        try {
            transfers = h2Dao.getTransferDAO().getAllTransfers();
            if(transfers == null){
                return Response.status(Response.Status.NO_CONTENT).entity("No transfer found.").build();
            }
        } catch (Exception e){
            log.severe(e.getMessage());
            return Response.serverError().entity("SError while getting transfers.").build();
        }
        return Response.status(Response.Status.OK).entity(transfers).build();
    }

    /**
     * Service returning all transfers for a specific account (to or from)
     *
     * @param sourceAccountNo Source Account Number
     * @param destinationAccountNo Destination Account Number
     * @return List of transfers
     */
    @GET
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransfersByAccountNo(@QueryParam("from") Long sourceAccountNo, @QueryParam("to") Long destinationAccountNo) {
        log.info("REST : getTransferByAccountNo");
        if (sourceAccountNo == null && destinationAccountNo == null) {
            return Response.serverError().entity("Source and Destination account numbers cannot be null.").build();
        }
        if (sourceAccountNo != null && destinationAccountNo != null) {
            return Response.serverError().entity("Source and Destination account numbers cannot be passed at the same time.").build();
        }
        String mode = null;
        Long accountNo = null;
        if (sourceAccountNo != null) {
            accountNo = sourceAccountNo;
            mode = "FROM";
        } else if (destinationAccountNo != null) {
            accountNo = destinationAccountNo;
            mode = "TO";
        }
        List<Transfer> transfers;
        try {
            transfers = h2Dao.getTransferDAO().getTransfersByAccountNo(accountNo, mode);
            if (transfers == null) {
                return Response.status(Response.Status.NO_CONTENT).entity("No transfer found for account : " + accountNo).build();
            }
        } catch (Exception e) {
            log.severe(e.getMessage());
            return Response.serverError().entity("Error while getting transfer.").build();
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
        if (transfer == null)
            return Response.serverError().entity("Transfer cannot be null.").build();
        if (transfer.getSourceAccountNo() == null || transfer.getSourceAccountNo().compareTo(0L) == 0) {
            return Response.serverError().entity("Source account cannot be null.").build();
        } else if (transfer.getDestinationAccountNo() == null || transfer.getDestinationAccountNo().compareTo(0L) == 0) {
            return Response.serverError().entity("Destination account cannot be null.").build();
        } else if (transfer.getTransferAmount() == null || transfer.getTransferAmount().setScale(2, BigDecimal.ROUND_HALF_EVEN).equals(new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_EVEN))) {
            return Response.serverError().entity("Amount to transfer cannot be null.").build();
        } else if (transfer.getTransferCurrencyCode() == null || transfer.getTransferCurrencyCode().isEmpty()) {
            return Response.serverError().entity("Currency cannot be null.").build();
        }
        try {
            transfer = h2Dao.getTransferDAO().processTransfer(transfer);
            if (transfer.getResponse() == Transfer.transferResponse.INVALID_FROM_ACC || transfer.getResponse() == Transfer.transferResponse.INVALID_TO_ACC || transfer.getResponse() == Transfer.transferResponse.RATE_NOT_FOUND) {
                return Response.status(Response.Status.NOT_FOUND).entity(transfer.getResponse().getErrorMessage()).build();
            } else if (transfer.getResponse().getErrorMessage() != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(transfer.getResponse().getErrorMessage()).build();
            }
        } catch (Exception e) {
            log.severe("Error while processing transfer : " + e.getMessage());
            return Response.serverError().entity("Error while processing transfer.").build();
        }
        return Response.status(Response.Status.CREATED).entity(transfer).build();
    }
}

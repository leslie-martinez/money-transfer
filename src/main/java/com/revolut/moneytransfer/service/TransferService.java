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
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransfers() throws Exception {
        log.info("REST : getTransfers");
        List<Transfer> transfers = null;
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
     * @param accountNo Account Number     *
     * @param startDt
     * @param endDt
     * @param format
     * @return List of transfers
     * @throws Exception e
     */
    @GET
    @Path("/{accountNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransfersByAccountNo(@PathParam("accountNo") Long accountNo, @QueryParam("startDt") String startDt, @QueryParam("endDt") String endDt, @QueryParam("format") String format) throws Exception {
        log.info("REST : getTransferByAccountNo");
        if (accountNo == null) {
            return Response.serverError().entity("accountNo cannot be null.").build();
        }
        List<Transfer> transfers = null;
        try {
            transfers = h2Dao.getTransferDAO().getTransfersByAccountNo(accountNo);
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
     * @return
     * @throws Exception e
     */
    @POST
    public Response transferAmount(Transfer transfer) throws Exception {
        log.info("REST : transferAmount");
        if (transfer == null) {
            return Response.serverError().entity("transfer object cannot be null.").build();
        }
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
        int response;
        try {
            response = h2Dao.getTransferDAO().executeTransfer(transfer);
            if (response == transferResponse.INVALID_FROM_ACC.getCode()) {
                return Response.status(Response.Status.NOT_FOUND).entity(transferResponse.INVALID_FROM_ACC.getErrorMessage() + transfer.getSourceAccountNo()).build();
            }
            else if(response == transferResponse.INVALID_TO_ACC.getCode()){
                return Response.status(Response.Status.NOT_FOUND).entity(transferResponse.INVALID_TO_ACC.getErrorMessage() + transfer.getDestinationAccountNo()).build();
            }
            else if(response == transferResponse.INSUFFICIENT_FUND.getCode()){
                return Response.status(Response.Status.BAD_REQUEST).entity(transferResponse.INSUFFICIENT_FUND.getErrorMessage() + transfer.getSourceAccountNo()).build();
            }
            else if(response == transferResponse.INVALID_CURRENCY_FROM_ACC.getCode()){
                return Response.status(Response.Status.BAD_REQUEST).entity(transferResponse.INVALID_CURRENCY_FROM_ACC.getErrorMessage() + transfer.getSourceAccountNo()).build();
            }
            else if(response == transferResponse.INVALID_CURRENCY_TO_ACC.getCode()){
                return Response.status(Response.Status.BAD_REQUEST).entity(transferResponse.INVALID_CURRENCY_TO_ACC.getErrorMessage() + transfer.getDestinationAccountNo()).build();
            }
        } catch (Exception e) {
            log.severe("Error while getting transfer.");
            return Response.serverError().entity("Error while getting transfer.").build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    private enum transferResponse{
        SUCCESS(0, null),
        INVALID_FROM_ACC(1, "Invalid account : "),
        INVALID_TO_ACC(2, "Invalid destination account : "),
        INSUFFICIENT_FUND(3, "Insufficient fund on account : "),
        INVALID_CURRENCY_FROM_ACC(4, "Invalid currency on sending account : "),
        INVALID_CURRENCY_TO_ACC(5, "Invalid currency on destination account : ");

        private int code;
        private String errorMessage;

        transferResponse(int code, String errorMessage){
            this.code = code;
            this.errorMessage = errorMessage;
        }

        public int getCode() {
            return code;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

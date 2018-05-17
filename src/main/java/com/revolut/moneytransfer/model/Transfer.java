package com.revolut.moneytransfer.model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Transfer Model Class
 */
public class Transfer extends Generic {
    /**
     * Unique Transfer Identifier
     */
    private int id;
    /**
     * Source Account id
     */
    private Long sourceAccountNo;
    /**
     * Destination Account id
     */
    private Long destinationAccountNo;
    /**
     * Amount to transfer
     */
    private BigDecimal amount;
    /**
     * Currency Code of the transaction
     */
    private String currencyCode;
    /**
     * Transaction status
     */
    private String status;

    /**
     * Transfer remarks
     */
    private String remarks;

    /**
     * Default Transfer Constructor
     */
    public Transfer() {
    }

    /**
     * Transfer constructor with parameter
     * @param id transfer uid
     * @param sourceAccountNo source account id
     * @param destinationAccountNo destination account id
     * @param amount amount to be transferred
     * @param currencyCode amount currency code
     * @param status status of transfer
     * @param remarks remarks of the transfer
     * @param createdDt creation date time of transfer
     * @param lastUpdateDt last update date time of transfer
     */
    public Transfer(int id, Long sourceAccountNo, Long destinationAccountNo, BigDecimal amount, String currencyCode, String status, String remarks, Date createdDt, Date lastUpdateDt) {
        this.id = id;
        this.sourceAccountNo = sourceAccountNo;
        this.destinationAccountNo = destinationAccountNo;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.status = status;
        this.remarks = remarks;
        this.setCreatedDt(createdDt);
        this.setLastUpdateDt(lastUpdateDt);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getSourceAccountNo() {
        return sourceAccountNo;
    }

    public void setSourceAccountNo(Long sourceAccountNo) {
        this.sourceAccountNo = sourceAccountNo;
    }

    public Long getDestinationAccountNo() {
        return destinationAccountNo;
    }

    public void setDestinationAccountNo(Long destinationAccountNo) {
        this.destinationAccountNo = destinationAccountNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public enum transferResponse {
        SUCCESS(0, null),
        INVALID_FROM_ACC(1, "Invalid account : "),
        INVALID_TO_ACC(2, "Invalid destination account : "),
        INSUFFICIENT_FUND(3, "Insufficient fund on account : "),
        INVALID_CURRENCY_FROM_ACC(4, "Invalid currency on sending account : "),
        INVALID_CURRENCY_TO_ACC(5, "Invalid currency on destination account : "),
        INVALID_CURRENCY_TRANSFER(6, "Invalid transfer currency."),
        TRANSFER_CURRENCY_MISMATCH(7, "Transfer currency doesn't correspond to either account currencies.");

        private int code;
        private String errorMessage;

        transferResponse(int code, String errorMessage) {
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

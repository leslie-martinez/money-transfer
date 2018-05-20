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
     * Amount debited from source account
     */
    private BigDecimal debitedAmount;
    /**
     * Currency Code of the source account
     */
    private String sourceCurrencyCode;

    /**
     *  Amount to transfer
     */
    private BigDecimal transferAmount;
    /**
     * Currency Code of the transaction
     */
    private String transferCurrencyCode;

    /**
     * Amount credited on destination account
     */
    private BigDecimal creditedAmount;
    /**
     * Currency Code of the credited amount
     */
    private String destinationCurrencyCode;

    /**
     * Transfer rate
     */
    private BigDecimal rate;
    /**
     * Transaction status
     */
    private String status;

    private Transfer.transferResponse response;

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
     * @param debitedAmount debitedAmount to be transferred
     * @param sourceCurrencyCode debitedAmount currency code
     * @param transferAmount amount to be transferred
     * @param transferCurrencyCode transfer currency
     * @param creditedAmount amount to be credited
     * @param destinationCurrencyCode destination currency
     * @param status status of transfer
     * @param rate rate of the transfer
     * @param createdDt creation date time of transfer
     * @param lastUpdateDt last update date time of transfer
     */
    public Transfer(int id, Long sourceAccountNo, Long destinationAccountNo, BigDecimal debitedAmount, String sourceCurrencyCode, BigDecimal transferAmount, String transferCurrencyCode, BigDecimal creditedAmount, String destinationCurrencyCode, BigDecimal rate, String status, Date createdDt, Date lastUpdateDt) {
        this.id = id;
        this.sourceAccountNo = sourceAccountNo;
        this.destinationAccountNo = destinationAccountNo;
        this.debitedAmount = debitedAmount;
        this.transferAmount = transferAmount;
        this.sourceCurrencyCode = sourceCurrencyCode;
        this.transferCurrencyCode = transferCurrencyCode;
        this.status = status;
        this.rate = rate;
        this.creditedAmount = creditedAmount;
        this.destinationCurrencyCode = destinationCurrencyCode;
        this.setCreatedDt(createdDt);
        this.setLastUpdatedDt(lastUpdateDt);
    }


    public Transfer(Long sourceAccountNo, Long destinationAccountNo, BigDecimal transferAmount, String transferCurrencyCode) {
        this.sourceAccountNo = sourceAccountNo;
        this.destinationAccountNo = destinationAccountNo;
        this.transferAmount = transferAmount;
        this.transferCurrencyCode = transferCurrencyCode;
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

    public BigDecimal getDebitedAmount() {
        return debitedAmount;
    }

    public void setDebitedAmount(BigDecimal debitedAmount) {
        this.debitedAmount = debitedAmount;
    }

    public String getSourceCurrencyCode() {
        return sourceCurrencyCode;
    }

    public void setSourceCurrencyCode(String sourceCurrencyCode) {
        this.sourceCurrencyCode = sourceCurrencyCode;
    }

    public BigDecimal getCreditedAmount() {
        return creditedAmount;
    }

    public void setCreditedAmount(BigDecimal creditedAmount) {
        this.creditedAmount = creditedAmount;
    }

    public String getDestinationCurrencyCode() {
        return destinationCurrencyCode;
    }

    public void setDestinationCurrencyCode(String destinationCurrencyCode) {
        this.destinationCurrencyCode = destinationCurrencyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getTransferCurrencyCode() {
        return transferCurrencyCode;
    }

    public void setTransferCurrencyCode(String transferCurrencyCode) {
        this.transferCurrencyCode = transferCurrencyCode;
    }

    public transferResponse getResponse() {
        return response;
    }

    public void setResponse(transferResponse response) {
        this.response = response;
    }


    public enum transferResponse {
        SUCCESS(0, null),
        INVALID_FROM_ACC(1, "Invalid source account."),
        INVALID_TO_ACC(2, "Invalid destination account."),
        INSUFFICIENT_FUND(3, "Insufficient fund on source account."),
        INVALID_CURRENCY_FROM_ACC(4, "Invalid currency on source account."),
        INVALID_CURRENCY_TO_ACC(5, "Invalid currency on destination account."),
        INVALID_CURRENCY_TRANSFER(6, "Invalid transfer currency."),
        TRANSFER_CURRENCY_MISMATCH(7, "Transfer currency doesn't correspond to either account currencies."),
        RATE_NOT_FOUND(8, "Rate not found for source and destination currencies");

        private final int code;
        private final String errorMessage;

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

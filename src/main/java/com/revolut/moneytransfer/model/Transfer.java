package com.revolut.moneytransfer.model;

import java.io.Serializable;
import java.math.BigDecimal;
/**
 * Transfer Model Class
 */
public class Transfer implements Serializable {
    private static final long serialVersionUID = 1L;
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
     * Default Transfer Constructor
     */
    public Transfer() {
    }

    /**
     * Transfer constructor with parameter
     * @param sourceAccountNo source account id
     * @param destinationAccountNo destination account id
     * @param amount amount to be transferred
     * @param currencyCode amount currency code
     * @param status status of transaction
     */
    public Transfer(int id, Long sourceAccountNo, Long destinationAccountNo, BigDecimal amount, String currencyCode, String status) {
        this.id = id;
        this.sourceAccountNo = sourceAccountNo;
        this.destinationAccountNo = destinationAccountNo;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.status = status;
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


}

package com.revolut.moneytransfer.model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Account Model Class
 */
public class Account extends Generic {
    /**
     * Account unique Identifier
     */
    private  int id;
    /**
     * Account owner Id
     */
    private int accountOwnerId;
    /**
     * Account Number
     */
    private long accountNo;
    /**
     * Account Balance
     */
    private BigDecimal balance;
    /**
     * Account Currency Code
     */
    private String currencyCode;

    /**
     * Default Account Constructor
     */
    public Account(){}

    /**
     * Account Constructor with parameter
     * @param id unique identifier
     * @param accountOwnerId account owner id
     * @param accountNo account number
     * @param balance account balance
     * @param currencyCode account currency code
     * @param createdDt creation date time of transfer
     * @param lastUpdateDt last update date time of transfer
     */
    public Account(int id, int accountOwnerId, long accountNo, BigDecimal balance, String currencyCode, Date createdDt, Date lastUpdateDt) {
        this.id = id;
        this.accountNo = accountNo;
        this.balance = balance;
        this.currencyCode = currencyCode;
        this.accountOwnerId = accountOwnerId;
        this.setCreatedDt(createdDt);
        this.setLastUpdatedDt(lastUpdateDt);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(long accountNo) {
        this.accountNo = accountNo;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public int getAccountOwnerId() {
        return accountOwnerId;
    }

    public void setAccountOwnerId(int accountOwnerId) {
        this.accountOwnerId = accountOwnerId;
    }


    public enum accountResponse {
        SUCCESS("SUCCESS", null),
        ACCOUNT_NOT_FOUND("ACCOUNT_NOT_FOUND", "Account not found."),
        BALANCE_NOT_ZERO("BALANCE_NOT_ZERO", "Account balance not zero.");

        private final String code;
        private final String errorMessage;

        accountResponse(String code, String errorMessage) {
            this.code = code;
            this.errorMessage = errorMessage;
        }

        public String getCode() {
            return code;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

}

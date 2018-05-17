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
     */
    public Account(int id, int accountOwnerId, long accountNo, BigDecimal balance, String currencyCode, Date createdDt, Date lastUpdateDt) {
        this.id = id;
        this.accountNo = accountNo;
        this.balance = balance;
        this.currencyCode = currencyCode;
        this.accountOwnerId = accountOwnerId;
        this.setCreatedDt(createdDt);
        this.setLastUpdateDt(lastUpdateDt);
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

}

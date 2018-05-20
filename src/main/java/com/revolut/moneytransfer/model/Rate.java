package com.revolut.moneytransfer.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Rate extends Generic {

    /**
     * unique id
     */
    private int id;

    /**
     * source currency
     */
    private String sourceCurrencyCode;

    /**
     * destination currency
     */
    private String destinationCurrencyCode;

    /**
     * rate
     */
    private BigDecimal rate;

    /**
     * effective date time
     */
    private Date effectiveDt;

    /**
     * default constructor
     */
    public Rate() {
    }

    /**
     * @param id                      unique id
     * @param sourceCurrencyCode      source currency
     * @param destinationCurrencyCode destination currency
     * @param rate                    transfer rate
     * @param effectiveDt             effective date
     * @param createdDt               created date time
     * @param lastUpdatedDt           last updated date time
     */
    public Rate(int id, String sourceCurrencyCode, String destinationCurrencyCode, BigDecimal rate, Date effectiveDt, Date createdDt, Date lastUpdatedDt) {
        this.id = id;
        this.sourceCurrencyCode = sourceCurrencyCode;
        this.destinationCurrencyCode = destinationCurrencyCode;
        this.rate = rate;
        this.effectiveDt = effectiveDt;
        this.setCreatedDt(createdDt);
        this.setLastUpdatedDt(lastUpdatedDt);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSourceCurrencyCode() {
        return sourceCurrencyCode;
    }

    public void setSourceCurrencyCode(String sourceCurrencyCode) {
        this.sourceCurrencyCode = sourceCurrencyCode;
    }

    public String getDestinationCurrencyCode() {
        return destinationCurrencyCode;
    }

    public void setDestinationCurrencyCode(String destinationCurrencyCode) {
        this.destinationCurrencyCode = destinationCurrencyCode;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Date getEffectiveDt() {
        return effectiveDt;
    }

    public void setEffectiveDt(Date effectiveDt) {
        this.effectiveDt = effectiveDt;
    }
}

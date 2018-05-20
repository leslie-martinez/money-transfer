package com.revolut.moneytransfer.model;

import java.io.Serializable;
import java.sql.Date;

class Generic implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Date and Time of creation
     */
    private Date createdDt;

    /**
     * Last Date and Time of update
     */
    private Date lastUpdatedDt;


    public Date getCreatedDt() {
        return createdDt;
    }

    void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public Date getLastUpdatedDt() {
        return lastUpdatedDt;
    }

    void setLastUpdatedDt(Date lastUpdatedDt) {
        this.lastUpdatedDt = lastUpdatedDt;
    }
}

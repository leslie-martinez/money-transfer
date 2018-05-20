package com.revolut.moneytransfer.model;

import java.sql.Date;

/**
 * Customer Model Class
 */
public class Customer extends Generic {

    /**
     * Customer unique Identifier
     */
    private int id;
    /**
     * Customer name
     */
    private String name;

    /**
     * Customer Address
     */
    private String address;

    public Customer() {
    }

    public Customer(int id, String name, String address, Date createdDt, Date lastUpdateDt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.setCreatedDt(createdDt);
        this.setLastUpdatedDt(lastUpdateDt);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

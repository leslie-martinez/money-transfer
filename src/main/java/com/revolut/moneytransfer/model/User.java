package com.revolut.moneytransfer.model;

import java.sql.Date;

/**
 * User Model Class
 */
public class User extends Generic {

    /**
     * User unique Identifier
     */
    private int id;
    /**
     * User name
     */
    private String name;

    /**
     * User Address
     */
    private String address;

    public User() {
    }

    public User(int id, String name, String address, Date createdDt, Date lastUpdateDt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.setCreatedDt(createdDt);
        this.setLastUpdateDt(lastUpdateDt);
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

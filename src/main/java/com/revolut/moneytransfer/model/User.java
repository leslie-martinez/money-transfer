package com.revolut.moneytransfer.model;

import java.io.Serializable;

/**
 * User Model Class
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * User unique Identifier
     */
    private int id;
    /**
     * User name
     */
    private String name;

    public User() {
    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
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

}

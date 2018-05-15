package com.revolut.moneytransfer.exception;

public class ProjectException extends Exception {

    private static final long serialVersionUID = 1L;

    public ProjectException(String msg) {
        super(msg);
    }

    public ProjectException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

package com.revolut.moneytransfer.exception;

public class ResponseError {
    public String getCodeError() {
        return codeError;
    }

    public void setCodeError(String codeError) {
        this.codeError = codeError;
    }

    private String codeError;
}

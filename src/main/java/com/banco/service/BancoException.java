package com.banco.service;

public class BancoException extends Exception {

    private final int httpStatus;

    public BancoException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}

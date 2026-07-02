package com.mycompany.bancoadn.cliente.httpapi.service;

/**
 * Simple result holder for account creation operation.
 */
public class CreateAccountResult {
    public boolean success;
    public String message;

    public CreateAccountResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}

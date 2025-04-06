package com.peter.authnservicespringboot.exception;

public class EmailAlreadyVerifiedException extends RuntimeException {
    public EmailAlreadyVerifiedException(String message) {
        super(message);
    }
}

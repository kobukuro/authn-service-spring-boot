package com.peter.authnservicespringboot.exception;

public class TokenNotValidException extends RuntimeException {
    public TokenNotValidException(String message) {
        super(message);
    }
}

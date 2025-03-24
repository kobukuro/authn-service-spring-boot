package com.peter.authnservicespringboot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({EmailAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handleConflict(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}

package com.challenge.customers.exception;

import org.springframework.http.HttpStatus;

public class ScoreServiceException extends RuntimeException {
    private final HttpStatus status;

    public ScoreServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ScoreServiceException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

package com.chaty.exception;

public class RetryException extends RuntimeException {
    
    public RetryException(String message) {
        super(message);
    }

    public RetryException(String message, Throwable throwable) {
        super(message, throwable);
    }

}

package com.signicat.dev.exception;

public class FairUsageLimitExpiredException extends RuntimeException {
    public FairUsageLimitExpiredException(String message) {
        super(message);
    }
}

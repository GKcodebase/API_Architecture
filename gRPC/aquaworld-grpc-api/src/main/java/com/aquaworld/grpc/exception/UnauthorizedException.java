package com.aquaworld.grpc.exception;

/**
 * Exception thrown when authentication fails
 */
public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
    }
}

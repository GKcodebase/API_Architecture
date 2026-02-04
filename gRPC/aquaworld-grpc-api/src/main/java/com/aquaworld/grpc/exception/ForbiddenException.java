package com.aquaworld.grpc.exception;

/**
 * Exception thrown when a user lacks permission to access a resource
 */
public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(message, "FORBIDDEN");
    }
}

package com.aquaworld.grpc.exception;

/**
 * Exception thrown when input validation fails
 */
public class InvalidInputException extends ApiException {
    public InvalidInputException(String message) {
        super(message, "INVALID_INPUT");
    }
}

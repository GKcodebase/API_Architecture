package com.aquaworld.graphql.exception;

/**
 * Custom exception for invalid operations
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}

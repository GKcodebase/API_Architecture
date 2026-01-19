package com.aquaworld.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found
 *
 * HTTP Status: 404 Not Found
 *
 * Usage Examples:
 * - User not found by ID
 * - Product not found
 * - Order not found
 * - Payment not found
 *
 * @author AquaWorld Development Team
 */
public class ResourceNotFoundException extends ApiException {

    /**
     * Constructor for ResourceNotFoundException
     *
     * @param message the error message
     */
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value()); // 404
    }

    /**
     * Constructor with message and cause
     *
     * @param message the error message
     * @param cause the cause exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND.value(), cause);
    }
}

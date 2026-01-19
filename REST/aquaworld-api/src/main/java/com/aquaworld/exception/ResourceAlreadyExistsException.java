package com.aquaworld.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a resource already exists
 *
 * HTTP Status: 409 Conflict
 *
 * Usage Examples:
 * - Username already exists during registration
 * - Email already exists in the system
 * - Duplicate product entry
 *
 * @author AquaWorld Development Team
 */
public class ResourceAlreadyExistsException extends ApiException {

    /**
     * Constructor for ResourceAlreadyExistsException
     *
     * @param message the error message
     */
    public ResourceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT.value()); // 409
    }

    /**
     * Constructor with message and cause
     *
     * @param message the error message
     * @param cause the cause exception
     */
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT.value(), cause);
    }
}

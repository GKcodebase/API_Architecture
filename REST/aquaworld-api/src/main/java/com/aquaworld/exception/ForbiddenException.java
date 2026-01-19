package com.aquaworld.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user lacks permission to perform an action
 *
 * HTTP Status: 403 Forbidden
 *
 * Usage Examples:
 * - User trying to update another user's profile
 * - Regular user trying to access admin endpoints
 * - Insufficient permissions for an operation
 *
 * @author AquaWorld Development Team
 */
public class ForbiddenException extends ApiException {

    /**
     * Constructor for ForbiddenException
     *
     * @param message the error message
     */
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN.value()); // 403
    }

    /**
     * Constructor with message and cause
     *
     * @param message the error message
     * @param cause the cause exception
     */
    public ForbiddenException(String message, Throwable cause) {
        super(message, HttpStatus.FORBIDDEN.value(), cause);
    }
}

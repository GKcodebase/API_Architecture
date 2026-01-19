package com.aquaworld.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when input validation fails
 *
 * HTTP Status: 400 Bad Request
 *
 * Usage Examples:
 * - Missing required fields
 * - Invalid email format
 * - Password too short
 * - Invalid quantity in order
 * - Invalid price value
 *
 * @author AquaWorld Development Team
 */
public class InvalidInputException extends ApiException {

    /**
     * Constructor for InvalidInputException
     *
     * @param message the error message
     */
    public InvalidInputException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value()); // 400
    }

    /**
     * Constructor with message and cause
     *
     * @param message the error message
     * @param cause the cause exception
     */
    public InvalidInputException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST.value(), cause);
    }
}

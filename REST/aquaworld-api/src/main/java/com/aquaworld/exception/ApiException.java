package com.aquaworld.exception;

/**
 * Base custom exception for AquaWorld REST API
 *
 * Extends RuntimeException to make it unchecked.
 * This is the parent class for all custom API exceptions.
 *
 * Usage: Thrown by service layer, caught by GlobalExceptionHandler
 *
 * @author AquaWorld Development Team
 */
public class ApiException extends RuntimeException {

    /** HTTP status code associated with this exception */
    private int statusCode;

    /**
     * Constructor with message only
     *
     * @param message the exception message
     */
    public ApiException(String message) {
        super(message);
        this.statusCode = 500; // Default to internal server error
    }

    /**
     * Constructor with message and status code
     *
     * @param message the exception message
     * @param statusCode the HTTP status code
     */
    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Constructor with message, status code, and cause
     *
     * @param message the exception message
     * @param statusCode the HTTP status code
     * @param cause the cause exception
     */
    public ApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Gets the HTTP status code
     *
     * @return HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the HTTP status code
     *
     * @param statusCode HTTP status code
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}

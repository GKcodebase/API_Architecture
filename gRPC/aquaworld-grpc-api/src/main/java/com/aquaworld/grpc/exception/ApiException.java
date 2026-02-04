package com.aquaworld.grpc.exception;

/**
 * Base exception class for AquaWorld gRPC API
 */
public class ApiException extends RuntimeException {
    private final String errorCode;

    public ApiException(String message) {
        super(message);
        this.errorCode = "INTERNAL_ERROR";
    }

    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "INTERNAL_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}

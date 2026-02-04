package com.aquaworld.grpc.exception;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceName, String id) {
        super(resourceName + " with ID '" + id + "' not found", "RESOURCE_NOT_FOUND");
    }
}

package com.aquaworld.grpc.exception;

/**
 * Exception thrown when attempting to create a resource that already exists
 */
public class ResourceAlreadyExistsException extends ApiException {
    public ResourceAlreadyExistsException(String message) {
        super(message, "RESOURCE_ALREADY_EXISTS");
    }

    public ResourceAlreadyExistsException(String resourceName, String identifier) {
        super(resourceName + " with identifier '" + identifier + "' already exists", "RESOURCE_ALREADY_EXISTS");
    }
}

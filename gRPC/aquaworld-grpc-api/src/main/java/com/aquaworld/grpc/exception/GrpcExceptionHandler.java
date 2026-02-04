package com.aquaworld.grpc.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

/**
 * Exception handler for mapping application exceptions to gRPC Status codes
 */
public class GrpcExceptionHandler {

    private GrpcExceptionHandler() {
    }

    /**
     * Maps an exception to appropriate gRPC Status code
     *
     * @param ex the exception to handle
     * @return StatusRuntimeException with appropriate gRPC status
     */
    public static StatusRuntimeException handle(Exception ex) {
        if (ex instanceof UnauthorizedException) {
            return Status.UNAUTHENTICATED
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        } else if (ex instanceof ForbiddenException) {
            return Status.PERMISSION_DENIED
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        } else if (ex instanceof ResourceNotFoundException) {
            return Status.NOT_FOUND
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        } else if (ex instanceof InvalidInputException) {
            return Status.INVALID_ARGUMENT
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        } else if (ex instanceof ResourceAlreadyExistsException) {
            return Status.ALREADY_EXISTS
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        } else if (ex instanceof ApiException apiEx) {
            return Status.INTERNAL
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        }
        return Status.INTERNAL
                .withDescription("Internal server error: " + ex.getMessage())
                .asRuntimeException();
    }
}

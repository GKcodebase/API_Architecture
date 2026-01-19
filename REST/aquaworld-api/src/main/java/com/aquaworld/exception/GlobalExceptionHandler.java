package com.aquaworld.exception;

import com.aquaworld.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for AquaWorld REST API
 *
 * This centralized exception handler catches and processes all exceptions thrown
 * by controllers and services, converting them into proper HTTP responses.
 *
 * Key Features:
 * - Centralized error handling across the application
 * - Consistent error response format
 * - Proper HTTP status codes (400, 401, 403, 404, 409, 500, 503)
 * - Detailed validation error messages
 * - Stack trace logging for debugging
 *
 * Exception Hierarchy:
 * 1. ApiException (custom exceptions with HTTP status codes)
 * 2. MethodArgumentNotValidException (validation errors)
 * 3. MethodArgumentTypeMismatchException (parameter type errors)
 * 4. Generic Exception (uncaught exceptions -> 500)
 *
 * Flow:
 * Exception thrown in Controller/Service
 *    ↓
 * GlobalExceptionHandler catches it
 *    ↓
 * Appropriate @ExceptionHandler method processes it
 *    ↓
 * Returns ApiResponse with proper HTTP status
 *    ↓
 * Client receives error response
 *
 * @author AquaWorld Development Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom ApiException and its subclasses
     *
     * Caught exceptions:
     * - ResourceNotFoundException (404)
     * - UnauthorizedException (401)
     * - ForbiddenException (403)
     * - ResourceAlreadyExistsException (409)
     * - InvalidInputException (400)
     *
     * @param exception the ApiException that was thrown
     * @param request the HTTP request
     * @return ResponseEntity with error response and appropriate status code
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(
            ApiException exception,
            HttpServletRequest request) {

        // Log the exception for debugging
        logException(request, exception);

        // Create error response with the status code from exception
        ApiResponse<Void> response = ApiResponse.error(
                exception.getMessage(),
                exception.getStatusCode()
        );

        // Return response with appropriate HTTP status
        return new ResponseEntity<>(response, HttpStatus.valueOf(exception.getStatusCode()));
    }

    /**
     * Handles validation errors from @Valid annotation
     *
     * This is thrown when @Valid validation fails on request body
     * Examples:
     * - @NotNull, @NotBlank fields are null/empty
     * - @Email field has invalid email format
     * - @Min, @Max constraints are violated
     *
     * Response includes field-level error details:
     * {
     *   "success": false,
     *   "message": "Validation failed",
     *   "statusCode": 400,
     *   "data": {
     *     "username": "Username is required",
     *     "email": "Email should be a valid email address"
     *   }
     * }
     *
     * @param exception the MethodArgumentNotValidException
     * @param request the HTTP request
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        // Log the exception
        logException(request, exception);

        // Extract field-level error messages
        Map<String, String> errors = new HashMap<>();

        // Iterate through binding result errors
        exception.getBindingResult().getAllErrors().forEach(error -> {
            // Get the field name that failed validation
            String fieldName = ((FieldError) error).getField();

            // Get the validation error message
            String errorMessage = error.getDefaultMessage();

            // Add to errors map
            errors.put(fieldName, errorMessage);
        });

        // Create error response with validation details
        ApiResponse<Map<String, String>> response = ApiResponse.error(
                "Validation failed - Please check your input",
                HttpStatus.BAD_REQUEST.value(),
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles parameter type mismatch errors
     *
     * Thrown when request parameter cannot be converted to expected type
     * Examples:
     * - String "abc" provided for Integer parameter
     * - Invalid date format
     * - Non-numeric ID in path
     *
     * @param exception the MethodArgumentTypeMismatchException
     * @param request the HTTP request
     * @return ResponseEntity with error response
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {

        // Log the exception
        logException(request, exception);

        // Build error message
        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s",
                exception.getValue(),
                exception.getName(),
                exception.getRequiredType().getSimpleName()
        );

        ApiResponse<Void> response = ApiResponse.error(
                message,
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all uncaught exceptions
     *
     * This is the fallback handler for any exception not caught by specific handlers.
     * Returns a generic 500 Internal Server Error response.
     *
     * In production, consider:
     * - Logging full stack trace
     * - Notifying error monitoring service
     * - Masking sensitive information
     *
     * @param exception the generic exception
     * @param request the HTTP request
     * @return ResponseEntity with error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception exception,
            HttpServletRequest request) {

        // Log the exception with full stack trace
        logException(request, exception);

        // Create generic error response
        ApiResponse<Void> response = ApiResponse.error(
                "An unexpected error occurred. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Logs exception details for debugging and monitoring
     *
     * Logs:
     * - HTTP method and request path
     * - Exception class name
     * - Exception message
     * - Stack trace (for debugging)
     *
     * In production, use a proper logging framework (SLF4J, Log4j)
     *
     * @param request the HTTP request
     * @param exception the exception
     */
    private void logException(HttpServletRequest request, Exception exception) {
        System.err.println("=== EXCEPTION OCCURRED ===");
        System.err.println("Method: " + request.getMethod());
        System.err.println("Path: " + request.getRequestURI());
        System.err.println("Exception: " + exception.getClass().getSimpleName());
        System.err.println("Message: " + exception.getMessage());
        exception.printStackTrace();
        System.err.println("==========================");
    }
}

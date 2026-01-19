package com.aquaworld.controller;

import com.aquaworld.dto.LoginRequest;
import com.aquaworld.dto.LoginResponse;
import com.aquaworld.dto.RegisterRequest;
import com.aquaworld.model.User;
import com.aquaworld.response.ApiResponse;
import com.aquaworld.service.AuthenticationService;
import com.aquaworld.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Authentication REST Controller for AquaWorld
 *
 * Provides endpoints for user authentication:
 * - User registration (create new account)
 * - User login (authenticate and get JWT token)
 *
 * Base Path: /api/v1/auth
 * All endpoints are PUBLIC (no JWT required)
 *
 * HTTP Response Status Codes:
 * - 200 OK: Login successful
 * - 201 Created: Registration successful
 * - 400 Bad Request: Invalid input (validation error)
 * - 401 Unauthorized: Invalid credentials (login failed)
 * - 409 Conflict: Username or email already exists
 * - 500 Internal Server Error: Server error
 *
 * Security Notes:
 * - Passwords are encrypted using BCrypt (never stored in plain text)
 * - JWT tokens are issued on successful login
 * - JWT tokens expire after 1 hour
 *
 * @author AquaWorld Development Team
 */
@RestController
@RequestMapping(Constants.AUTH_ENDPOINT)
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Register a new user account
     *
     * Creates a new AquaWorld user account.
     *
     * HTTP Request:
     * POST /api/v1/auth/register
     * Content-Type: application/json
     * {
     *   "username": "johndoe",
     *   "email": "john@example.com",
     *   "password": "SecurePass123",
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "phone": "555-1234"
     * }
     *
     * Success Response (201 Created):
     * {
     *   "success": true,
     *   "message": "Registration successful",
     *   "statusCode": 201,
     *   "data": {
     *     "id": 1001,
     *     "username": "johndoe",
     *     "email": "john@example.com",
     *     "firstName": "John",
     *     "lastName": "Doe"
     *   }
     * }
     *
     * Error Responses:
     * - 400 Bad Request: Missing/invalid fields
     * - 409 Conflict: Username or email already exists
     *
     * @param registerRequest contains username, email, password, firstName, lastName, phone
     * @return ResponseEntity with success message and user data (201 Created)
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new AquaWorld user account")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - validation error"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Username or email already exists"
            )
    })
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Call service to register user
        User registeredUser = authenticationService.register(registerRequest);

        // Return success response (201 Created)
        ApiResponse<User> response = ApiResponse.success(
                Constants.MSG_REGISTRATION_SUCCESS,
                registeredUser,
                HttpStatus.CREATED.value()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * User login endpoint
     *
     * Authenticates user and returns JWT token.
     *
     * HTTP Request:
     * POST /api/v1/auth/login
     * Content-Type: application/json
     * {
     *   "username": "johndoe",
     *   "password": "SecurePass123"
     * }
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Login successful",
     *   "statusCode": 200,
     *   "data": {
     *     "id": 1001,
     *     "username": "johndoe",
     *     "email": "john@example.com",
     *     "firstName": "John",
     *     "lastName": "Doe",
     *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *     "tokenType": "Bearer",
     *     "expiresIn": 3600,
     *     "loginAt": "2025-01-18T10:30:00"
     *   }
     * }
     *
     * Error Response (401 Unauthorized):
     * {
     *   "success": false,
     *   "message": "Invalid username or password",
     *   "statusCode": 401,
     *   "data": null
     * }
     *
     * JWT Token Usage:
     * Add token to all subsequent requests in Authorization header:
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     *
     * @param loginRequest contains username and password
     * @return ResponseEntity with JWT token and user info (200 OK)
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and get JWT token")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful, JWT token returned",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid username or password"
            )
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Call service to authenticate and get JWT token
        LoginResponse loginResponse = authenticationService.login(loginRequest);

        // Return success response (200 OK)
        ApiResponse<LoginResponse> response = ApiResponse.success(
                Constants.MSG_LOGIN_SUCCESS,
                loginResponse,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Health check endpoint
     *
     * Returns API status and store information.
     *
     * HTTP Request:
     * GET /api/v1/auth/health
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "AquaWorld API is running",
     *   "statusCode": 200,
     *   "data": null
     * }
     *
     * @return ResponseEntity with health status
     */
    @GetMapping("/health")
    @Operation(summary = "API health check", description = "Check if API is running")
    public ResponseEntity<ApiResponse<Void>> health() {
        ApiResponse<Void> response = ApiResponse.success(
                "🐠 AquaWorld API is running",
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

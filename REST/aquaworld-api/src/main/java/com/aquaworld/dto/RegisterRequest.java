package com.aquaworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Registration Request DTO for New User Account Creation
 *
 * Used when a new customer registers for AquaWorld.
 * Validates all required fields for account creation.
 *
 * HTTP Request Example:
 * POST /api/v1/auth/register
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
 * Error Response (409 Conflict - Username exists):
 * {
 *   "success": false,
 *   "message": "Username already exists",
 *   "statusCode": 409,
 *   "data": null
 * }
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registration request for new user account")
public class RegisterRequest {

    /**
     * Username for the new account
     *
     * Validation:
     * - Required (not blank)
     * - Length: 3-20 characters
     * - Must be unique in the system
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Schema(description = "Unique username", example = "johndoe")
    private String username;

    /**
     * Email address for the new account
     *
     * Validation:
     * - Required (not blank)
     * - Must be valid email format
     * - Must be unique in the system
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Unique email address", example = "john@example.com")
    private String email;

    /**
     * Password for the new account
     *
     * Validation:
     * - Required (not blank)
     * - Length: 8-50 characters (strong password)
     * - Will be encrypted using BCrypt before storage
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Schema(description = "Password (will be encrypted)", example = "SecurePass123")
    private String password;

    /**
     * User's first name
     *
     * Validation:
     * - Required (not blank)
     * - Max length: 50 characters
     */
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Schema(description = "First name", example = "John")
    private String firstName;

    /**
     * User's last name
     *
     * Validation:
     * - Required (not blank)
     * - Max length: 50 characters
     */
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    /**
     * User's phone number
     *
     * Optional field, but recommended for order notifications
     */
    @Schema(description = "Phone number (optional)", example = "555-1234")
    private String phone;
}

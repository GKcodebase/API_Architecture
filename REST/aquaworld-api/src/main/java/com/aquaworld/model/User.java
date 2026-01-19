package com.aquaworld.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * User Data Model (DBO - Database Object) for AquaWorld REST API
 *
 * Represents a user account in the system.
 * Can be a customer, admin, or vendor.
 *
 * Fields:
 * - id: Unique identifier (auto-generated)
 * - username: Unique username for login
 * - email: Unique email address
 * - password: BCrypt hashed password (never stored in plain text)
 * - firstName: User's first name
 * - lastName: User's last name
 * - address: Delivery address
 * - phone: Contact phone number
 * - role: User role (CUSTOMER, ADMIN, VENDOR)
 * - createdAt: Account creation timestamp
 * - lastLogin: Last login timestamp
 * - active: Whether the account is active
 *
 * Usage:
 * - User registration
 * - User login and authentication
 * - User profile management
 * - Order tracking
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {

    /** Unique user identifier */
    private Long id;

    /** Unique username for login (required) */
    private String username;

    /** Unique email address (required) */
    private String email;

    /** BCrypt hashed password (required, never store plain text) */
    private String password;

    /** User's first name */
    private String firstName;

    /** User's last name */
    private String lastName;

    /** Delivery address */
    private String address;

    /** Contact phone number */
    private String phone;

    /** User role: CUSTOMER, ADMIN, VENDOR */
    private String role;

    /** Account creation timestamp */
    private LocalDateTime createdAt;

    /** Last login timestamp */
    private LocalDateTime lastLogin;

    /** Whether the account is currently active */
    private Boolean active;
}

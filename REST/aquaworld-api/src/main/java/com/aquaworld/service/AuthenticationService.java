package com.aquaworld.service;

import com.aquaworld.dto.LoginRequest;
import com.aquaworld.dto.LoginResponse;
import com.aquaworld.dto.RegisterRequest;
import com.aquaworld.exception.InvalidInputException;
import com.aquaworld.exception.ResourceAlreadyExistsException;
import com.aquaworld.exception.UnauthorizedException;
import com.aquaworld.model.User;
import com.aquaworld.repository.UserRepository;
import com.aquaworld.util.Constants;
import com.aquaworld.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Authentication Service for AquaWorld REST API
 *
 * Handles user authentication operations:
 * - User registration (account creation)
 * - User login (authentication and JWT generation)
 * - Password validation
 * - JWT token generation and validation
 *
 * Security:
 * - Passwords are encrypted using BCrypt
 * - JWT tokens are used for stateless authentication
 * - Validates input before processing
 *
 * Service Layer Responsibilities:
 * - Business logic and validation
 * - Password encryption/matching
 * - Token generation
 * - User lookup and creation
 *
 * Does NOT:
 * - Access database directly (uses repository)
 * - Handle HTTP requests (controller's job)
 * - Return HTTP responses directly
 *
 * @author AquaWorld Development Team
 */
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Register a new user account
     *
     * Process:
     * 1. Validate input (not empty, valid email format)
     * 2. Check if username already exists -> throw 409 Conflict
     * 3. Check if email already exists -> throw 409 Conflict
     * 4. Encrypt password using BCrypt
     * 5. Create user object with CUSTOMER role
     * 6. Save to repository
     * 7. Return user data (without password)
     *
     * @param registerRequest contains username, email, password, firstName, lastName
     * @return User object with generated ID
     * @throws InvalidInputException if input is invalid
     * @throws ResourceAlreadyExistsException if username or email already exists (409)
     */
    public User register(RegisterRequest registerRequest) {
        // Validate input
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            throw new InvalidInputException(Constants.MSG_USERNAME_REQUIRED);
        }

        // Check if username already exists (409 Conflict)
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResourceAlreadyExistsException(Constants.MSG_USERNAME_ALREADY_EXISTS);
        }

        // Check if email already exists (409 Conflict)
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException(Constants.MSG_EMAIL_ALREADY_EXISTS);
        }

        // Create new user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                // Encrypt password using BCrypt (never store plain text)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phone(registerRequest.getPhone())
                // Set role to CUSTOMER by default
                .role(Constants.ROLE_CUSTOMER)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        // Save to repository (ID will be auto-generated)
        return userRepository.save(user);
    }

    /**
     * Authenticate user and generate JWT token
     *
     * Process:
     * 1. Find user by username
     * 2. If not found -> throw 401 Unauthorized
     * 3. Compare provided password with stored encrypted password
     * 4. If password doesn't match -> throw 401 Unauthorized
     * 5. Update last login time
     * 6. Generate JWT token
     * 7. Return login response with token
     *
     * @param loginRequest contains username and password
     * @return LoginResponse with JWT token and user info
     * @throws UnauthorizedException if username not found or password invalid (401)
     */
    public LoginResponse login(LoginRequest loginRequest) {
        // Find user by username
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UnauthorizedException(Constants.MSG_INVALID_CREDENTIALS));

        // Verify password
        // passwordEncoder.matches() compares plain text with BCrypt encrypted password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(Constants.MSG_INVALID_CREDENTIALS);
        }

        // Update last login timestamp
        user.setLastLogin(LocalDateTime.now());
        userRepository.update(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        // Return login response with token
        return LoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .token(token)
                .tokenType("Bearer")
                .expiresIn(3600L) // 1 hour in seconds
                .loginAt(LocalDateTime.now())
                .build();
    }

    /**
     * Validate JWT token
     *
     * Used to check if a token is still valid
     *
     * @param token the JWT token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * Extract username from JWT token
     *
     * Used to identify the authenticated user
     *
     * @param token the JWT token
     * @return username from token
     */
    public String getUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }
}

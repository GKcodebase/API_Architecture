package com.aquaworld.grpc.service;

import com.aquaworld.grpc.model.User;
import com.aquaworld.grpc.exception.UnauthorizedException;
import com.aquaworld.grpc.exception.ResourceAlreadyExistsException;
import com.aquaworld.grpc.repository.UserRepository;
import com.aquaworld.grpc.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service for AquaWorld gRPC API
 * Handles user login, registration, and authentication
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Authenticate user with username and password
     *
     * @param username the username
     * @param password the password
     * @return authenticated User
     * @throws UnauthorizedException if authentication fails
     */
    public User authenticate(String username, String password) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        log.info("User {} authenticated successfully", username);
        return user;
    }

    /**
     * Register a new user
     *
     * @param username the username
     * @param email the email
     * @param password the password
     * @param fullName the full name
     * @return newly created User
     * @throws ResourceAlreadyExistsException if user already exists
     */
    public User register(String username, String email, String password, String fullName) {
        if (userRepository.existsByUsername(username)) {
            throw new ResourceAlreadyExistsException("User", username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException("Email", email);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);

        User savedUser = userRepository.save(user);
        log.info("User {} registered successfully", username);
        return savedUser;
    }

    /**
     * Validate JWT token
     *
     * @param token the JWT token
     * @return User associated with token
     * @throws UnauthorizedException if token is invalid
     */
    public User validateToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}

package com.aquaworld.graphql.service;

import com.aquaworld.graphql.dto.AuthPayload;
import com.aquaworld.graphql.dto.LoginInput;
import com.aquaworld.graphql.dto.RegisterInput;
import com.aquaworld.graphql.exception.DuplicateResourceException;
import com.aquaworld.graphql.exception.InvalidOperationException;
import com.aquaworld.graphql.exception.ResourceNotFoundException;
import com.aquaworld.graphql.model.User;
import com.aquaworld.graphql.repository.UserRepository;
import com.aquaworld.graphql.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Authentication Service
 * Handles user registration, login, and JWT token generation
 */
@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Register a new user
     */
    public AuthPayload register(RegisterInput input) {
        // Check if user already exists
        if (userRepository.existsByUsername(input.getUsername())) {
            throw new DuplicateResourceException("User", "username", input.getUsername());
        }
        
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new DuplicateResourceException("User", "email", input.getEmail());
        }

        // Create new user
        User user = User.builder()
                .username(input.getUsername())
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .phone(input.getPhone())
                .role("CUSTOMER")
                .createdAt(LocalDateTime.now().format(formatter))
                .active(true)
                .build();

        user = userRepository.save(user);

        // Generate token
        String token = jwtTokenProvider.generateTokenFromUsername(user.getUsername());

        return AuthPayload.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationSeconds())
                .loginAt(LocalDateTime.now().format(formatter))
                .build();
    }

    /**
     * Login user
     */
    public AuthPayload login(LoginInput input) {
        User user = userRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", input.getUsername()));

        if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new InvalidOperationException("Invalid password");
        }

        if (!user.getActive()) {
            throw new InvalidOperationException("User account is inactive");
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now().format(formatter));
        userRepository.save(user);

        // Generate token
        String token = jwtTokenProvider.generateTokenFromUsername(user.getUsername());

        return AuthPayload.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationSeconds())
                .loginAt(LocalDateTime.now().format(formatter))
                .build();
    }
}

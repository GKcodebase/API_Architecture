package com.aquaworld.graphql.service;

import com.aquaworld.graphql.dto.UpdateUserInput;
import com.aquaworld.graphql.exception.ResourceNotFoundException;
import com.aquaworld.graphql.model.User;
import com.aquaworld.graphql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * User Service
 * Handles user profile operations
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Get user by ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    /**
     * Get current authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("No authenticated user found");
        }

        String username = authentication.getName();
        return getUserByUsername(username);
    }

    /**
     * Update user profile
     */
    public User updateProfile(Long id, UpdateUserInput input) {
        User user = getUserById(id);

        if (input.getFirstName() != null) {
            user.setFirstName(input.getFirstName());
        }
        if (input.getLastName() != null) {
            user.setLastName(input.getLastName());
        }
        if (input.getPhone() != null) {
            user.setPhone(input.getPhone());
        }
        if (input.getAddress() != null) {
            user.setAddress(input.getAddress());
        }

        return userRepository.save(user);
    }
}

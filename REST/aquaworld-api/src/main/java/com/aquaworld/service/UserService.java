package com.aquaworld.service;

import com.aquaworld.exception.ResourceNotFoundException;
import com.aquaworld.model.User;
import com.aquaworld.repository.UserRepository;
import com.aquaworld.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * User Service for AquaWorld REST API
 *
 * Handles user profile management:
 * - Get user profile
 * - Update user information
 * - Manage user data
 *
 * Responsibilities:
 * - User data retrieval
 * - User profile updates
 * - Validation
 *
 * Does NOT:
 * - Handle authentication (AuthenticationService's job)
 * - Handle HTTP requests
 *
 * @author AquaWorld Development Team
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get user profile by ID
     *
     * @param userId the user ID
     * @return User object
     * @throws ResourceNotFoundException if user not found (404)
     */
    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_USER_NOT_FOUND));
    }

    /**
     * Update user profile
     *
     * @param userId the user ID
     * @param updatedUser the updated user information
     * @return updated User object
     * @throws ResourceNotFoundException if user not found (404)
     */
    public User updateUserProfile(Long userId, User updatedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_USER_NOT_FOUND));

        // Update fields (only non-null values)
        if (updatedUser.getFirstName() != null) {
            user.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            user.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getPhone() != null) {
            user.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getAddress() != null) {
            user.setAddress(updatedUser.getAddress());
        }

        return userRepository.update(user);
    }

    /**
     * Get user by username
     *
     * @param username the username
     * @return User object
     * @throws ResourceNotFoundException if user not found (404)
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_USER_NOT_FOUND));
    }
}

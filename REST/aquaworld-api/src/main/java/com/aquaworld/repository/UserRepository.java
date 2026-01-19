package com.aquaworld.repository;

import com.aquaworld.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;

/**
 * User Repository - In-Memory Data Access Object (DAO)
 *
 * Implements the Data Access Layer for User entities.
 * Uses ConcurrentHashMap for thread-safe in-memory storage.
 * In production, this would connect to a real database.
 *
 * Thread-Safe Storage:
 * - ConcurrentHashMap for thread-safe access
 * - AtomicLong for auto-incrementing IDs
 *
 * Operations:
 * - Create (save), Read (find), Update, Delete
 * - Search by username, email
 * - List all users
 *
 * @author AquaWorld Development Team
 */
@Repository
public class UserRepository {

    // In-memory storage for users: Map<userId, User>
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();

    // Auto-increment ID generator
    private final AtomicLong idGenerator = new AtomicLong(1000);

    /**
     * Save a new user to the repository
     *
     * Generates a new ID if not set, and stores the user.
     *
     * @param user the user to save
     * @return the saved user with generated ID
     */
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.incrementAndGet());
        }
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Find user by ID
     *
     * @param id the user ID
     * @return Optional containing the user if found
     */
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Find user by username
     *
     * Used during login authentication
     *
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    /**
     * Find user by email
     *
     * Used during registration to check for duplicate emails
     *
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    /**
     * Get all users
     *
     * @return list of all users
     */
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Update an existing user
     *
     * @param user the user with updated information
     * @return the updated user
     */
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Delete a user by ID
     *
     * @param id the user ID to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteById(Long id) {
        return users.remove(id) != null;
    }

    /**
     * Check if user exists by username
     *
     * @param username the username to check
     * @return true if username exists
     */
    public boolean existsByUsername(String username) {
        return users.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    /**
     * Check if user exists by email
     *
     * @param email the email to check
     * @return true if email exists
     */
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    /**
     * Get total number of users
     *
     * @return user count
     */
    public long count() {
        return users.size();
    }

    /**
     * Clear all users (useful for testing)
     */
    public void deleteAll() {
        users.clear();
    }
}

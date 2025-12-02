package com.se310.store.repository;

import com.se310.store.data.DataManager;
import com.se310.store.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * User Repository implements Repository Pattern for user data access layer
 * Uses DataManager for persistent storage
 *
 * This repository is completely database-agnostic - it has no knowledge of SQL,
 * ResultSets, or SQLExceptions. All database-specific logic is encapsulated in
 * DataManager.
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 * @since 2025-11-06
 */
public class UserRepository {

    // User persistence layer using Repository Pattern

    private final DataManager dataManager;

    public UserRepository(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Find user by email.
     */
    public Optional<User> findByEmail(String email) {
        return dataManager.getUserByEmail(email);
    }

    /**
     * Get all users.
     */
    public Collection<User> findAll() {
        List<User> users = dataManager.getAllUsers();
        return users;
    }

    /**
     * Save (insert or update) user.
     */
    public User save(User user) {
        return dataManager.persistUser(user);
    }

    /**
     * Check if user exists.
     */
    public boolean existsByEmail(String email) {
        return dataManager.doesUserExist(email);
    }

    /**
     * Delete user by email.
     */
    public boolean deleteByEmail(String email) {
        return dataManager.removeUser(email);
    }
}

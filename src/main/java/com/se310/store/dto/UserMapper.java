package com.se310.store.dto;

import com.se310.store.model.User;
import com.se310.store.model.UserRole;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * UserMapper implements the DTO Pattern for User entities.
 * Provides transformation between User domain objects and DTOs to separate
 * internal representation from API responses (e.g., hiding sensitive data like
 * passwords).
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 * @since 2025-11-11
 */
public class UserMapper {

    // Data Transfer Object + factory methods for User entity

    /**
     * Convert a User domain object to a UserDTO (no password).
     */
    public static UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getEmail(),
                user.getName(),
                user.getRole() != null ? user.getRole().name() : null);
    }

    /**
     * Convert a collection of Users to UserDTOs.
     */
    public static Collection<UserDTO> toDtoList(Collection<User> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert a UserDTO back to a User domain object.
     * Note: password is NOT available in DTO and must be provided separately if
     * needed.
     */
    public static User toDomain(UserDTO dto, String encryptedPassword) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPassword(encryptedPassword);
        if (dto.getRole() != null) {
            user.setRole(UserRole.valueOf(dto.getRole()));
        }
        return user;
    }

    /**
     * UserDTO - Data Transfer Object for User
     */
    public static class UserDTO {
        private String email;
        private String name;
        private String role;

        public UserDTO() {
        }

        public UserDTO(String email, String name, String role) {
            this.email = email;
            this.name = name;
            this.role = role;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}

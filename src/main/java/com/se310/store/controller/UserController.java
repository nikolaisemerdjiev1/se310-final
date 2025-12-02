package com.se310.store.controller;

import com.se310.store.dto.UserMapper;
import com.se310.store.dto.UserMapper.UserDTO;
import com.se310.store.model.User;
import com.se310.store.model.UserRole;
import com.se310.store.service.AuthenticationService;
import com.se310.store.servlet.BaseServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * REST API controller for User operations
 *
 * Demonstrates the Controller component of the MVC Pattern.
 * Controllers handle HTTP requests, delegate to services for business logic,
 * and return DTOs serialized as JSON (no passwords).
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 * @since 2025-11-11
 */
public class UserController extends BaseServlet {

    // Controller for User operations, part of the MVC Pattern

    private final AuthenticationService authenticationService;

    public UserController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Handle GET requests - Returns UserDTO objects (without passwords)
     * - GET /api/v1/users (no parameters) - Get all users
     * - GET /api/v1/users/{email} - Get user by email
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = extractResourceId(request);

        if (email == null) {
            // All users
            Collection<User> users = authenticationService.getAllUsers();
            Collection<UserDTO> dtos = users.stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
            sendJsonResponse(response, dtos, HttpServletResponse.SC_OK);
        } else {
            // Single user
            User user = authenticationService.getUserByEmail(email);
            if (user == null) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found: " + email);
                return;
            }
            UserDTO dto = UserMapper.toDto(user);
            sendJsonResponse(response, dto, HttpServletResponse.SC_OK);
        }
    }

    /**
     * Handle POST requests - Create new user, returns UserDTO (without password)
     * POST /api/v1/users?email=xxx&password=xxx&name=xxx&role=ADMIN|MANAGER|USER
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String roleParam = request.getParameter("role");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "email and password are required");
            return;
        }

        if (authenticationService.userExists(email)) {
            sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, "User already exists: " + email);
            return;
        }

        UserRole role = UserRole.USER;
        if (roleParam != null && !roleParam.isBlank()) {
            try {
                role = UserRole.valueOf(roleParam.toUpperCase());
            } catch (IllegalArgumentException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid role: " + roleParam);
                return;
            }
        }

        try {
            User user = authenticationService.registerUser(email, password, name, role);
            UserDTO dto = UserMapper.toDto(user);
            sendJsonResponse(response, dto, HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Handle PUT requests - Update user information, returns UserDTO (without
     * password)
     * PUT /api/v1/users/{email}?password=xxx&name=xxx
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = extractResourceId(request);
        if (email == null || email.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "email is required in the path");
            return;
        }

        String newPassword = request.getParameter("password");
        String newName = request.getParameter("name");

        if ((newPassword == null || newPassword.isBlank()) &&
                (newName == null || newName.isBlank())) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Nothing to update");
            return;
        }

        User updated = authenticationService.updateUser(email, newPassword, newName);
        if (updated == null) {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found: " + email);
            return;
        }

        UserDTO dto = UserMapper.toDto(updated);
        sendJsonResponse(response, dto, HttpServletResponse.SC_OK);
    }

    /**
     * Handle DELETE requests - Delete user
     * DELETE /api/v1/users/{email}
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = extractResourceId(request);
        if (email == null || email.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "email is required in the path");
            return;
        }

        boolean deleted = authenticationService.deleteUser(email);
        if (!deleted) {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found: " + email);
            return;
        }

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}

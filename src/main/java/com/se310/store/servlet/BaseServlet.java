package com.se310.store.servlet;

import com.se310.store.dto.JsonHelper;
import com.se310.store.dto.JsonSerializable;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Base servlet providing common functionality for all API servlets.
 *
 * This class demonstrates the Template Method Pattern and provides reusable
 * methods for handling HTTP requests and responses in a RESTful manner.
 *
 * Key Design Principles:
 * - Template Method Pattern: Defines the skeleton of HTTP handling
 * - DRY Principle: Common JSON/HTTP logic centralized here
 * - Single Responsibility: Handles HTTP communication concerns
 * - Open/Closed Principle: Open for extension (new DTOs), closed for
 * modification
 * - Dependency Inversion: Depends on JsonSerializable abstraction, not concrete
 * DTOs
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 * @since 2025-11-11
 */
public abstract class BaseServlet extends HttpServlet {

    // TODO: Implement Template Method Pattern for handling HTTP requests and
    // responses

    /**
     * Read the request body as a string.
     * Used for parsing JSON payloads from POST/PUT requests.
     *
     * @param request The HTTP request
     * @return The request body as a string
     * @throws IOException If reading fails
     */
    protected String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    /**
     * Send a JSON response with the given status code.
     *
     * @param response   The HTTP response
     * @param object     The object to serialize to JSON
     * @param statusCode HTTP status code to send
     * @throws IOException If writing fails
     */
    protected void sendJsonResponse(HttpServletResponse response, Object object, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json;
        if (object instanceof JsonSerializable serializable) {
            json = serializable.toJson();
        } else {
            json = JsonHelper.toJson(object);
        }

        try (PrintWriter writer = response.getWriter()) {
            writer.write(json);
            writer.flush();
        }
    }

    /**
     * Send an error response with a message.
     * Uses ErrorResponse's own toJson() method, following the DTO pattern.
     */
    protected void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        ErrorResponse error = new ErrorResponse(statusCode, message);
        sendJsonResponse(response, error, statusCode);
    }

    /**
     * Extract the resource ID from the request path.
     * For example, "/api/v1/deals/DEAL-001" returns "DEAL-001"
     *
     * @param request The HTTP request
     * @return The resource ID, or null if not present
     */
    protected String extractResourceId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }

        // Remove leading slash and extract ID
        String[] parts = pathInfo.substring(1).split("/");
        return parts.length > 0 ? parts[0] : null;
    }

    /**
     * Simple error response object for consistent error formatting.
     */
    @Getter
    private static class ErrorResponse implements JsonSerializable {
        private final int status;
        private final String message;
        private final long timestamp;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toJson() {
            return JsonHelper.toJson(this);
        }
    }

}
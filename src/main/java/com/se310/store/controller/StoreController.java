package com.se310.store.controller;

import com.se310.store.dto.StoreMapper;
import com.se310.store.dto.StoreMapper.StoreDTO;
import com.se310.store.model.Store;
import com.se310.store.model.StoreException;
import com.se310.store.service.StoreService;
import com.se310.store.servlet.BaseServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * REST API controller for Store operations
 *
 * Demonstrates the Controller component of the MVC Pattern.
 * Controllers handle HTTP requests, delegate to services for business logic,
 * and return DTOs serialized as JSON.
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 * @since 2025-11-11
 */
public class StoreController extends BaseServlet {

    // Controller for Store operations, part of the MVC Pattern

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    /**
     * Handle GET requests - Returns StoreDTO objects
     * - GET /api/v1/stores (no parameters) - Get all stores
     * - GET /api/v1/stores/{storeId} - Get store by ID
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String storeId = extractResourceId(request);

        try {
            if (storeId == null) {
                // Get all stores
                Collection<Store> stores = storeService.getAllStores();
                Collection<StoreDTO> dtos = stores.stream()
                        .map(StoreMapper::toDto)
                        .collect(Collectors.toList());
                sendJsonResponse(response, dtos, HttpServletResponse.SC_OK);
            } else {
                // Get single store
                Store store = storeService.showStore(storeId, null);
                StoreDTO dto = StoreMapper.toDto(store);
                sendJsonResponse(response, dto, HttpServletResponse.SC_OK);
            }
        } catch (StoreException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Handle POST requests - Create new store, returns StoreDTO
     * POST /api/v1/stores?storeId=xxx&name=xxx&address=xxx
     * (name is treated as description in the Store model)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String storeId = request.getParameter("storeId");
        String address = request.getParameter("address");
        String description = request.getParameter("description");

        // Fallback: some docs use "name" instead of "description"
        if (description == null || description.isBlank()) {
            description = request.getParameter("name");
        }

        if (storeId == null || storeId.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "storeId is required");
            return;
        }

        try {
            Store store = storeService.provisionStore(storeId, description, address, null);
            StoreDTO dto = StoreMapper.toDto(store);
            sendJsonResponse(response, dto, HttpServletResponse.SC_CREATED);
        } catch (StoreException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Handle PUT requests - Update existing store, returns StoreDTO
     * PUT /api/v1/stores/{storeId}?description=xxx&address=xxx
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String storeId = extractResourceId(request);
        if (storeId == null || storeId.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "storeId is required in the path");
            return;
        }

        String description = request.getParameter("description");
        String address = request.getParameter("address");

        try {
            Store updated = storeService.updateStore(storeId, description, address);
            StoreDTO dto = StoreMapper.toDto(updated);
            sendJsonResponse(response, dto, HttpServletResponse.SC_OK);
        } catch (StoreException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Handle DELETE requests - Delete store
     * DELETE /api/v1/stores/{storeId}
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String storeId = extractResourceId(request);
        if (storeId == null || storeId.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "storeId is required in the path");
            return;
        }

        try {
            storeService.deleteStore(storeId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (StoreException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}

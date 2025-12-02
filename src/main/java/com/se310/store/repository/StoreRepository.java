package com.se310.store.repository;

import com.se310.store.data.DataManager;
import com.se310.store.model.Store;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Store Repository implements Repository Pattern for store data access layer
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
public class StoreRepository {

    // Store persistence layer using Repository Pattern

    private final DataManager dataManager;

    public StoreRepository(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Find store by ID.
     */
    public Optional<Store> findById(String storeId) {
        return dataManager.getStoreById(storeId);
    }

    /**
     * Get all stores.
     */
    public Collection<Store> findAll() {
        List<Store> stores = dataManager.getAllStores();
        return stores;
    }

    /**
     * Save (insert or update) store.
     */
    public Store save(Store store) {
        return dataManager.persistStore(store);
    }

    /**
     * Check if store exists.
     */
    public boolean existsById(String storeId) {
        return dataManager.doesStoreExist(storeId);
    }

    /**
     * Delete store by ID.
     */
    public boolean deleteById(String storeId) {
        return dataManager.removeStore(storeId);
    }
}

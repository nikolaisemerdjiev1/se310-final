package com.se310.store.dto;

import com.se310.store.model.Store;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * StoreMapper implements the DTO Pattern for Store entities.
 * Provides transformation between Store domain objects and DTOs to separate
 * internal representation from API responses (excludes transient collections
 * for cleaner JSON).
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 * @since 2025-11-11
 */
public class StoreMapper {

    // Data Transfer Object + factory methods for Store entity

    /**
     * Convert a Store domain object to a StoreDTO.
     */
    public static StoreDTO toDto(Store store) {
        if (store == null) {
            return null;
        }
        return new StoreDTO(
                store.getId(),
                store.getAddress(),
                store.getDescription());
    }

    /**
     * Convert a collection of Store domain objects to StoreDTOs.
     */
    public static Collection<StoreDTO> toDtoList(Collection<Store> stores) {
        if (stores == null || stores.isEmpty()) {
            return Collections.emptyList();
        }
        return stores.stream()
                .map(StoreMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert a StoreDTO back to a Store domain object.
     * (Useful if you ever accept StoreDTO in requests.)
     */
    public static Store toDomain(StoreDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Store(
                dto.getId(),
                dto.getAddress(),
                dto.getDescription());
    }

    /**
     * StoreDTO - Data Transfer Object for Store
     */
    public static class StoreDTO {
        private String id;
        private String address;
        private String description;

        public StoreDTO() {
        }

        public StoreDTO(String id, String address, String description) {
            this.id = id;
            this.address = address;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}

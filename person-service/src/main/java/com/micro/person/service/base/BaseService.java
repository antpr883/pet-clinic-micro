package com.micro.person.service.base;

import com.micro.person.data.dto.base.BaseDto;
import com.micro.person.data.dto.base.DtoMarker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Base interface for all services with CRUD operations.
 * 
 * Service Layer returns pure DTOs without web-specific wrappers.
 * Controller Layer wraps them in AppResponse/PaginationResponse.
 * 
 * Adapted for our project:
 * - Returns pure DTOs (without AppResponse wrapper)
 * - Soft delete support instead of status enum
 * - Entity Graph via varargs
 * 
 * @param <D> DTO type (must extend BaseDto)
 * @param <R> Request DTO type (must extend DtoMarker)
 */
public interface BaseService<D extends BaseDto, R extends DtoMarker> {
    
    // ========== READ Operations ==========
    
    /**
     * Find by ID with optional Entity Graph.
     * 
     * @param id Entity ID
     * @param graphAttributes attributes to load (e.g., "contacts")
     * @return DTO (pure, without wrapper)
     */
    D findById(Long id, String... graphAttributes);
    
    /**
     * Find all records with optional Entity Graph.
     * 
     * @param graphAttributes attributes to load
     * @return list of DTOs
     */
    List<D> findAll(String... graphAttributes);
    
    /**
     * Find all records with pagination and optional Entity Graph.
     * 
     * @param pageable pagination
     * @param graphAttributes attributes to load
     * @return page of DTOs
     */
    Page<D> findAll(Pageable pageable, String... graphAttributes);
    
    /**
     * Find all active records.
     * 
     * @param graphAttributes attributes to load
     * @return list of DTOs
     */
    List<D> findAllActive(String... graphAttributes);
    
    /**
     * Find all active records with pagination.
     * 
     * @param pageable pagination
     * @param graphAttributes attributes to load
     * @return page of DTOs
     */
    Page<D> findAllActive(Pageable pageable, String... graphAttributes);
    
    /**
     * Find records by list of IDs.
     * 
     * @param ids list of IDs
     * @param graphAttributes attributes to load
     * @return list of DTOs
     */
    List<D> findByIds(List<Long> ids, String... graphAttributes);
    
    /**
     * Check existence of records by list of IDs (bulk check).
     * 
     * Useful for microservice interaction and validation.
     * 
     * @param ids list of IDs to check
     * @return Map where key = ID, value = true if exists, false otherwise
     */
    Map<Long, Boolean> existsByIds(List<Long> ids);
    
    // ========== WRITE Operations ==========
    
    /**
     * Create a new entity.
     * 
     * @param requestDto DTO with data for creation
     * @return created DTO (pure, without wrapper)
     */
    D create(R requestDto);
    
    /**
     * Update existing entity.
     * 
     * @param id Entity ID
     * @param requestDto DTO with data for update
     * @return updated DTO (pure, without wrapper)
     */
    D update(Long id, R requestDto);
    
    /**
     * Delete entity (hard delete).
     * 
     * @param id Entity ID
     */
    void delete(Long id);
    
    /**
     * Soft delete entity (soft delete).
     * 
     * @param id Entity ID
     * @return deleted DTO (pure, without wrapper)
     */
    D softDelete(Long id);
    
    /**
     * Bulk soft delete.
     * 
     * @param ids list of IDs
     * @return number of deleted records
     */
    int softDeleteAll(List<Long> ids);
    
    /**
     * Bulk create records (bulk create).
     * 
     * Useful for data import and microservice interaction.
     * 
     * @param requestDtos list of DTOs for creation
     * @return list of created DTOs
     */
    List<D> createAll(List<R> requestDtos);
    
    /**
     * Check if entity exists.
     * 
     * @param id Entity ID
     * @return true if exists
     */
    boolean existsById(Long id);
    
    /**
     * Check if entity is active.
     * 
     * @param id Entity ID
     * @return true if active
     */
    boolean isActive(Long id);
}

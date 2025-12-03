package com.micro.person.service.person;

import com.micro.person.data.dto.person.PersonCreateDto;
import com.micro.person.data.dto.person.PersonDto;
import com.micro.person.data.dto.person.PersonFullDto;
import com.micro.person.data.dto.person.PersonFullExtendedDto;
import com.micro.person.data.dto.person.PersonFullPreviewDto;
import com.micro.person.data.dto.person.PersonPreviewDto;
import com.micro.person.data.dto.person.PersonUpdateDto;
import com.micro.person.service.base.BaseService;
import com.micro.person.service.base.PreviewBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Optimized Service for managing persons.
 * 
 * Version: 2.0 (Refactored - Clean API)
 * 
 * Main changes:
 * - Removed duplicate endpoints (with-contacts, with-addresses, etc.)
 * - Added FullPreview API for aggregated view
 * - RSQL as the only search mechanism
 * - Clear contracts for each endpoint
 */
public interface PersonService extends 
        BaseService<PersonDto, PersonDto>,  // PersonDto is also used for request (legacy)
        PreviewBaseService<PersonPreviewDto> {
    
    /**
     * Get full person information by ID.
     * Always returns PersonFullDto with contacts and addresses.
     * 
     * @param id Person ID
     * @return PersonFullDto with contacts and addresses
     */
    PersonFullDto findFullById(Long id);
    
    /**
     * Get Preview person by ID.
     * 
     * @param id Person ID
     * @return PersonPreviewDto
     */
    PersonPreviewDto findPreviewById(Long id);
    
    /**
     * Get FullPreview person by ID.
     * Returns aggregated view with primary contacts and address.
     * 
     * @param id Person ID
     * @return PersonFullPreviewDto
     */
    PersonFullPreviewDto findFullPreviewById(Long id);
    
    /**
     * Get full extended person information by ID.
     * 
     * @param id Person ID
     * @return PersonFullExtendedDto with all relationships
     */
    PersonFullExtendedDto findFullExtendedById(Long id);
    
    /**
     * Create a new person from PersonCreateDto.
     * 
     * @param createDto DTO for creation
     * @return created PersonFullDto
     */
    PersonFullDto create(PersonCreateDto createDto);
    
    /**
     * Update person from PersonUpdateDto.
     * 
     * @param id Person ID
     * @param updateDto DTO for update
     * @return updated PersonFullDto
     */
    PersonFullDto update(Long id, PersonUpdateDto updateDto);
    
    /**
     * Count active persons.
     * 
     * @return number of active persons
     */
    long countActive();
    
    // ========== List All Methods ==========
    
    /**
     * Get all persons (Full).
     * Always returns PersonFullDto with contacts and addresses.
     * 
     * @return list of PersonFullDto
     */
    List<PersonFullDto> findAllFull();
    
    /**
     * Get all persons (Full) with pagination.
     * Always returns PersonFullDto with contacts and addresses.
     * 
     * @param pageable pagination
     * @return page of PersonFullDto
     */
    Page<PersonFullDto> findAllFullWithPagination(Pageable pageable);
    
    /**
     * Get all persons (Preview).
     * 
     * @return list of PersonPreviewDto
     */
    List<PersonPreviewDto> findAllPreviews();
    
    /**
     * Get all persons (Preview) with pagination.
     * 
     * @param pageable pagination
     * @return page of PersonPreviewDto
     */
    Page<PersonPreviewDto> findAllPreviewsWithPagination(Pageable pageable);
    
    // ========== RSQL Search Methods ==========
    
    /**
     * Universal search with RSQL query.
     * Always returns PersonFullDto with contacts and addresses.
     * 
     * @param rsqlQuery RSQL query string
     * @return list of PersonFullDto
     */
    List<PersonFullDto> search(String rsqlQuery);
    
    /**
     * Search with pagination via RSQL.
     * Always returns PersonFullDto with contacts and addresses.
     * 
     * @param rsqlQuery RSQL query string
     * @param pageable pagination
     * @return page of PersonFullDto
     */
    Page<PersonFullDto> searchWithPagination(String rsqlQuery, Pageable pageable);
    
    /**
     * Search Preview persons via RSQL.
     * 
     * @param rsqlQuery RSQL query string
     * @return list of PersonPreviewDto
     */
    List<PersonPreviewDto> searchPreview(String rsqlQuery);
    
    /**
     * Search Preview persons via RSQL with pagination.
     * 
     * @param rsqlQuery RSQL query string
     * @param pageable pagination
     * @return page of PersonPreviewDto
     */
    Page<PersonPreviewDto> searchPreviewWithPagination(String rsqlQuery, Pageable pageable);
    
    /**
     * Search FullPreview persons via RSQL.
     * Returns aggregated view with primary contacts and address.
     * 
     * @param rsqlQuery RSQL query string
     * @return list of PersonFullPreviewDto
     */
    List<PersonFullPreviewDto> searchFullPreview(String rsqlQuery);
    
    /**
     * Search FullPreview persons via RSQL with pagination.
     * 
     * @param rsqlQuery RSQL query string
     * @param pageable pagination
     * @return page of PersonFullPreviewDto
     */
    Page<PersonFullPreviewDto> searchFullPreviewWithPagination(String rsqlQuery, Pageable pageable);
    
    /**
     * Get all FullPreview persons.
     * 
     * @return list of PersonFullPreviewDto
     */
    List<PersonFullPreviewDto> findAllFullPreviews();
    
    /**
     * Get all FullPreview persons with pagination.
     * 
     * @param pageable pagination
     * @return page of PersonFullPreviewDto
     */
    Page<PersonFullPreviewDto> findAllFullPreviewsWithPagination(Pageable pageable);
    
    // ========== Microservice Integration Methods ==========
    
    /**
     * Check if persons exist by list of IDs (for other microservices).
     * 
     * @param ids list of person IDs
     * @return Map where key = ID, value = true if exists and active
     */
    Map<Long, Boolean> checkPersonsExist(List<Long> ids);
    
    /**
     * Get Preview persons by list of IDs (for other microservices).
     * 
     * @param ids list of person IDs
     * @return list of PersonPreviewDto
     */
    List<PersonPreviewDto> getPreviewsByIds(List<Long> ids);
}


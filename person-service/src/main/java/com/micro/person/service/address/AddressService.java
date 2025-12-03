package com.micro.person.service.address;

import com.micro.person.data.dto.address.AddressCreateDto;
import com.micro.person.data.dto.address.AddressDto;
import com.micro.person.data.dto.address.AddressPreviewDto;
import com.micro.person.data.dto.address.AddressUpdateDto;
import com.micro.person.service.base.BaseService;
import com.micro.person.service.base.PreviewBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Optimized Service for managing addresses.
 * 
 * Version: 1.0 (Optimized)
 * 
 * Main changes:
 * - Use of AddressCreateDto and AddressUpdateDto
 * - RSQL as the only search mechanism
 * - Minimal set of methods without duplicates
 */
public interface AddressService extends 
        BaseService<AddressDto, AddressDto>,
        PreviewBaseService<AddressPreviewDto> {
    
    /**
     * Create a new address from AddressCreateDto.
     * 
     * @param createDto DTO for creation
     * @return created AddressDto
     */
    AddressDto create(AddressCreateDto createDto);
    
    /**
     * Update address from AddressUpdateDto.
     * 
     * @param id Address ID
     * @param updateDto DTO for update
     * @return updated AddressDto
     */
    AddressDto update(Long id, AddressUpdateDto updateDto);
    
    /**
     * Find person's addresses.
     * 
     * @param personId Person ID
     * @return list of AddressDto
     */
    List<AddressDto> findByPersonId(Long personId);
    
    /**
     * Find person's primary address (HOME type).
     * 
     * @param personId Person ID
     * @return Optional with AddressDto
     */
    Optional<AddressDto> findPrimaryByPersonId(Long personId);
    
    /**
     * Find person's primary address (Preview version).
     * Used for GET /addresses/persons/{personId}/primary endpoint.
     * 
     * @param personId Person ID
     * @return Optional with AddressPreviewDto
     */
    Optional<AddressPreviewDto> findPrimaryPreviewByPersonId(Long personId);
    
    // ========== RSQL Search Methods ==========
    
    /**
     * Universal search for addresses with RSQL query.
     * 
     * Query examples:
     * - "addressType==HOME" - by type
     * - "city=ilike='%kyiv%'" - case-insensitive LIKE
     * - "person.id==1" - by person
     * - "country==Ukraine;active==true" - AND
     * - "city==Kyiv" - exact match
     * 
     * Replaces all custom search endpoints (/by-city, /by-country, /active, etc.).
     * 
     * @param rsqlQuery RSQL query string
     * @return list of AddressDto
     */
    List<AddressDto> search(String rsqlQuery);
    
    /**
     * Search with pagination via RSQL.
     * 
     * Replaces all paginated endpoints (/paginated, /active/paginated, etc.).
     * 
     * @param rsqlQuery RSQL query string
     * @param pageable pagination
     * @return page of AddressDto
     */
    Page<AddressDto> searchWithPagination(String rsqlQuery, Pageable pageable);
}


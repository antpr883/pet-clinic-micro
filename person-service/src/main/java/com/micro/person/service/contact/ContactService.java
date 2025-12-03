package com.micro.person.service.contact;

import com.micro.person.data.dto.contact.ContactCreateDto;
import com.micro.person.data.dto.contact.ContactDto;
import com.micro.person.data.dto.contact.ContactPreviewDto;
import com.micro.person.data.dto.contact.ContactUpdateDto;
import com.micro.person.service.base.BaseService;
import com.micro.person.service.base.PreviewBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Optimized Service for managing contacts.
 * 
 * Version: 1.0 (Optimized)
 * 
 * Main changes:
 * - Use of ContactCreateDto and ContactUpdateDto
 * - RSQL as the only search mechanism
 * - Minimal set of methods without duplicates
 */
public interface ContactService extends 
        BaseService<ContactDto, ContactDto>,
        PreviewBaseService<ContactPreviewDto> {
    
    /**
     * Create a new contact from ContactCreateDto.
     * 
     * @param createDto DTO for creation
     * @return created ContactDto
     */
    ContactDto create(ContactCreateDto createDto);
    
    /**
     * Update contact from ContactUpdateDto.
     * 
     * @param id Contact ID
     * @param updateDto DTO for update
     * @return updated ContactDto
     */
    ContactDto update(Long id, ContactUpdateDto updateDto);
    
    /**
     * Find person's contacts.
     * 
     * @param personId Person ID
     * @return list of ContactDto
     */
    List<ContactDto> findByPersonId(Long personId);
    
    /**
     * Find person's primary contact.
     * 
     * @param personId Person ID
     * @return Optional with ContactDto
     */
    Optional<ContactDto> findPrimaryByPersonId(Long personId);
    
    /**
     * Find person's primary contact (Preview version).
     * Used for GET /contacts/persons/{personId}/primary endpoint.
     * 
     * @param personId Person ID
     * @return Optional with ContactPreviewDto
     */
    Optional<ContactPreviewDto> findPrimaryPreviewByPersonId(Long personId);
    
    // ========== RSQL Search Methods ==========
    
    /**
     * Universal search for contacts with RSQL query.
     * 
     * Query examples:
     * - "contactType==EMAIL" - by type
     * - "value=ilike='%@gmail.com%'" - case-insensitive LIKE
     * - "person.id==1" - by person
     * - "isPrimary==true;active==true" - AND
     * - "value==+380123456789" - exact match
     * 
     * Replaces all custom search endpoints (/by-email, /by-phone, /active, etc.).
     * 
     * @param rsqlQuery RSQL query string
     * @return list of ContactDto
     */
    List<ContactDto> search(String rsqlQuery);
    
    /**
     * Search with pagination via RSQL.
     * 
     * Replaces all paginated endpoints (/paginated, /active/paginated, etc.).
     * 
     * @param rsqlQuery RSQL query string
     * @param pageable pagination
     * @return page of ContactDto
     */
    Page<ContactDto> searchWithPagination(String rsqlQuery, Pageable pageable);
}


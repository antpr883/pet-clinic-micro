package com.micro.pet.service.pet;

import com.micro.pet.data.dto.pet.*;
import com.micro.pet.service.base.BaseService;
import com.micro.pet.service.base.PreviewBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service for managing pets.
 */
public interface PetService extends 
        BaseService<PetFullDto, PetCreateDto>,  
        PreviewBaseService<PetPreviewDto> {
    
    /**
     * Get full pet information by ID.
     * Always returns PetFullDto with type and details.
     */
    PetFullDto findFullById(Long id);
    
    /**
     * Get pet preview by ID.
     */
    PetPreviewDto findPreviewById(Long id);
    
    /**
     * Create new pet from PetCreateDto.
     */
    PetFullDto create(PetCreateDto createDto);
    
    /**
     * Update pet from PetUpdateDto.
     */
    PetFullDto update(Long id, PetUpdateDto updateDto);
    
    /**
     * Find all pets of a specific owner.
     */
    List<PetPreviewDto> findByOwnerId(Long ownerId);
    
    /**
     * RSQL search for pets.
     */
    List<PetFullDto> search(String rsqlQuery);
    
    /**
     * RSQL search for pets with pagination.
     */
    Page<PetFullDto> searchWithPagination(String rsqlQuery, Pageable pageable);
    
    /**
     * RSQL search for pets (Preview).
     */
    List<PetPreviewDto> searchPreview(String rsqlQuery);
    
    /**
     * RSQL search for pets (Preview) with pagination.
     */
    Page<PetPreviewDto> searchPreviewWithPagination(String rsqlQuery, Pageable pageable);
    
    /**
     * Bulk check pet existence.
     */
    Map<Long, Boolean> checkExistence(List<Long> ids);
    
    /**
     * Get Preview pets by list of IDs.
     */
    List<PetPreviewDto> findPreviewsByIds(List<Long> ids);
    
    /**
     * Count active pets.
     */
    long countActive();
    
    /**
     * Count pets by type.
     */
    Map<String, Long> countByType();
}


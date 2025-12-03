package com.micro.person.service.base;

import com.micro.person.data.dto.base.BasePreviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface for Preview operations (lightweight DTOs for lists).
 * 
 * Preview DTOs contain minimal set of fields for performance optimization
 * when working with lists.
 * 
 * @param <P> Preview DTO type (must extend BasePreviewDto)
 */
public interface PreviewBaseService<P extends BasePreviewDto> {
    
    /**
     * Find Preview by ID.
     * 
     * @param id Entity ID
     * @param graphAttributes attributes to load
     * @return Preview DTO
     */
    P findPreviewById(Long id, String... graphAttributes);
    
    /**
     * Find all Preview.
     * 
     * @param graphAttributes attributes to load
     * @return list of Preview DTOs
     */
    List<P> findAllPreview(String... graphAttributes);
    
    /**
     * Find all Preview with pagination.
     * 
     * @param pageable pagination
     * @param graphAttributes attributes to load
     * @return page of Preview DTOs
     */
    Page<P> findAllPreview(Pageable pageable, String... graphAttributes);
    
    /**
     * Find Preview by list of IDs.
     * 
     * @param ids list of IDs
     * @param graphAttributes attributes to load
     * @return list of Preview DTOs
     */
    List<P> findPreviewsByIds(List<Long> ids, String... graphAttributes);
    
    /**
     * Find all active Preview.
     * 
     * @param graphAttributes attributes to load
     * @return list of Preview DTOs
     */
    List<P> findAllActivePreview(String... graphAttributes);
    
    /**
     * Find all active Preview with pagination.
     * 
     * @param pageable pagination
     * @param graphAttributes attributes to load
     * @return page of Preview DTOs
     */
    Page<P> findAllActivePreview(Pageable pageable, String... graphAttributes);
}


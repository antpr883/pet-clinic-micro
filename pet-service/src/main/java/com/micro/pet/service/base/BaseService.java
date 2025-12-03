package com.micro.pet.service.base;

import com.micro.pet.data.dto.base.BaseDto;
import com.micro.pet.data.dto.base.DtoMarker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Base interface for all services with CRUD operations.
 * 
 * Service Layer returns pure DTOs without web-specific wrappers.
 * Controller Layer wraps them in AppResponse/PaginationResponse.
 */
public interface BaseService<D extends BaseDto, R extends DtoMarker> {
    
    D findById(Long id, String... graphAttributes);
    List<D> findAll(String... graphAttributes);
    Page<D> findAll(Pageable pageable, String... graphAttributes);
    List<D> findAllActive(String... graphAttributes);
    Page<D> findAllActive(Pageable pageable, String... graphAttributes);
    List<D> findByIds(List<Long> ids, String... graphAttributes);
    Map<Long, Boolean> existsByIds(List<Long> ids);
    D create(R requestDto);
    D update(Long id, R requestDto);
    void delete(Long id);
    D softDelete(Long id);
    int softDeleteAll(List<Long> ids);
    List<D> createAll(List<R> requestDtos);
    boolean existsById(Long id);
    boolean isActive(Long id);
}


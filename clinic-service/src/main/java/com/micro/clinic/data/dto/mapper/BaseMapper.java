package com.micro.clinic.data.dto.mapper;

import com.micro.clinic.data.dto.base.BaseDto;
import com.micro.clinic.data.dto.base.BasePreviewDto;
import com.micro.clinic.data.dto.base.DtoMarker;
import com.micro.clinic.data.entities.BaseActiveEntity;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Base interface for all Mappers.
 * 
 * Provides type-safe method calls without reflection.
 * 
 * @param <E> Entity type (must extend BaseActiveEntity)
 * @param <D> DTO type (must extend BaseDto)
 * @param <P> Preview DTO type (must extend BasePreviewDto)
 * @param <R> Request DTO type (must extend DtoMarker)
 */
public interface BaseMapper<
        E extends BaseActiveEntity,
        D extends BaseDto,
        P extends BasePreviewDto,
        R extends DtoMarker> {
    
    /**
     * Converts Entity to DTO.
     */
    D toDto(E entity);
    
    /**
     * Converts Entity to Preview DTO.
     */
    P toPreviewDto(E entity);
    
    /**
     * Converts Request DTO to Entity.
     */
    E toEntity(R requestDto);
    
    /**
     * Updates Entity from Request DTO (partial update).
     */
    void updateEntityFromDto(R requestDto, @MappingTarget E entity);
    
    /**
     * Converts list of Entity to list of DTO.
     */
    List<D> toDtoList(List<E> entities);
    
    /**
     * Converts list of Entity to list of Preview DTO.
     */
    List<P> toPreviewDtoList(List<E> entities);
}


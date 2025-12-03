package com.micro.person.data.dto.mapper;

import com.micro.person.data.dto.base.BaseDto;
import com.micro.person.data.dto.base.BasePreviewDto;
import com.micro.person.data.dto.base.DtoMarker;
import com.micro.person.data.entities.BaseActiveEntity;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Базовий інтерфейс для всіх Mapper'ів.
 * 
 * Забезпечує type-safe виклики методів без reflection.
 * 
 * @param <E> тип Entity (має extends BaseActiveEntity)
 * @param <D> тип DTO (має extends BaseDto)
 * @param <P> тип Preview DTO (має extends BasePreviewDto)
 * @param <R> тип Request DTO (має extends DtoMarker)
 */
public interface BaseMapper<
        E extends BaseActiveEntity,
        D extends BaseDto,
        P extends BasePreviewDto,
        R extends DtoMarker> {
    
    /**
     * Конвертує Entity в DTO.
     */
    D toDto(E entity);
    
    /**
     * Конвертує Entity в Preview DTO.
     */
    P toPreviewDto(E entity);
    
    /**
     * Конвертує Request DTO в Entity.
     */
    E toEntity(R requestDto);
    
    /**
     * Оновлює Entity з Request DTO (partial update).
     */
    void updateEntityFromDto(R requestDto, @MappingTarget E entity);
    
    /**
     * Конвертує список Entity в список DTO.
     */
    List<D> toDtoList(List<E> entities);
    
    /**
     * Конвертує список Entity в список Preview DTO.
     */
    List<P> toPreviewDtoList(List<E> entities);
}


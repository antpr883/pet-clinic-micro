package com.micro.person.data.dto.mapper;

import com.micro.person.data.entities.AddressEntity;
import com.micro.person.data.dto.address.*;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface AddressMapper extends BaseMapper<
    AddressEntity, 
    AddressDto, 
    AddressPreviewDto, 
    AddressDto> {

    @Override
    AddressDto toDto(AddressEntity entity);

    @Override
    AddressPreviewDto toPreviewDto(AddressEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    AddressEntity toEntity(AddressDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    void updateEntityFromDto(AddressDto dto, @MappingTarget AddressEntity entity);

    @Override
    List<AddressDto> toDtoList(List<AddressEntity> entities);

    @Override
    List<AddressPreviewDto> toPreviewDtoList(List<AddressEntity> entities);
    
    Set<AddressDto> toDtoSet(Set<AddressEntity> entities);
    Set<AddressPreviewDto> toPreviewDtoSet(Set<AddressEntity> entities);
    Set<AddressEntity> toEntitySet(Set<AddressDto> dtos);
    
    // ========== AddressCreateDto Mapping ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    AddressEntity fromCreateDto(AddressCreateDto dto);
    
    // ========== AddressUpdateDto Mapping ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateFromUpdateDto(AddressUpdateDto dto, @MappingTarget AddressEntity entity);
    
    /**
     * Створити нову AddressEntity з AddressUpdateDto.
     * Використовується при оновленні PersonEntity з PersonUpdateDto.
     * Всі поля з UpdateDto копіюються в нову entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    AddressEntity fromUpdateDto(AddressUpdateDto dto);
}


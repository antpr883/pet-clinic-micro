package com.micro.pet.data.dto.mapper;

import com.micro.pet.data.dto.pettype.PetTypeCreateDto;
import com.micro.pet.data.dto.pettype.PetTypeDto;
import com.micro.pet.data.dto.pettype.PetTypePreviewDto;
import com.micro.pet.data.dto.pettype.PetTypeUpdateDto;
import com.micro.pet.data.entities.PetTypeEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PetTypeMapper extends BaseMapper<
    PetTypeEntity, 
    PetTypeDto, 
    PetTypePreviewDto,
    PetTypeCreateDto> {

    @Override
    PetTypeDto toDto(PetTypeEntity entity);

    @Override
    PetTypePreviewDto toPreviewDto(PetTypeEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Override
    PetTypeEntity toEntity(PetTypeCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Override
    void updateEntityFromDto(PetTypeCreateDto dto, @MappingTarget PetTypeEntity entity);

    @Override
    List<PetTypeDto> toDtoList(List<PetTypeEntity> entities);

    @Override
    List<PetTypePreviewDto> toPreviewDtoList(List<PetTypeEntity> entities);

    // ========== PetTypeCreateDto Mapping ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    PetTypeEntity fromCreateDto(PetTypeCreateDto dto);
    
    // ========== PetTypeUpdateDto Mapping ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateFromUpdateDto(PetTypeUpdateDto dto, @MappingTarget PetTypeEntity entity);
}


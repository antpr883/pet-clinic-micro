package com.micro.pet.data.dto.mapper;

import com.micro.pet.data.dto.pet.*;
import com.micro.pet.data.entities.PetEntity;
import com.micro.pet.data.entities.PetInfoEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {PetTypeMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PetMapper extends BaseMapper<
    PetEntity, 
    PetFullDto, 
    PetPreviewDto, 
    PetCreateDto> {

    @Mapping(target = "details", source = "info.details")
    @Named("toDto")
    @Override
    PetFullDto toDto(PetEntity entity);

    @Mapping(target = "typeName", source = "type.typeName")
    @Override
    PetPreviewDto toPreviewDto(PetEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "info", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Override
    PetEntity toEntity(PetCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "info", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Override
    void updateEntityFromDto(PetCreateDto dto, @MappingTarget PetEntity entity);

    @Override
    List<PetFullDto> toDtoList(List<PetEntity> entities);

    @Override
    List<PetPreviewDto> toPreviewDtoList(List<PetEntity> entities);
    
    // ========== PetCreateDto Mapping ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "info", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    PetEntity fromCreateDto(PetCreateDto dto);
    
    // ========== PetUpdateDto Mapping ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "info", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateFromUpdateDto(PetUpdateDto dto, @MappingTarget PetEntity entity);
}


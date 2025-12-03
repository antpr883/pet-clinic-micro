package com.micro.pet.data.dto.mapper;

import com.micro.pet.data.dto.petinfo.PetInfoDto;
import com.micro.pet.data.dto.petinfo.PetInfoUpdateDto;
import com.micro.pet.data.entities.PetInfoEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PetInfoMapper {
    
    @Mapping(target = "petId", source = "pet.id")
    PetInfoDto toDto(PetInfoEntity entity);
    
    List<PetInfoDto> toDtoList(List<PetInfoEntity> entities);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateFromUpdateDto(PetInfoUpdateDto dto, @MappingTarget PetInfoEntity entity);
}


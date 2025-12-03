package com.micro.clinic.data.dto.mapper;

import com.micro.clinic.data.dto.clinicvisit.ClinicVisitCreateDto;
import com.micro.clinic.data.dto.clinicvisit.ClinicVisitDto;
import com.micro.clinic.data.entities.ClinicVisitEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ClinicVisitMapper {
    
    @Mapping(target = "clinicCaseId", source = "clinicCase.id")
    ClinicVisitDto toDto(ClinicVisitEntity entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clinicCase", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    ClinicVisitEntity toEntity(ClinicVisitCreateDto dto);
    
    List<ClinicVisitDto> toDtoList(List<ClinicVisitEntity> entities);
}


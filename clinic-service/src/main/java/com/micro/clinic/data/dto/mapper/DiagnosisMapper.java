package com.micro.clinic.data.dto.mapper;

import com.micro.clinic.data.dto.diagnosis.DiagnosisCreateDto;
import com.micro.clinic.data.dto.diagnosis.DiagnosisDto;
import com.micro.clinic.data.dto.diagnosis.DiagnosisUpdateDto;
import com.micro.clinic.data.entities.DiagnosisEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface DiagnosisMapper {
    
    @Mapping(target = "clinicCaseId", source = "clinicCase.id")
    DiagnosisDto toDto(DiagnosisEntity entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clinicCase", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    DiagnosisEntity toEntity(DiagnosisCreateDto dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clinicCase", ignore = true)
    @Mapping(target = "diagnosedByVetId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateFromUpdateDto(DiagnosisUpdateDto dto, @MappingTarget DiagnosisEntity entity);
    
    List<DiagnosisDto> toDtoList(List<DiagnosisEntity> entities);
}


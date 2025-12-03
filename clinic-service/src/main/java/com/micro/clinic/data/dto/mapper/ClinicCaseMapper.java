package com.micro.clinic.data.dto.mapper;

import com.micro.clinic.data.dto.cliniccase.*;
import com.micro.clinic.data.entities.ClinicCaseEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {
        ClinicVisitMapper.class,
        DiagnosisMapper.class,
        ProcedureMapper.class,
        HospitalizationMapper.class
    },
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ClinicCaseMapper extends BaseMapper<
    ClinicCaseEntity, 
    ClinicCaseFullDto, 
    ClinicCasePreviewDto, 
    ClinicCaseCreateDto> {

    @Named("toDto")
    @Override
    ClinicCaseFullDto toDto(ClinicCaseEntity entity);

    @Override
    ClinicCasePreviewDto toPreviewDto(ClinicCaseEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "diagnoses", ignore = true)
    @Mapping(target = "procedures", ignore = true)
    @Mapping(target = "hospitalization", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Override
    ClinicCaseEntity toEntity(ClinicCaseCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "diagnoses", ignore = true)
    @Mapping(target = "procedures", ignore = true)
    @Mapping(target = "hospitalization", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Override
    void updateEntityFromDto(ClinicCaseCreateDto dto, @MappingTarget ClinicCaseEntity entity);

    @Override
    List<ClinicCaseFullDto> toDtoList(List<ClinicCaseEntity> entities);

    @Override
    List<ClinicCasePreviewDto> toPreviewDtoList(List<ClinicCaseEntity> entities);
    
    // ========== ClinicCaseCreateDto Mapping ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "diagnoses", ignore = true)
    @Mapping(target = "procedures", ignore = true)
    @Mapping(target = "hospitalization", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    ClinicCaseEntity fromCreateDto(ClinicCaseCreateDto dto);
    
    // ========== ClinicCaseUpdateDto Mapping ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "diagnoses", ignore = true)
    @Mapping(target = "procedures", ignore = true)
    @Mapping(target = "hospitalization", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateFromUpdateDto(ClinicCaseUpdateDto dto, @MappingTarget ClinicCaseEntity entity);
}


package com.micro.clinic.data.dto.mapper;

import com.micro.clinic.data.dto.procedure.ProcedureCreateDto;
import com.micro.clinic.data.dto.procedure.ProcedureDto;
import com.micro.clinic.data.entities.ProcedureEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProcedureMapper {
    
    @Mapping(target = "clinicCaseId", source = "clinicCase.id")
    ProcedureDto toDto(ProcedureEntity entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clinicCase", ignore = true)
    @Mapping(target = "completedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    ProcedureEntity toEntity(ProcedureCreateDto dto);
    
    List<ProcedureDto> toDtoList(List<ProcedureEntity> entities);
}


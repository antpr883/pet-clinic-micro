package com.micro.clinic.data.dto.mapper;

import com.micro.clinic.data.dto.hospitalization.HospitalizationCreateDto;
import com.micro.clinic.data.dto.hospitalization.HospitalizationDto;
import com.micro.clinic.data.entities.HospitalizationEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {DailyCheckMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface HospitalizationMapper {
    
    @Mapping(target = "clinicCaseId", source = "clinicCase.id")
    HospitalizationDto toDto(HospitalizationEntity entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clinicCase", ignore = true)
    @Mapping(target = "endAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dailyChecks", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    HospitalizationEntity toEntity(HospitalizationCreateDto dto);
    
    List<HospitalizationDto> toDtoList(List<HospitalizationEntity> entities);
}


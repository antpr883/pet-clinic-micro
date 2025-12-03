package com.micro.clinic.data.dto.mapper;

import com.micro.clinic.data.dto.dailycheck.DailyCheckCreateDto;
import com.micro.clinic.data.dto.dailycheck.DailyCheckDto;
import com.micro.clinic.data.entities.DailyCheckEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface DailyCheckMapper {
    
    @Mapping(target = "hospitalizationId", source = "hospitalization.id")
    DailyCheckDto toDto(DailyCheckEntity entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hospitalization", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    DailyCheckEntity toEntity(DailyCheckCreateDto dto);
    
    List<DailyCheckDto> toDtoList(List<DailyCheckEntity> entities);
}


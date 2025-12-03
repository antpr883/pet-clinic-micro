package com.micro.person.data.dto.mapper;

import com.micro.person.data.entities.ContactEntity;
import com.micro.person.data.dto.contact.*;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ContactMapper extends BaseMapper<
    ContactEntity, 
    ContactDto, 
    ContactPreviewDto, 
    ContactDto> {

    @Override
    ContactDto toDto(ContactEntity entity);

    @Override
    ContactPreviewDto toPreviewDto(ContactEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    ContactEntity toEntity(ContactDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    void updateEntityFromDto(ContactDto dto, @MappingTarget ContactEntity entity);

    @Override
    List<ContactDto> toDtoList(List<ContactEntity> entities);

    @Override
    List<ContactPreviewDto> toPreviewDtoList(List<ContactEntity> entities);
    
    // Додаткові методи для Set (якщо потрібно)
    Set<ContactDto> toDtoSet(Set<ContactEntity> entities);
    Set<ContactPreviewDto> toPreviewDtoSet(Set<ContactEntity> entities);
    Set<ContactEntity> toEntitySet(Set<ContactDto> dtos);
    
    // ========== ContactCreateDto Mapping ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    ContactEntity fromCreateDto(ContactCreateDto dto);
    
    // ========== ContactUpdateDto Mapping ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateFromUpdateDto(ContactUpdateDto dto, @MappingTarget ContactEntity entity);
    
    /**
     * Створити нову ContactEntity з ContactUpdateDto.
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
    @Mapping(target = "isPrimary", defaultValue = "false")
    ContactEntity fromUpdateDto(ContactUpdateDto dto);
}

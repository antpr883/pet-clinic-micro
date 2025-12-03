package com.micro.auth.data.dto.mapper;

import com.micro.auth.data.dto.auth.UserCreateDto;
import com.micro.auth.data.dto.auth.UserFullDto;
import com.micro.auth.data.entities.RoleEntity;
import com.micro.auth.data.entities.UserEntity;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "passwordSalt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    public abstract UserEntity toEntity(UserCreateDto dto);

    @Mapping(target = "roles", ignore = true)
    protected abstract UserFullDto mapToDtoInternal(UserEntity entity);

    public UserFullDto toDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserFullDto dto = mapToDtoInternal(entity);
        if (dto != null && entity.getRoles() != null) {
            Set<String> roleNames = entity.getRoles().stream()
                    .filter(role -> role != null && role.getName() != null)
                    .map(RoleEntity::getName)
                    .collect(Collectors.toSet());
            dto.setRoles(roleNames);
        } else if (dto != null) {
            dto.setRoles(Set.of());
        }
        return dto;
    }
}


package com.micro.auth.service.permission;

import com.micro.auth.data.dto.auth.PermissionCreateDto;
import com.micro.auth.data.entities.PermissionEntity;
import com.micro.auth.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional
    public PermissionEntity create(PermissionCreateDto createDto) {
        log.debug("Creating new permission: {}", createDto.getName());

        if (permissionRepository.existsByName(createDto.getName())) {
            throw new IllegalArgumentException("Permission already exists: " + createDto.getName());
        }

        PermissionEntity permission = PermissionEntity.builder()
                .name(createDto.getName())
                .active(true)
                .build();

        permission = permissionRepository.save(permission);
        log.info("Permission created: {}", permission.getName());

        return permission;
    }
}


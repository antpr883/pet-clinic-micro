package com.micro.auth.service.role;

import com.micro.auth.data.dto.auth.RoleCreateDto;
import com.micro.auth.data.entities.PermissionEntity;
import com.micro.auth.data.entities.RoleEntity;
import com.micro.auth.repository.PermissionRepository;
import com.micro.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public RoleEntity create(RoleCreateDto createDto) {
        log.debug("Creating new role: {}", createDto.getName());

        if (roleRepository.existsByName(createDto.getName())) {
            throw new IllegalArgumentException("Role already exists: " + createDto.getName());
        }

        RoleEntity role = RoleEntity.builder()
                .name(createDto.getName())
                .active(true)
                .build();

        // Assign permissions
        if (createDto.getPermissionNames() != null && !createDto.getPermissionNames().isEmpty()) {
            Set<PermissionEntity> permissions = createDto.getPermissionNames().stream()
                    .map(permissionName -> permissionRepository.findByNameAndActiveTrue(permissionName)
                            .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionName)))
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }

        role = roleRepository.save(role);
        log.info("Role created: {}", role.getName());

        return role;
    }
}


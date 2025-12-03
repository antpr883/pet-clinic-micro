package com.micro.auth.service.user;

import com.micro.auth.data.dto.auth.UserCreateDto;
import com.micro.auth.data.dto.auth.UserFullDto;
import com.micro.auth.data.entities.RoleEntity;
import com.micro.auth.data.entities.UserEntity;
import com.micro.auth.data.dto.mapper.UserMapper;
import com.micro.auth.repository.RoleRepository;
import com.micro.auth.repository.UserRepository;
import com.micro.auth.service.auth.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordService passwordService;

    @Transactional
    public UserFullDto create(UserCreateDto createDto) {
        log.debug("Creating new user: {}", createDto.getUsername());

        if (userRepository.existsByUsername(createDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + createDto.getUsername());
        }

        if (userRepository.existsByEmail(createDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + createDto.getEmail());
        }

        UserEntity entity = userMapper.toEntity(createDto);
        
        // Hash password
        String salt = passwordService.generateSalt();
        String passwordHash = passwordService.hashPassword(createDto.getPassword(), salt);
        entity.setPasswordHash(passwordHash);
        entity.setPasswordSalt(salt);
        entity.setActive(true);

        // Assign roles
        if (createDto.getRoleNames() != null && !createDto.getRoleNames().isEmpty()) {
            Set<RoleEntity> roles = createDto.getRoleNames().stream()
                    .map(roleName -> roleRepository.findByNameAndActiveTrue(roleName)
                            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            entity.setRoles(roles);
        }

        entity = userRepository.save(entity);
        log.info("User created: {}", entity.getUsername());

        return userMapper.toDto(entity);
    }

    public Page<UserFullDto> findAll(Pageable pageable) {
        log.debug("Finding all users with pagination: {}", pageable);
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    /**
     * Знаходить користувача за ID.
     * 
     * Примітка: Якщо потрібні ролі та пермішени, варто використати Entity Graph
     * для уникнення N+1 проблеми. Для поточного використання (DTO mapping) це не критично.
     * 
     * @param id ID користувача
     * @return UserFullDto з даними користувача
     */
    public UserFullDto findById(Long id) {
        log.debug("Finding user by id: {}", id);
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return userMapper.toDto(entity);
    }
}


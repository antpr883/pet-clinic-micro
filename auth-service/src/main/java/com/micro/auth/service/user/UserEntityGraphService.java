package com.micro.auth.service.user;

import com.micro.auth.data.constants.UserEntityGraphConstants;
import com.micro.auth.data.entities.UserEntity;
import com.micro.auth.repository.UserRepository;
import com.micro.auth.service.graph.GraphBuilderMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервіс для завантаження UserEntity з ролями та пермішенами через Entity Graph.
 * 
 * BEST PRACTICE: Винесено дублюваний код для створення Entity Graph та завантаження користувача.
 * Це уникнення дублювання коду в JpaUserRealm та AuthService.
 * 
 * Використання:
 * - Завантаження користувача з ролями та пермішенами одним запитом
 * - Уникнення N+1 проблеми
 * - Консистентність - однакова логіка в усіх місцях
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserEntityGraphService {

    private final UserRepository userRepository;
    private final GraphBuilderMappingService graphBuilderMappingService;

    /**
     * Знаходить користувача за ID з ролями та пермішенами.
     * 
     * BEST PRACTICE: Використовує Entity Graph для уникнення N+1 проблеми.
     * Завантажує користувача з ролями та пермішенами одним запитом через JOIN.
     * 
     * @param id ID користувача
     * @return Optional<UserEntity> з завантаженими ролями та пермішенами
     */
    public Optional<UserEntity> findByIdWithRolesAndPermissions(Long id) {
        log.debug("Loading user with roles and permissions by id: {}", id);
        
        var entityGraph = graphBuilderMappingService.getGraphWithAttributes(
                UserEntity.class,
                UserEntityGraphConstants.ROLES,
                UserEntityGraphConstants.ROLES_PERMISSIONS
        );
        
        return userRepository.findById(id, entityGraph);
    }

    /**
     * Знаходить користувача за username з ролями та пермішенами.
     * 
     * BEST PRACTICE: Використовує Entity Graph для уникнення N+1 проблеми.
     * Завантажує користувача з ролями та пермішенами одним запитом через JOIN.
     * 
     * @param username username користувача
     * @return Optional<UserEntity> з завантаженими ролями та пермішенами
     */
    public Optional<UserEntity> findByUsernameAndActiveTrueWithRolesAndPermissions(String username) {
        log.debug("Loading user with roles and permissions by username: {}", username);
        
        var entityGraph = graphBuilderMappingService.getGraphWithAttributes(
                UserEntity.class,
                UserEntityGraphConstants.ROLES,
                UserEntityGraphConstants.ROLES_PERMISSIONS
        );
        
        return userRepository.findByUsernameAndActiveTrue(username, entityGraph);
    }
}


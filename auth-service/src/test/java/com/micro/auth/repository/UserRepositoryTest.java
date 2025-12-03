package com.micro.auth.repository;

import com.micro.auth.config.TestcontainersConfig;
import com.micro.auth.config.jpa.CustomRepositoryAutoConfiguration;
import com.micro.auth.config.jpa.JpaAuditingConfig;
import com.micro.auth.data.entities.RoleEntity;
import com.micro.auth.data.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository тести для UserRepository.
 * 
 * Використовує @DataJpaTest для тестування JPA репозиторіїв з PostgreSQL через Testcontainers.
 * Тестує реальні SQL запити та взаємодію з базою даних.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("testcontainers")
@Import({JpaAuditingConfig.class, TestcontainersConfig.class, CustomRepositoryAutoConfiguration.class})
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private UserEntity testUser;
    private UserEntity inactiveUser;
    private RoleEntity adminRole;

    @BeforeEach
    void setUp() {
        // Створюємо роль ADMIN
        adminRole = RoleEntity.builder()
                .name("ADMIN")
                .active(true)
                .build();
        adminRole = entityManager.persistAndFlush(adminRole);

        // Створюємо активного користувача
        testUser = UserEntity.builder()
                .username("testuser")
                .passwordHash("hash123")
                .passwordSalt("salt123")
                .email("testuser@example.com")
                .active(true)
                .build();
        testUser.getRoles().add(adminRole);
        testUser = entityManager.persistAndFlush(testUser);

        // Створюємо неактивного користувача (soft deleted)
        inactiveUser = UserEntity.builder()
                .username("inactive")
                .passwordHash("hash456")
                .passwordSalt("salt456")
                .email("inactive@example.com")
                .active(false)
                .build();
        inactiveUser = entityManager.persistAndFlush(inactiveUser);

        entityManager.clear();
    }

    @Test
    @DisplayName("findByUsernameAndActiveTrue - should return user when exists and active")
    void findByUsernameAndActiveTrue_shouldReturnUser_whenExistsAndActive() {
        // When
        Optional<UserEntity> result = userRepository.findByUsernameAndActiveTrue("testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        assertThat(result.get().getEmail()).isEqualTo("testuser@example.com");
        assertThat(result.get().getActive()).isTrue();
    }

    @Test
    @DisplayName("findByUsernameAndActiveTrue - should return empty when user inactive")
    void findByUsernameAndActiveTrue_shouldReturnEmpty_whenUserInactive() {
        // When
        Optional<UserEntity> result = userRepository.findByUsernameAndActiveTrue("inactive");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUsernameAndActiveTrue - should return empty when user not exists")
    void findByUsernameAndActiveTrue_shouldReturnEmpty_whenUserNotExists() {
        // When
        Optional<UserEntity> result = userRepository.findByUsernameAndActiveTrue("nonexistent");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByEmailAndActiveTrue - should return user when exists and active")
    void findByEmailAndActiveTrue_shouldReturnUser_whenExistsAndActive() {
        // When
        Optional<UserEntity> result = userRepository.findByEmailAndActiveTrue("testuser@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("testuser@example.com");
        assertThat(result.get().getActive()).isTrue();
    }

    @Test
    @DisplayName("findByEmailAndActiveTrue - should return empty when user inactive")
    void findByEmailAndActiveTrue_shouldReturnEmpty_whenUserInactive() {
        // When
        Optional<UserEntity> result = userRepository.findByEmailAndActiveTrue("inactive@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByUsername - should return true when username exists")
    void existsByUsername_shouldReturnTrue_whenUsernameExists() {
        // When
        boolean result = userRepository.existsByUsername("testuser");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsByUsername - should return false when username not exists")
    void existsByUsername_shouldReturnFalse_whenUsernameNotExists() {
        // When
        boolean result = userRepository.existsByUsername("nonexistent");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("existsByEmail - should return true when email exists")
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        // When
        boolean result = userRepository.existsByEmail("testuser@example.com");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsByEmail - should return false when email not exists")
    void existsByEmail_shouldReturnFalse_whenEmailNotExists() {
        // When
        boolean result = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("save - should persist user with roles")
    void save_shouldPersistUserWithRoles() {
        // Given
        RoleEntity vetRole = RoleEntity.builder()
                .name("VET")
                .active(true)
                .build();
        vetRole = entityManager.persistAndFlush(vetRole);

        UserEntity newUser = UserEntity.builder()
                .username("newuser")
                .passwordHash("hash789")
                .passwordSalt("salt789")
                .email("newuser@example.com")
                .active(true)
                .build();
        newUser.getRoles().add(vetRole);

        // When
        UserEntity saved = userRepository.save(newUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<UserEntity> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getRoles()).hasSize(1);
        assertThat(found.get().getRoles().iterator().next().getName()).isEqualTo("VET");
    }
}


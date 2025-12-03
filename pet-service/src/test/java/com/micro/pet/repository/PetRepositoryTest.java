package com.micro.pet.repository;

import com.micro.pet.config.TestcontainersConfig;
import com.micro.pet.config.jpa.CustomRepositoryAutoConfiguration;
import com.micro.pet.config.jpa.JpaAuditingConfig;
import com.micro.pet.data.entities.PetEntity;
import com.micro.pet.data.entities.PetTypeEntity;
import com.micro.pet.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository тести для PetRepository.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("testcontainers")
@Import({CustomRepositoryAutoConfiguration.class, JpaAuditingConfig.class, TestcontainersConfig.class})
@Sql(scripts = "/db/test/seed-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("PetRepository Tests")
class PetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetTypeRepository petTypeRepository;

    private PetTypeEntity testType;
    private PetEntity testPet;
    private PetEntity inactivePet;

    @BeforeEach
    void setUp() {
        // Create pet type with unique name to avoid conflicts with seed data
        testType = TestDataBuilder.petTypeEntity()
                .typeName("TEST_DOG_" + System.currentTimeMillis())
                .build();
        testType = entityManager.persistAndFlush(testType);

        // Create active pet
        testPet = TestDataBuilder.petEntity().build();
        testPet.setType(testType);
        testPet = entityManager.persistAndFlush(testPet);

        // Create inactive pet
        inactivePet = TestDataBuilder.petEntityInactive().build();
        inactivePet.setType(testType);
        inactivePet = entityManager.persistAndFlush(inactivePet);

        entityManager.clear();
    }

    @Test
    @DisplayName("findById - should return PetEntity when exists")
    void findById_shouldReturnPetEntity_whenExists() {
        // When
        Optional<PetEntity> result = petRepository.findById(testPet.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Барсик");
        assertThat(result.get().getActive()).isTrue();
    }

    @Test
    @DisplayName("findById - should return empty when not exists")
    void findById_shouldReturnEmpty_whenNotExists() {
        // When
        Optional<PetEntity> result = petRepository.findById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByOwnerIdAndActiveTrue - should return only active pets including seed data")
    void findByOwnerIdAndActiveTrue_shouldReturnOnlyActivePets() {
        // When
        List<PetEntity> result = petRepository.findByOwnerIdAndActiveTrue(1L);

        // Then
        // Seed data has 2 active pets with ownerId=1 (Barsik, Rex), we add 1 more in setUp = at least 3 total
        assertThat(result.size()).isGreaterThanOrEqualTo(3);
        assertThat(result).contains(testPet);
        assertThat(result).doesNotContain(inactivePet);
        assertThat(result).allMatch(PetEntity::getActive);
        assertThat(result).allMatch(p -> p.getOwnerId().equals(1L));
    }

    @Test
    @DisplayName("countActive - should return count of active pets including seed data")
    void countActive_shouldReturnCountOfActivePets() {
        // When
        long count = petRepository.countActive();

        // Then
        // Seed data has 4 active pets, we add 1 more in setUp = at least 5 total
        assertThat(count).isGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("existsByIdAndActiveTrue - should return true for active pet")
    void existsByIdAndActiveTrue_shouldReturnTrue_forActivePet() {
        // When
        boolean exists = petRepository.existsByIdAndActiveTrue(testPet.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByIdAndActiveTrue - should return false for inactive pet")
    void existsByIdAndActiveTrue_shouldReturnFalse_forInactivePet() {
        // When
        boolean exists = petRepository.existsByIdAndActiveTrue(inactivePet.getId());

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("softDelete - should set active to false")
    void softDelete_shouldSetActiveToFalse() {
        // When
        petRepository.softDelete(testPet.getId(), null);

        // Then
        entityManager.clear();
        Optional<PetEntity> result = petRepository.findById(testPet.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getActive()).isFalse();
    }
}


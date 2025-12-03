package com.micro.pet.repository;

import com.micro.pet.config.TestcontainersConfig;
import com.micro.pet.config.jpa.CustomRepositoryAutoConfiguration;
import com.micro.pet.config.jpa.JpaAuditingConfig;
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
 * Repository тести для PetTypeRepository.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("testcontainers")
@Import({CustomRepositoryAutoConfiguration.class, JpaAuditingConfig.class, TestcontainersConfig.class})
@Sql(scripts = "/db/test/seed-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("PetTypeRepository Tests")
class PetTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PetTypeRepository petTypeRepository;

    private PetTypeEntity testType;
    private PetTypeEntity inactiveType;

    @BeforeEach
    void setUp() {
        // Create active type with unique name to avoid conflicts with seed data
        long timestamp = System.currentTimeMillis();
        PetTypeEntity typeBuilder = TestDataBuilder.petTypeEntity()
                .typeName("TEST_DOG_" + timestamp)
                .build();
        testType = entityManager.persistAndFlush(typeBuilder);

        // Create inactive type with unique name
        PetTypeEntity inactiveBuilder = (PetTypeEntity) TestDataBuilder.petTypeEntity()
                .typeName("TEST_BIRD_" + timestamp)
                .active(false)
                .build();
        inactiveType = entityManager.persistAndFlush(inactiveBuilder);

        entityManager.clear();
    }

    @Test
    @DisplayName("findById - should return PetTypeEntity when exists")
    void findById_shouldReturnPetTypeEntity_whenExists() {
        // When
        Optional<PetTypeEntity> result = petTypeRepository.findById(testType.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTypeName()).startsWith("TEST_DOG_");
        assertThat(result.get().getActive()).isTrue();
    }

    @Test
    @DisplayName("findAll - should return all types including seed data")
    void findAll_shouldReturnAllTypes() {
        // When
        List<PetTypeEntity> result = petTypeRepository.findAll();

        // Then
        // Seed data has 6 types, we add 2 more in setUp = 8 total
        assertThat(result.size()).isGreaterThanOrEqualTo(8);
        assertThat(result).contains(testType, inactiveType);
    }

    @Test
    @DisplayName("findAllActive - should return only active types including seed data")
    void findAllActive_shouldReturnOnlyActiveTypes() {
        // When
        List<PetTypeEntity> result = petTypeRepository.findAllActive();

        // Then
        // Seed data has 6 active types, we add 1 more in setUp = at least 7 total
        assertThat(result.size()).isGreaterThanOrEqualTo(7);
        assertThat(result).contains(testType);
        assertThat(result).doesNotContain(inactiveType);
        assertThat(result).allMatch(PetTypeEntity::getActive);
    }
}


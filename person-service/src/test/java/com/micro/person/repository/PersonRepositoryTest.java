package com.micro.person.repository;

import com.micro.person.data.entities.ContactEntity;
import com.micro.person.data.entities.PersonEntity;
import com.micro.person.data.entities.enums.ContactType;
import com.micro.person.data.entities.enums.PersonRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.micro.person.config.jpa.CustomRepositoryAutoConfiguration;
import com.micro.person.config.jpa.JpaAuditingConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository тести для PersonRepository.
 * 
 * Використовує @DataJpaTest для тестування JPA репозиторіїв з PostgreSQL через Testcontainers.
 * Тестує реальні SQL запити та взаємодію з базою даних.
 * 
 * Переваги Testcontainers:
 * - Реальна PostgreSQL БД з підтримкою схем
 * - Автоматичне управління життєвим циклом контейнера
 * - Можливість використання Liquibase міграцій
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Використовуємо Testcontainers
@ActiveProfiles("testcontainers")
@Import({CustomRepositoryAutoConfiguration.class, JpaAuditingConfig.class, com.micro.person.config.TestcontainersConfig.class})
@DisplayName("PersonRepository Tests")
class PersonRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PersonRepository personRepository;

    private PersonEntity testPerson;
    private PersonEntity inactivePerson;
    private ContactEntity testContact;

    @BeforeEach
    void setUp() {
        // Створюємо активну персону
        testPerson = PersonEntity.builder()
                .firstName("Іван")
                .lastName("Петренко")
                .middleName("Олександрович")
                .notes("Тестова персона")
                .personRole(PersonRole.OWNER)
                .active(true)
                .build();
        testPerson = entityManager.persistAndFlush(testPerson);

        // Створюємо контакт для персони
        testContact = ContactEntity.builder()
                .person(testPerson)
                .contactType(ContactType.EMAIL)
                .value("ivan.petrenko@example.com")
                .label("Особистий email")
                .isPrimary(true)
                .active(true)
                .build();
        testContact = entityManager.persistAndFlush(testContact);

        // Створюємо неактивну персону (soft deleted)
        inactivePerson = PersonEntity.builder()
                .firstName("Петро")
                .lastName("Сидоренко")
                .personRole(PersonRole.OWNER)
                .active(false)
                .build();
        inactivePerson = entityManager.persistAndFlush(inactivePerson);

        entityManager.clear();
    }

    // ========== Basic CRUD Tests ==========

    @Test
    @DisplayName("findById - should return PersonEntity when exists")
    void findById_shouldReturnPersonEntity_whenExists() {
        // When
        Optional<PersonEntity> result = personRepository.findById(testPerson.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Іван");
        assertThat(result.get().getLastName()).isEqualTo("Петренко");
        assertThat(result.get().getActive()).isTrue();
    }

    @Test
    @DisplayName("findById - should return empty when not exists")
    void findById_shouldReturnEmpty_whenNotExists() {
        // When
        Optional<PersonEntity> result = personRepository.findById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save - should save and return PersonEntity")
    void save_shouldSaveAndReturnPersonEntity() {
        // Given
        PersonEntity newPerson = PersonEntity.builder()
                .firstName("Марія")
                .lastName("Коваленко")
                .personRole(PersonRole.OWNER)
                .active(true)
                .build();

        // When
        PersonEntity saved = personRepository.save(newPerson);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(saved.getId()).isNotNull();
        Optional<PersonEntity> found = personRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Марія");
    }

    @Test
    @DisplayName("deleteById - should delete PersonEntity")
    void deleteById_shouldDeletePersonEntity() {
        // Given
        Long id = testPerson.getId();

        // When
        personRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<PersonEntity> result = personRepository.findById(id);
        assertThat(result).isEmpty();
    }

    // ========== Custom Query Methods Tests ==========

    @Test
    @DisplayName("findByFirstNameAndLastNameAndActiveTrue - should return PersonEntity when found")
    void findByFirstNameAndLastNameAndActiveTrue_shouldReturnPersonEntity_whenFound() {
        // When
        Optional<PersonEntity> result = personRepository
                .findByFirstNameAndLastNameAndActiveTrue("Іван", "Петренко");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testPerson.getId());
        assertThat(result.get().getActive()).isTrue();
    }

    @Test
    @DisplayName("findByFirstNameAndLastNameAndActiveTrue - should return empty when inactive")
    void findByFirstNameAndLastNameAndActiveTrue_shouldReturnEmpty_whenInactive() {
        // When
        Optional<PersonEntity> result = personRepository
                .findByFirstNameAndLastNameAndActiveTrue("Петро", "Сидоренко");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByFirstNameContainingIgnoreCaseAndActiveTrue - should return list of PersonEntity")
    void findByFirstNameContainingIgnoreCaseAndActiveTrue_shouldReturnList() {
        // Given - додаємо ще одну персону
        PersonEntity person2 = PersonEntity.builder()
                .firstName("Іванна")
                .lastName("Іваненко")
                .personRole(PersonRole.OWNER)
                .active(true)
                .build();
        entityManager.persistAndFlush(person2);
        entityManager.clear();

        // When
        List<PersonEntity> result = personRepository
                .findByFirstNameContainingIgnoreCaseAndActiveTrue("іван");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(PersonEntity::getFirstName)
                .containsExactlyInAnyOrder("Іван", "Іванна");
    }

    @Test
    @DisplayName("findByLastNameContainingIgnoreCaseAndActiveTrue - should return list of PersonEntity")
    void findByLastNameContainingIgnoreCaseAndActiveTrue_shouldReturnList() {
        // When
        List<PersonEntity> result = personRepository
                .findByLastNameContainingIgnoreCaseAndActiveTrue("петренко");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Петренко");
    }

    @Test
    @DisplayName("searchByName - should return list when matches firstName or lastName")
    void searchByName_shouldReturnList_whenMatchesFirstNameOrLastName() {
        // Given - додаємо персону з іншим ім'ям
        PersonEntity person2 = PersonEntity.builder()
                .firstName("Олександр")
                .lastName("Петренко")
                .personRole(PersonRole.OWNER)
                .active(true)
                .build();
        entityManager.persistAndFlush(person2);
        entityManager.clear();

        // When - шукаємо по частині прізвища
        List<PersonEntity> result = personRepository.searchByName("Петренко");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(PersonEntity::getLastName)
                .containsOnly("Петренко");
    }

    @Test
    @DisplayName("findByContactType - should return list of PersonEntity with contacts")
    void findByContactType_shouldReturnListOfPersonEntity_withContacts() {
        // When
        List<PersonEntity> result = personRepository.findByContactType(ContactType.EMAIL);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testPerson.getId());
    }

    @Test
    @DisplayName("countActive - should return count of active persons")
    void countActive_shouldReturnCountOfActivePersons() {
        // Given - додаємо ще одну активну персону
        PersonEntity person2 = PersonEntity.builder()
                .firstName("Марія")
                .lastName("Коваленко")
                .personRole(PersonRole.OWNER)
                .active(true)
                .build();
        entityManager.persistAndFlush(person2);
        entityManager.clear();

        // When
        long count = personRepository.countActive();

        // Then
        assertThat(count).isEqualTo(2);
    }

    // ========== Soft Delete Tests ==========

    @Test
    @DisplayName("softDelete - should set active=false")
    void softDelete_shouldSetActiveFalse() {
        // Given
        Long id = testPerson.getId();

        // When
        Optional<PersonEntity> result = personRepository.softDelete(id, null);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(result).isPresent();
        Optional<PersonEntity> found = personRepository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getActive()).isFalse();
    }

    @Test
    @DisplayName("findAllActive - should return only active persons")
    void findAllActive_shouldReturnOnlyActivePersons() {
        // When
        List<PersonEntity> result = personRepository.findAllActive();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testPerson.getId());
        assertThat(result.get(0).getActive()).isTrue();
    }

    @Test
    @DisplayName("isActive - should return true for active person")
    void isActive_shouldReturnTrue_forActivePerson() {
        // When
        boolean result = personRepository.isActive(testPerson.getId());

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isActive - should return false for inactive person")
    void isActive_shouldReturnFalse_forInactivePerson() {
        // When
        boolean result = personRepository.isActive(inactivePerson.getId());

        // Then
        assertThat(result).isFalse();
    }
}


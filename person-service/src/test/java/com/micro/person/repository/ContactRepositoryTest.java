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
 * Repository тести для ContactRepository.
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
@DisplayName("ContactRepository Tests")
class ContactRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private PersonRepository personRepository;

    private PersonEntity testPerson;
    private ContactEntity primaryContact;
    private ContactEntity secondaryContact;
    private ContactEntity inactiveContact;

    @BeforeEach
    void setUp() {
        // Створюємо персону
        testPerson = PersonEntity.builder()
                .firstName("Іван")
                .lastName("Петренко")
                .personRole(PersonRole.OWNER)
                .active(true)
                .build();
        testPerson = entityManager.persistAndFlush(testPerson);

        // Створюємо основний контакт (email)
        primaryContact = ContactEntity.builder()
                .person(testPerson)
                .contactType(ContactType.EMAIL)
                .value("ivan.petrenko@example.com")
                .label("Особистий email")
                .isPrimary(true)
                .active(true)
                .build();
        primaryContact = entityManager.persistAndFlush(primaryContact);

        // Створюємо вторинний контакт (phone)
        secondaryContact = ContactEntity.builder()
                .person(testPerson)
                .contactType(ContactType.MOBILE)
                .value("+380501234567")
                .label("Мобільний телефон")
                .isPrimary(false)
                .active(true)
                .build();
        secondaryContact = entityManager.persistAndFlush(secondaryContact);

        // Створюємо неактивний контакт
        inactiveContact = ContactEntity.builder()
                .person(testPerson)
                .contactType(ContactType.EMAIL)
                .value("inactive@example.com")
                .label("Неактивний email")
                .isPrimary(false)
                .active(false)
                .build();
        inactiveContact = entityManager.persistAndFlush(inactiveContact);

        entityManager.clear();
    }

    // ========== Basic CRUD Tests ==========

    @Test
    @DisplayName("findById - should return ContactEntity when exists")
    void findById_shouldReturnContactEntity_whenExists() {
        // When
        Optional<ContactEntity> result = contactRepository.findById(primaryContact.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo("ivan.petrenko@example.com");
        assertThat(result.get().getContactType()).isEqualTo(ContactType.EMAIL);
        assertThat(result.get().getIsPrimary()).isTrue();
        assertThat(result.get().getActive()).isTrue();
    }

    @Test
    @DisplayName("save - should save and return ContactEntity")
    void save_shouldSaveAndReturnContactEntity() {
        // Given
        ContactEntity newContact = ContactEntity.builder()
                .person(testPerson)
                .contactType(ContactType.EMAIL)
                .value("work@example.com")
                .label("Робочий email")
                .isPrimary(false)
                .active(true)
                .build();

        // When
        ContactEntity saved = contactRepository.save(newContact);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(saved.getId()).isNotNull();
        Optional<ContactEntity> found = contactRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getValue()).isEqualTo("work@example.com");
    }

    // ========== Person-Specific Query Methods Tests ==========

    @Test
    @DisplayName("findByPersonIdAndActiveTrue - should return only active contacts")
    void findByPersonIdAndActiveTrue_shouldReturnOnlyActiveContacts() {
        // When
        List<ContactEntity> result = contactRepository.findByPersonIdAndActiveTrue(testPerson.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ContactEntity::getActive)
                .containsOnly(true);
        assertThat(result).extracting(ContactEntity::getId)
                .containsExactlyInAnyOrder(primaryContact.getId(), secondaryContact.getId());
    }

    @Test
    @DisplayName("findByPersonIdAndIsPrimaryTrueAndActiveTrue - should return primary contact")
    void findByPersonIdAndIsPrimaryTrueAndActiveTrue_shouldReturnPrimaryContact() {
        // When
        Optional<ContactEntity> result = contactRepository
                .findByPersonIdAndIsPrimaryTrueAndActiveTrue(testPerson.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(primaryContact.getId());
        assertThat(result.get().getIsPrimary()).isTrue();
    }

    @Test
    @DisplayName("findByPersonIdAndContactTypeAndActiveTrue - should return contacts by type")
    void findByPersonIdAndContactTypeAndActiveTrue_shouldReturnContactsByType() {
        // When
        List<ContactEntity> result = contactRepository
                .findByPersonIdAndContactTypeAndActiveTrue(testPerson.getId(), ContactType.MOBILE);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(secondaryContact.getId());
        assertThat(result.get(0).getContactType()).isEqualTo(ContactType.MOBILE);
    }

    @Test
    @DisplayName("countByPersonId - should return count of active contacts")
    void countByPersonId_shouldReturnCountOfActiveContacts() {
        // When
        long count = contactRepository.countByPersonId(testPerson.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("existsByPersonIdAndIsPrimaryTrueAndActiveTrue - should return true when primary exists")
    void existsByPersonIdAndIsPrimaryTrueAndActiveTrue_shouldReturnTrue_whenPrimaryExists() {
        // When
        boolean result = contactRepository
                .existsByPersonIdAndIsPrimaryTrueAndActiveTrue(testPerson.getId());

        // Then
        assertThat(result).isTrue();
    }

    // ========== Search Methods Tests ==========

    @Test
    @DisplayName("findByEmailIgnoreCaseAndActiveTrue - should return ContactEntity ignoring case")
    void findByEmailIgnoreCaseAndActiveTrue_shouldReturnContactEntity_ignoringCase() {
        // When - шукаємо з великої літери
        Optional<ContactEntity> result = contactRepository
                .findByEmailIgnoreCaseAndActiveTrue("IVAN.PETRENKO@EXAMPLE.COM");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(primaryContact.getId());
    }

    @Test
    @DisplayName("findByEmailIgnoreCaseAndActiveTrue - should return empty when not found")
    void findByEmailIgnoreCaseAndActiveTrue_shouldReturnEmpty_whenNotFound() {
        // When
        Optional<ContactEntity> result = contactRepository
                .findByEmailIgnoreCaseAndActiveTrue("notfound@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByPhoneAndActiveTrue - should return ContactEntity")
    void findByPhoneAndActiveTrue_shouldReturnContactEntity() {
        // When
        Optional<ContactEntity> result = contactRepository
                .findByPhoneAndActiveTrue("+380501234567");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(secondaryContact.getId());
    }

    @Test
    @DisplayName("findByEmailContainingIgnoreCaseAndActiveTrue - should return list of ContactEntity")
    void findByEmailContainingIgnoreCaseAndActiveTrue_shouldReturnList() {
        // Given - додаємо ще один email контакт
        ContactEntity contact2 = ContactEntity.builder()
                .person(testPerson)
                .contactType(ContactType.EMAIL)
                .value("ivan.work@example.com")
                .label("Робочий email")
                .isPrimary(false)
                .active(true)
                .build();
        entityManager.persistAndFlush(contact2);
        entityManager.clear();

        // When
        List<ContactEntity> result = contactRepository
                .findByEmailContainingIgnoreCaseAndActiveTrue("ivan");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ContactEntity::getValue)
                .containsExactlyInAnyOrder("ivan.petrenko@example.com", "ivan.work@example.com");
    }

    @Test
    @DisplayName("findByPhoneContainingAndActiveTrue - should return list of ContactEntity")
    void findByPhoneContainingAndActiveTrue_shouldReturnList() {
        // Given - додаємо ще один phone контакт
        ContactEntity contact2 = ContactEntity.builder()
                .person(testPerson)
                .contactType(ContactType.MOBILE)
                .value("+380671234567")
                .label("Додатковий телефон")
                .isPrimary(false)
                .active(true)
                .build();
        entityManager.persistAndFlush(contact2);
        entityManager.clear();

        // When
        List<ContactEntity> result = contactRepository
                .findByPhoneContainingAndActiveTrue("38050");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getValue()).contains("38050");
    }

    @Test
    @DisplayName("findByIsPrimaryTrueAndActiveTrue - should return all primary contacts")
    void findByIsPrimaryTrueAndActiveTrue_shouldReturnAllPrimaryContacts() {
        // Given - додаємо персону з основним контактом
        PersonEntity person2 = PersonEntity.builder()
                .firstName("Марія")
                .lastName("Коваленко")
                .personRole(PersonRole.OWNER)
                .active(true)
                .build();
        person2 = entityManager.persistAndFlush(person2);

        ContactEntity primary2 = ContactEntity.builder()
                .person(person2)
                .contactType(ContactType.EMAIL)
                .value("maria@example.com")
                .label("Особистий email")
                .isPrimary(true)
                .active(true)
                .build();
        entityManager.persistAndFlush(primary2);
        entityManager.clear();

        // When
        List<ContactEntity> result = contactRepository.findByIsPrimaryTrueAndActiveTrue();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ContactEntity::getIsPrimary)
                .containsOnly(true);
    }

    // ========== Soft Delete Tests ==========

    @Test
    @DisplayName("softDelete - should set active=false")
    void softDelete_shouldSetActiveFalse() {
        // Given
        Long id = primaryContact.getId();

        // When
        Optional<ContactEntity> result = contactRepository.softDelete(id, null);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(result).isPresent();
        Optional<ContactEntity> found = contactRepository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getActive()).isFalse();
    }

    @Test
    @DisplayName("findAllActive - should return only active contacts")
    void findAllActive_shouldReturnOnlyActiveContacts() {
        // When
        List<ContactEntity> result = contactRepository.findAllActive();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ContactEntity::getActive)
                .containsOnly(true);
    }

    @Test
    @DisplayName("isActive - should return true for active contact")
    void isActive_shouldReturnTrue_forActiveContact() {
        // When
        boolean result = contactRepository.isActive(primaryContact.getId());

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isActive - should return false for inactive contact")
    void isActive_shouldReturnFalse_forInactiveContact() {
        // When
        boolean result = contactRepository.isActive(inactiveContact.getId());

        // Then
        assertThat(result).isFalse();
    }

    // ========== Relationship Tests ==========

    @Test
    @DisplayName("Cascade delete - should delete contacts when person is deleted")
    void cascadeDelete_shouldDeleteContacts_whenPersonIsDeleted() {
        // Given
        Long personId = testPerson.getId();
        Long contactId = primaryContact.getId();

        // When
        personRepository.deleteById(personId);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<ContactEntity> contact = contactRepository.findById(contactId);
        assertThat(contact).isEmpty();
    }
}


package com.micro.person.util;

import com.micro.person.data.dto.contact.ContactDto;
import com.micro.person.data.dto.person.PersonDto;
import com.micro.person.data.entities.ContactEntity;
import com.micro.person.data.entities.PersonEntity;
import com.micro.person.data.entities.enums.ContactType;
import com.micro.person.data.entities.enums.PersonRole;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Готові тестові дані (fixtures) для використання в тестах.
 * 
 * Містить готові об'єкти для різних сценаріїв тестування.
 * 
 * Приклад використання:
 * <pre>
 * PersonEntity person = TestFixtures.IVAN_PETRENKO_ENTITY;
 * PersonDto dto = TestFixtures.IVAN_PETRENKO_DTO;
 * </pre>
 */
public class TestFixtures {

    // ========== PersonEntity Fixtures ==========

    /**
     * Готова PersonEntity: Іван Петренко (активна).
     */
    public static final PersonEntity IVAN_PETRENKO_ENTITY = PersonEntity.builder()
            .id(1L)
            .firstName("Іван")
            .lastName("Петренко")
            .middleName("Олександрович")
            .dateOfBirth(LocalDate.of(1990, 5, 15))
            .notes("Власник собаки")
            .personRole(PersonRole.OWNER)
            .active(true)
            .build();

    /**
     * Готова PersonEntity: Марія Коваленко (активна).
     */
    public static final PersonEntity MARIA_KOVALENKO_ENTITY = PersonEntity.builder()
            .id(2L)
            .firstName("Марія")
            .lastName("Коваленко")
            .middleName("Володимирівна")
            .dateOfBirth(LocalDate.of(1985, 8, 20))
            .notes("Власник кота")
            .personRole(PersonRole.OWNER)
            .active(true)
            .build();

    /**
     * Готова PersonEntity: Петро Сидоренко (неактивна, soft deleted).
     */
    public static final PersonEntity PETRO_SYDORENKO_ENTITY = PersonEntity.builder()
            .id(3L)
            .firstName("Петро")
            .lastName("Сидоренко")
            .dateOfBirth(LocalDate.of(1975, 3, 10))
            .personRole(PersonRole.OWNER)
            .active(false)
            .build();

    /**
     * Готова PersonEntity: Олена Мельник (активна, без middleName).
     */
    public static final PersonEntity OLENA_MELNYK_ENTITY = PersonEntity.builder()
            .id(4L)
            .firstName("Олена")
            .lastName("Мельник")
            .dateOfBirth(LocalDate.of(1992, 11, 25))
            .personRole(PersonRole.OWNER)
            .active(true)
            .build();

    // ========== PersonDto Fixtures ==========

    /**
     * Готова PersonDto: Іван Петренко.
     */
    public static final PersonDto IVAN_PETRENKO_DTO = PersonDto.builder()
            .id(1L)
            .firstName("Іван")
            .lastName("Петренко")
            .personRole(PersonRole.OWNER)
            .build();

    /**
     * Готова PersonDto: Марія Коваленко.
     */
    public static final PersonDto MARIA_KOVALENKO_DTO = PersonDto.builder()
            .id(2L)
            .firstName("Марія")
            .lastName("Коваленко")
            .personRole(PersonRole.OWNER)
            .build();

    /**
     * Готова PersonDto: Петро Сидоренко (неактивна).
     */
    public static final PersonDto PETRO_SYDORENKO_DTO = PersonDto.builder()
            .id(3L)
            .firstName("Петро")
            .lastName("Сидоренко")
            .personRole(PersonRole.OWNER)
            .build();

    // ========== ContactEntity Fixtures ==========

    /**
     * Готовий ContactEntity: Email для Івана Петренка.
     */
    public static final ContactEntity IVAN_EMAIL_CONTACT = ContactEntity.builder()
            .id(101L)
            .contactType(ContactType.EMAIL)
            .value("ivan.petrenko@example.com")
            .label("Особистий email")
            .isPrimary(true)
            .active(true)
            .person(IVAN_PETRENKO_ENTITY)
            .build();

    /**
     * Готовий ContactEntity: Phone для Івана Петренка.
     */
    public static final ContactEntity IVAN_PHONE_CONTACT = ContactEntity.builder()
            .id(102L)
            .contactType(ContactType.MOBILE)
            .value("+380501234567")
            .label("Мобільний телефон")
            .isPrimary(false)
            .active(true)
            .person(IVAN_PETRENKO_ENTITY)
            .build();

    /**
     * Готовий ContactEntity: Email для Марії Коваленко.
     */
    public static final ContactEntity MARIA_EMAIL_CONTACT = ContactEntity.builder()
            .id(201L)
            .contactType(ContactType.EMAIL)
            .value("maria.kovalenko@example.com")
            .label("Особистий email")
            .isPrimary(true)
            .active(true)
            .person(MARIA_KOVALENKO_ENTITY)
            .build();

    // ========== ContactDto Fixtures ==========

    /**
     * Готова ContactDto: Email.
     */
    public static final ContactDto EMAIL_CONTACT_DTO = ContactDto.builder()
            .id(101L)
            .contactType(ContactType.EMAIL)
            .value("test@example.com")
            .label("Особистий email")
            .isPrimary(true)
            .build();

    /**
     * Готова ContactDto: Phone.
     */
    public static final ContactDto PHONE_CONTACT_DTO = ContactDto.builder()
            .id(102L)
            .contactType(ContactType.MOBILE)
            .value("+380501234567")
            .label("Мобільний телефон")
            .isPrimary(false)
            .build();

    // ========== Lists of Fixtures ==========

    /**
     * Список всіх активних PersonEntity.
     */
    public static final List<PersonEntity> ACTIVE_PERSONS_ENTITIES = Arrays.asList(
            IVAN_PETRENKO_ENTITY,
            MARIA_KOVALENKO_ENTITY,
            OLENA_MELNYK_ENTITY
    );

    /**
     * Список всіх PersonEntity (включно з неактивними).
     */
    public static final List<PersonEntity> ALL_PERSONS_ENTITIES = Arrays.asList(
            IVAN_PETRENKO_ENTITY,
            MARIA_KOVALENKO_ENTITY,
            PETRO_SYDORENKO_ENTITY,
            OLENA_MELNYK_ENTITY
    );

    /**
     * Список всіх активних PersonDto.
     */
    public static final List<PersonDto> ACTIVE_PERSONS_DTOS = Arrays.asList(
            IVAN_PETRENKO_DTO,
            MARIA_KOVALENKO_DTO
    );

    /**
     * Список ContactEntity для Івана Петренка.
     */
    public static final List<ContactEntity> IVAN_CONTACTS = Arrays.asList(
            IVAN_EMAIL_CONTACT,
            IVAN_PHONE_CONTACT
    );

    // ========== Helper Methods ==========

    /**
     * Створює PersonEntity з контактами для тестування.
     */
    public static PersonEntity createPersonWithContacts(Long personId, String firstName, String lastName) {
        PersonEntity person = PersonEntity.builder()
                .id(personId)
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .personRole(PersonRole.OWNER)
                .active(true)
                .build();

        ContactEntity email = ContactEntity.builder()
                .contactType(ContactType.EMAIL)
                .value(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com")
                .label("Особистий email")
                .isPrimary(true)
                .active(true)
                .person(person)
                .build();

        ContactEntity phone = ContactEntity.builder()
                .contactType(ContactType.MOBILE)
                .value("+380501234567")
                .label("Мобільний телефон")
                .isPrimary(false)
                .active(true)
                .person(person)
                .build();

        person.addContact(email);
        person.addContact(phone);

        return person;
    }

    /**
     * Створює PersonDto з контактами для тестування.
     */
    public static PersonDto createPersonDtoWithContacts(Long personId, String firstName, String lastName) {
        return PersonDto.builder()
                .id(personId)
                .firstName(firstName)
                .lastName(lastName)
                .personRole(PersonRole.OWNER)
                .build();
    }
}

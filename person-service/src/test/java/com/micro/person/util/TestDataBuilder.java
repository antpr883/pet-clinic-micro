package com.micro.person.util;

import com.micro.person.data.dto.contact.ContactDto;
import com.micro.person.data.dto.person.PersonDto;
import com.micro.person.data.dto.person.PersonPreviewDto;
import com.micro.person.data.entities.ContactEntity;
import com.micro.person.data.entities.PersonEntity;
import com.micro.person.data.entities.enums.ContactType;
import com.micro.person.data.entities.enums.PersonRole;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder для створення тестових Entity та DTO.
 * 
 * Використовується в тестах для швидкого створення тестових даних.
 * 
 * Приклад використання:
 * <pre>
 * PersonEntity person = TestDataBuilder.personEntity()
 *     .id(1L)
 *     .firstName("Іван")
 *     .lastName("Петренко")
 *     .build();
 * </pre>
 */
public class TestDataBuilder {

    // ========== PersonEntity Builders ==========

    /**
     * Створює builder для PersonEntity з дефолтними значеннями.
     */
    public static PersonEntity.PersonEntityBuilder personEntity() {
        return PersonEntity.builder()
                .firstName("Іван")
                .lastName("Петренко")
                .middleName("Олександрович")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .notes("Тестова персона")
                .personRole(PersonRole.OWNER)
                .active(true);
    }

    /**
     * Створює builder для PersonEntity з мінімальними даними.
     */
    public static PersonEntity.PersonEntityBuilder personEntityMinimal() {
        return PersonEntity.builder()
                .firstName("Петро")
                .lastName("Іваненко")
                .personRole(PersonRole.OWNER)
                .active(true);
    }

    /**
     * Створює builder для неактивної PersonEntity (soft deleted).
     */
    public static PersonEntity.PersonEntityBuilder personEntityInactive() {
        return PersonEntity.builder()
                .firstName("Іван")
                .lastName("Петренко")
                .middleName("Олександрович")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .notes("Тестова персона")
                .personRole(PersonRole.OWNER)
                .active(false);
    }

    // ========== PersonDto Builders ==========

    /**
     * Створює builder для PersonDto з дефолтними значеннями.
     */
    public static PersonDto.PersonDtoBuilder personDto() {
        return PersonDto.builder()
                .firstName("Іван")
                .lastName("Петренко")
                .personRole(PersonRole.OWNER);
    }

    /**
     * Створює builder для PersonDto з мінімальними даними.
     */
    public static PersonDto.PersonDtoBuilder personDtoMinimal() {
        return PersonDto.builder()
                .firstName("Петро")
                .lastName("Іваненко")
                .personRole(PersonRole.OWNER);
    }

    /**
     * Створює builder для PersonDto з контактами.
     */
    public static PersonDto.PersonDtoBuilder personDtoWithContacts() {
        Set<ContactDto> contacts = new HashSet<>();
        contacts.add(contactDtoEmail().build());
        contacts.add(contactDtoPhone().build());
        
        return personDto()
                .contacts(contacts);
    }

    // ========== PersonPreviewDto Builders ==========

    /**
     * Створює builder для PersonPreviewDto.
     */
    public static PersonPreviewDto.PersonPreviewDtoBuilder personPreviewDto() {
        return PersonPreviewDto.builder()
                .firstName("Іван")
                .lastName("Петренко");
    }

    // ========== ContactEntity Builders ==========

    /**
     * Створює builder для ContactEntity (EMAIL).
     */
    public static ContactEntity.ContactEntityBuilder contactEntityEmail() {
        return ContactEntity.builder()
                .contactType(ContactType.EMAIL)
                .value("ivan.petrenko@example.com")
                .label("Особистий email")
                .isPrimary(true)
                .active(true);
    }

    /**
     * Створює builder для ContactEntity (MOBILE).
     */
    public static ContactEntity.ContactEntityBuilder contactEntityPhone() {
        return ContactEntity.builder()
                .contactType(ContactType.MOBILE)
                .value("+380501234567")
                .label("Мобільний телефон")
                .isPrimary(false)
                .active(true);
    }

    /**
     * Створює builder для неактивного ContactEntity.
     */
    public static ContactEntity.ContactEntityBuilder contactEntityInactive() {
        return ContactEntity.builder()
                .contactType(ContactType.EMAIL)
                .value("ivan.petrenko@example.com")
                .label("Особистий email")
                .isPrimary(true)
                .active(false);
    }

    // ========== ContactDto Builders ==========

    /**
     * Створює builder для ContactDto (EMAIL).
     */
    public static ContactDto.ContactDtoBuilder contactDtoEmail() {
        return ContactDto.builder()
                .contactType(ContactType.EMAIL)
                .value("ivan.petrenko@example.com")
                .label("Особистий email")
                .isPrimary(true);
    }

    /**
     * Створює builder для ContactDto (MOBILE).
     */
    public static ContactDto.ContactDtoBuilder contactDtoPhone() {
        return ContactDto.builder()
                .contactType(ContactType.MOBILE)
                .value("+380501234567")
                .label("Мобільний телефон")
                .isPrimary(false);
    }

    // ========== Helper Methods ==========

    /**
     * Створює PersonEntity з повним набором даних (для інтеграційних тестів).
     */
    public static PersonEntity createFullPersonEntity(Long id) {
        PersonEntity person = PersonEntity.builder()
                .id(id)
                .firstName("Іван")
                .lastName("Петренко")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .personRole(PersonRole.OWNER)
                .active(true)
                .build();

        ContactEntity emailContact = ContactEntity.builder()
                .contactType(ContactType.EMAIL)
                .value("ivan.petrenko@example.com")
                .label("Особистий email")
                .isPrimary(true)
                .active(true)
                .person(person)
                .build();
        
        ContactEntity phoneContact = ContactEntity.builder()
                .contactType(ContactType.MOBILE)
                .value("+380501234567")
                .label("Мобільний телефон")
                .isPrimary(false)
                .active(true)
                .person(person)
                .build();

        person.addContact(emailContact);
        person.addContact(phoneContact);

        return person;
    }

    /**
     * Створює PersonDto з повним набором даних.
     */
    public static PersonDto createFullPersonDto(Long id) {
        return PersonDto.builder()
                .id(id)
                .firstName("Іван")
                .lastName("Петренко")
                .personRole(PersonRole.OWNER)
                .build();
    }
}


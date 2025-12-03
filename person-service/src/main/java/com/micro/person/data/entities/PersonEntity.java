package com.micro.person.data.entities;

import com.micro.person.data.entities.enums.PersonRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Person entity - головна таблиця для персон у системі.
 * Містить базову інформацію про людину (ім'я, прізвище).
 * Контактна інформація зберігається в окремій таблиці ContactEntity (One-to-Many).
 * 
 * Приклад:
 * - Person: Іван Петренко
 * - Contacts:
 *   * PERSONAL Phone: +380501234567
 *   * WORK Email: ivan.petrenko@company.com
 *   * HOME Address: вул. Хрещатик, 1, Київ
 */
@Entity
@Table(name = "persons", indexes = {
    @Index(name = "idx_person_first_name", columnList = "first_name"),
    @Index(name = "idx_person_last_name", columnList = "last_name"),
    @Index(name = "idx_person_active", columnList = "active"),
    @Index(name = "idx_person_role", columnList = "person_role")
})
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"contacts", "addresses"})
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class PersonEntity extends BaseActiveEntity {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Size(max = 50, message = "Middle name must not exceed 50 characters")
    @Column(name = "middle_name", length = 50)
    private String middleName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    @Column(name = "notes", length = 1000)
    private String notes;

    @NotNull(message = "Person role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "person_role", nullable = false, length = 20)
    @Builder.Default
    private PersonRole personRole = PersonRole.OWNER;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ContactEntity> contacts = new HashSet<>();

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<AddressEntity> addresses = new HashSet<>();

    public void addContact(ContactEntity contact) {
        if (contacts == null) {
            contacts = new HashSet<>();
        }
        contacts.add(contact);
        contact.setPerson(this);
    }

    public void removeContact(ContactEntity contact) {
        if (contacts != null) {
            contacts.remove(contact);
            contact.setPerson(null);
        }
    }

    public Set<ContactEntity> getContacts() {
        if (contacts == null) {
            contacts = new HashSet<>();
        }
        return contacts;
    }

    public void addAddress(AddressEntity address) {
        if (addresses == null) {
            addresses = new HashSet<>();
        }
        addresses.add(address);
        address.setPerson(this);
    }

    public void removeAddress(AddressEntity address) {
        if (addresses != null) {
            addresses.remove(address);
            address.setPerson(null);
        }
    }

    public Set<AddressEntity> getAddresses() {
        if (addresses == null) {
            addresses = new HashSet<>();
        }
        return addresses;
    }
}


package com.micro.person.data.dto.person;

import com.micro.person.data.dto.address.AddressCreateDto;
import com.micro.person.data.dto.contact.ContactCreateDto;
import com.micro.person.data.entities.enums.PersonRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO для створення нової персони.
 * Використовується для POST /api/v1/persons
 * Не містить id, timestamps, audit fields.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PersonCreateDto {
    
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @Size(max = 50, message = "Middle name must not exceed 50 characters")
    private String middleName;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    @NotNull(message = "Person role is required")
    private PersonRole personRole;
    
    /**
     * Контакти для створення разом з персоною (опціонально).
     */
    private Set<ContactCreateDto> contacts;
    
    /**
     * Адреси для створення разом з персоною (опціонально).
     */
    private Set<AddressCreateDto> addresses;
}


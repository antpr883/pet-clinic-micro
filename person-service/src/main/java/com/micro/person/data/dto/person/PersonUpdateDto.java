package com.micro.person.data.dto.person;

import com.micro.person.data.dto.address.AddressUpdateDto;
import com.micro.person.data.dto.contact.ContactUpdateDto;
import com.micro.person.data.entities.enums.PersonRole;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO для оновлення персони.
 * Використовується для PUT /api/v1/persons/{id}
 * Всі поля опціональні (для часткового оновлення).
 * 
 * Контакти та адреси можна оновлювати разом з персоною.
 * Якщо передано contacts/addresses - вони будуть повністю замінені (replace strategy).
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PersonUpdateDto {
    
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @Size(max = 50, message = "Middle name must not exceed 50 characters")
    private String middleName;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    private PersonRole personRole;
    
    /**
     * Контакти для оновлення (опціонально).
     * Якщо передано - всі існуючі контакти будуть замінені на нові.
     * Якщо null - контакти не змінюються.
     */
    private Set<ContactUpdateDto> contacts;
    
    /**
     * Адреси для оновлення (опціонально).
     * Якщо передано - всі існуючі адреси будуть замінені на нові.
     * Якщо null - адреси не змінюються.
     */
    private Set<AddressUpdateDto> addresses;
}


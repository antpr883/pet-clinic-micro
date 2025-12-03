package com.micro.person.data.dto.person;

import com.micro.person.data.dto.address.AddressDto;
import com.micro.person.data.dto.base.BaseDto;
import com.micro.person.data.dto.contact.ContactDto;
import com.micro.person.data.entities.enums.PersonRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

/**
 * Повна інформація про персону з усіма контактами та адресами.
 * Використовується для GET /api/v1/persons/{id}
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class PersonFullDto extends BaseDto {
    
    private String firstName;
    
    private String lastName;
    
    private String middleName;
    
    private LocalDate dateOfBirth;
    
    private String notes;
    
    private PersonRole personRole;
    
    /**
     * Всі контакти персони (повна інформація).
     */
    private Set<ContactDto> contacts;
    
    /**
     * Всі адреси персони (повна інформація).
     */
    private Set<AddressDto> addresses;
}


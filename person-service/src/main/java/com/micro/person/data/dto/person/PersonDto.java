package com.micro.person.data.dto.person;

import com.micro.person.data.dto.address.AddressPreviewDto;
import com.micro.person.data.dto.base.BaseDto;
import com.micro.person.data.dto.contact.ContactPreviewDto;
import com.micro.person.data.entities.enums.PersonRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class PersonDto extends BaseDto {
    
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @NotNull(message = "Person role is required")
    private PersonRole personRole;
    
    /**
     * Контакти персони (використовуємо ContactPreviewDto без системних полів).
     */
    private Set<ContactPreviewDto> contacts;
    
    /**
     * Адреси персони (використовуємо AddressPreviewDto без системних полів).
     */
    private Set<AddressPreviewDto> addresses;
}


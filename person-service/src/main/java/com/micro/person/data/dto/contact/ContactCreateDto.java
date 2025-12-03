package com.micro.person.data.dto.contact;

import com.micro.person.data.entities.enums.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO для створення нового контакту.
 * Використовується для POST /api/v1/persons/{personId}/contacts
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ContactCreateDto {
    
    @NotNull(message = "Contact type is required")
    private ContactType contactType;
    
    @NotBlank(message = "Contact value is required")
    private String value;
    
    @Size(max = 100, message = "Label must not exceed 100 characters")
    private String label;
    
    @Builder.Default
    private Boolean isPrimary = false;
}


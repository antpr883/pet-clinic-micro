package com.micro.person.data.dto.contact;

import com.micro.person.data.entities.enums.ContactType;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO для оновлення контакту.
 * Використовується для PUT /api/v1/contacts/{id}
 * Всі поля опціональні (для часткового оновлення).
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ContactUpdateDto {
    
    private ContactType contactType;
    
    private String value;
    
    @Size(max = 100, message = "Label must not exceed 100 characters")
    private String label;
    
    private Boolean isPrimary;
}


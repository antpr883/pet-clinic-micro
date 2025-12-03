package com.micro.person.data.dto.person;

import com.micro.person.data.dto.base.BaseDto;
import com.micro.person.data.entities.enums.PersonRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Базова інформація про персону без контактів та адрес.
 * Використовується для GET /api/v1/persons/{id}/basic
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class PersonBasicDto extends BaseDto {
    
    private String firstName;
    
    private String lastName;
    
    private String middleName;
    
    private String notes;
    
    private PersonRole personRole;
}


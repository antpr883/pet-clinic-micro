package com.micro.person.data.dto.person;

import com.micro.person.data.dto.base.BasePreviewDto;
import com.micro.person.data.entities.enums.PersonRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Скорочений формат персони з основним контактом та адресою.
 * Використовується для GET /api/v1/persons/search/extended
 * 
 * Містить: id, firstName, lastName, role, primaryPhone, city
 * Призначений для UI списків, де потрібна основна інформація про контакт та адресу.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class PersonPreviewExtendedDto extends BasePreviewDto {
    
    private String firstName;
    
    private String lastName;
    
    private PersonRole role;
    
    /**
     * Основний телефон персони (з primary contact).
     */
    private String primaryPhone;
    
    /**
     * Місто з основної адреси персони.
     */
    private String city;
}


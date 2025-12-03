package com.micro.person.data.dto.person;

import com.micro.person.data.dto.base.BasePreviewDto;
import com.micro.person.data.entities.enums.PersonRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Агрегований вигляд персони з основними контактами та адресою.
 * Використовується для Full Preview API.
 * 
 * Комбінований набір — Person + Primary Contacts + Primary Address.
 * Використовується у клінічних звітах, історії пацієнтів, інтеграціях із clinic-service.
 * 
 * Приклад:
 * {
 *   "id": 1,
 *   "fullName": "Іван Петренко",
 *   "role": "OWNER",
 *   "primaryPhone": "+380501234567",
 *   "primaryEmail": "ivan@ex.com",
 *   "primaryAddress": "Київ, Хрещатик 1"
 * }
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class PersonFullPreviewDto extends BasePreviewDto {
    
    /**
     * Повне ім'я персони (firstName + lastName).
     */
    private String fullName;
    
    /**
     * Дата народження персони.
     */
    private LocalDate dateOfBirth;
    
    /**
     * Роль персони.
     */
    private PersonRole role;
    
    /**
     * Основний телефон персони (з primary contact типу MOBILE/PHONE).
     */
    private String primaryPhone;
    
    /**
     * Основний email персони (з primary contact типу EMAIL).
     */
    private String primaryEmail;
    
    /**
     * Основна адреса персони (форматований рядок, наприклад "Київ, Хрещатик 1").
     * Береться з primary адреси або HOME типу.
     */
    private String primaryAddress;
}


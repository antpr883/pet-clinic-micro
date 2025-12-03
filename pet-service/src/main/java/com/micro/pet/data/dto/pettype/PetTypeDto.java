package com.micro.pet.data.dto.pettype;

import com.micro.pet.data.dto.base.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Full pet type information.
 * Used for GET /api/v1/pet-types/{id}
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class PetTypeDto extends BaseDto {
    
    private String typeName; // DOG, CAT, PARROT, IGUANA
    
    private String description;
    
    private Boolean exotic;
}


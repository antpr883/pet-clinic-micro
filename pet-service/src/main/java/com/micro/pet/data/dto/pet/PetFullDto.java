package com.micro.pet.data.dto.pet;

import com.fasterxml.jackson.databind.JsonNode;
import com.micro.pet.data.dto.base.BaseDto;
import com.micro.pet.data.dto.pettype.PetTypeDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Full pet information with all relationships.
 * Used for GET /api/v1/pets/{id}
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class PetFullDto extends BaseDto {
    
    private String name;
    
    private LocalDate birthDate;
    
    private Long ownerId;
    
    private PetTypeDto type;
    
    private JsonNode details; // JSON details from PetInfoEntity
    
    private String photoUrl;
}


package com.micro.pet.data.dto.pettype;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for updating pet type (all fields optional).
 * Used for PUT /api/v1/pet-types/{id}
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PetTypeUpdateDto {
    
    @Size(max = 50, message = "Type name must not exceed 50 characters")
    private String typeName;
    
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    
    private Boolean exotic;
}


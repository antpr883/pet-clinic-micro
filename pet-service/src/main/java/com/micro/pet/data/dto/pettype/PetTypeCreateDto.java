package com.micro.pet.data.dto.pettype;

import com.micro.pet.data.dto.base.DtoMarker;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for creating a new pet type.
 * Used for POST /api/v1/pet-types
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PetTypeCreateDto extends DtoMarker {
    
    @NotBlank(message = "Type name is required")
    @Size(max = 50, message = "Type name must not exceed 50 characters")
    private String typeName; // DOG, CAT, PARROT, IGUANA
    
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    
    private Boolean exotic;
}


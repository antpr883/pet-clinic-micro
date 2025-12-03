package com.micro.pet.data.dto.pet;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * DTO for updating pet (all fields optional).
 * Used for PUT /api/v1/pets/{id}
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PetUpdateDto {
    
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;
    
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    
    private Long ownerId;
    
    private Long typeId;
    
    @Size(max = 255, message = "Photo URL must not exceed 255 characters")
    private String photoUrl;
    
    /**
     * Detailed information in JSON format (optional).
     * Will be updated in PetInfoEntity.details as JSONB.
     */
    private JsonNode details;
}


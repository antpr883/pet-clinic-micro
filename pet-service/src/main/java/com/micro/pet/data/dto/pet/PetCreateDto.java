package com.micro.pet.data.dto.pet;

import com.fasterxml.jackson.databind.JsonNode;
import com.micro.pet.data.dto.base.DtoMarker;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * DTO for creating a new pet.
 * Used for POST /api/v1/pets
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PetCreateDto extends DtoMarker {
    
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;
    
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    
    @NotNull(message = "Type ID is required")
    private Long typeId;
    
    @Size(max = 255, message = "Photo URL must not exceed 255 characters")
    private String photoUrl;
    
    /**
     * Detailed information in JSON format (optional).
     * Will be saved in PetInfoEntity.details as JSONB.
     */
    private JsonNode details;
}


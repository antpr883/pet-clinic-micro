package com.micro.pet.data.dto.petinfo;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for updating detailed pet information (JSONB).
 * Used for PUT /api/v1/pets/{petId}/info
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PetInfoUpdateDto {
    
    @NotNull(message = "Details are required")
    private JsonNode details;
}


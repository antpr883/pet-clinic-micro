package com.micro.pet.data.dto.petinfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.micro.pet.data.dto.base.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Full pet detail information (JSONB).
 * Used for GET /api/v1/pets/{petId}/info
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class PetInfoDto extends BaseDto {
    
    private Long petId;
    
    /**
     * JSON details (JSONB in DB).
     * Example:
     * {
     *   "weight": 6.4,
     *   "breed": "Scottish Fold",
     *   "favoriteFood": "chicken",
     *   "notes": "Requires walks 2 times a day"
     * }
     */
    private JsonNode details;
}


package com.micro.pet.data.dto.pet;

import com.micro.pet.data.dto.base.BasePreviewDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Short pet view for lists and tables.
 * Used for GET /api/v1/pets/{id}/preview
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class PetPreviewDto extends BasePreviewDto {
    
    private String name;
    
    private String typeName; // Pet type name (DOG, CAT, etc.)
    
    private String photoUrl;
}


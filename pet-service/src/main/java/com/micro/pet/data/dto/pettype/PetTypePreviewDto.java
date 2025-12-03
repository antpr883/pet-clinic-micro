package com.micro.pet.data.dto.pettype;

import com.micro.pet.data.dto.base.BasePreviewDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Preview information about pet type.
 * Used for lists and quick access.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class PetTypePreviewDto extends BasePreviewDto {
    
    private String typeName; // DOG, CAT, PARROT, IGUANA
}


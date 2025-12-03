package com.micro.pet.service.pettype;

import com.micro.pet.data.dto.pettype.*;
import com.micro.pet.service.base.BaseService;
import com.micro.pet.service.base.PreviewBaseService;

/**
 * Service for managing pet types.
 */

public interface PetTypeService extends 
        BaseService<PetTypeDto, PetTypeCreateDto>,
        PreviewBaseService<PetTypePreviewDto> {
    
    /**
     * Create a new pet type.
     */
    PetTypeDto create(PetTypeCreateDto createDto);
    
    /**
     * Update pet type.
     */
    PetTypeDto update(Long id, PetTypeUpdateDto updateDto);
}


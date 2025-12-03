package com.micro.pet.service.petinfo;

import com.micro.pet.data.dto.petinfo.PetInfoDto;
import com.micro.pet.data.dto.petinfo.PetInfoUpdateDto;

/**
 * Service for managing detailed pet information (JSONB).
 */
public interface PetInfoService {
    
    /**
     * Get detailed information by pet ID.
     */
    PetInfoDto findByPetId(Long petId);
    
    /**
     * Update detailed information.
     */
    PetInfoDto update(Long petId, PetInfoUpdateDto updateDto);
}


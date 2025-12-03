package com.micro.pet.service.petinfo;

import com.micro.pet.data.dto.mapper.PetInfoMapper;
import com.micro.pet.data.dto.petinfo.PetInfoDto;
import com.micro.pet.data.dto.petinfo.PetInfoUpdateDto;
import com.micro.pet.data.entities.PetEntity;
import com.micro.pet.data.entities.PetInfoEntity;
import com.micro.pet.exception.ResourceNotFoundException;
import com.micro.pet.repository.PetInfoRepository;
import com.micro.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing detailed pet information.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetInfoServiceImpl implements PetInfoService {

    private final PetInfoRepository petInfoRepository;
    private final PetRepository petRepository;
    private final PetInfoMapper petInfoMapper;

    @Override
    public PetInfoDto findByPetId(Long petId) {
        log.debug("Finding pet info by petId: {}", petId);
        
        // FK constraint guarantees: if PetInfo exists, Pet exists
        // No need to check Pet existence separately
        PetInfoEntity info = petInfoRepository.findByPetId(petId)
                .orElseThrow(() -> new ResourceNotFoundException("PetInfoEntity", "petId", petId));
        
        return petInfoMapper.toDto(info);
    }

    @Override
    @Transactional
    public PetInfoDto update(Long petId, PetInfoUpdateDto updateDto) {
        log.debug("Updating pet info for petId: {}", petId);
        
        PetInfoEntity info = petInfoRepository.findByPetId(petId)
                .orElseGet(() -> {
                    // Only load Pet when creating new PetInfo
                    // FK constraint guarantees: if PetInfo exists, Pet exists
                    PetEntity pet = petRepository.findById(petId)
                            .orElseThrow(() -> new ResourceNotFoundException("PetEntity", petId));
                    
                    PetInfoEntity newInfo = PetInfoEntity.builder()
                            .pet(pet)
                            .details(updateDto.getDetails())
                            .active(true)
                            .build();
                    return petInfoRepository.save(newInfo);
                });
        
        petInfoMapper.updateFromUpdateDto(updateDto, info);
        info = petInfoRepository.save(info);
        
        PetInfoDto dto = petInfoMapper.toDto(info);
        log.debug("Updated pet info for petId: {}", petId);
        return dto;
    }
}


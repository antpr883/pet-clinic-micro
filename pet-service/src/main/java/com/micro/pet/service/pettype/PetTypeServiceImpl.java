package com.micro.pet.service.pettype;

import com.micro.pet.data.dto.mapper.PetTypeMapper;
import com.micro.pet.data.dto.pettype.*;
import com.micro.pet.data.entities.PetTypeEntity;
import com.micro.pet.repository.PetTypeRepository;
import com.micro.pet.service.base.AbstractBaseService;
import com.micro.pet.service.graph.GraphBuilderMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing pet types.
 */
@Service
@Slf4j
@Transactional(readOnly = true)

public class PetTypeServiceImpl
        extends AbstractBaseService<
                PetTypeEntity,
                PetTypeDto,
                PetTypePreviewDto,
                PetTypeCreateDto,
                PetTypeRepository,
                PetTypeMapper>
        implements PetTypeService {

    public PetTypeServiceImpl(
            PetTypeRepository repository,
            PetTypeMapper mapper,
            GraphBuilderMappingService graphBuilderMappingService) {
        super(repository, mapper, graphBuilderMappingService);
    }

    @Override
    @Transactional
    public PetTypeDto create(PetTypeCreateDto createDto) {
        log.debug("Creating new pet type from PetTypeCreateDto");
        PetTypeEntity entity = mapper.fromCreateDto(createDto);
        entity = repository.save(entity);
        PetTypeDto dto = mapper.toDto(entity);
        log.debug("Created pet type with ID: {}", entity.getId());
        return dto;
    }

    @Override
    @Transactional
    public PetTypeDto update(Long id, PetTypeUpdateDto updateDto) {
        log.debug("Updating pet type with ID: {} from PetTypeUpdateDto", id);
        PetTypeEntity existingEntity = findByIdExecutor(id);
        mapper.updateFromUpdateDto(updateDto, existingEntity);
        existingEntity = repository.save(existingEntity);
        PetTypeDto dto = mapper.toDto(existingEntity);
        log.debug("Updated pet type with ID: {}", id);
        return dto;
    }
}


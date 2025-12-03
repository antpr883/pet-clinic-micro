package com.micro.pet.service.pet;

import com.micro.pet.data.constants.EntityConstants;
import com.micro.pet.data.constants.PetEntityGraphConstants;
import com.micro.pet.data.dto.mapper.PetMapper;
import com.micro.pet.data.dto.pet.*;
import com.micro.pet.data.entities.PetEntity;
import com.micro.pet.data.entities.PetInfoEntity;
import com.micro.pet.data.entities.PetTypeEntity;
import com.micro.pet.exception.ResourceNotFoundException;
import com.micro.pet.repository.PetRepository;
import com.micro.pet.repository.PetTypeRepository;
import com.micro.pet.repository.rsql.PetSearchService;
import com.micro.pet.service.base.AbstractBaseService;
import com.micro.pet.service.graph.GraphBuilderMappingService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for managing pets.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class PetServiceImpl
        extends AbstractBaseService<
                PetEntity,
                PetFullDto,
                PetPreviewDto,
                PetCreateDto,
                PetRepository,
                PetMapper>
        implements PetService {

    private final PetRepository petRepository;
    private final PetTypeRepository petTypeRepository;
    private final PetSearchService petSearchService;

    public PetServiceImpl(
            PetRepository repository,
            PetMapper mapper,
            GraphBuilderMappingService graphBuilderMappingService,
            PetTypeRepository petTypeRepository,
            PetSearchService petSearchService) {
        super(repository, mapper, graphBuilderMappingService);
        this.petRepository = repository;
        this.petTypeRepository = petTypeRepository;
        this.petSearchService = petSearchService;
    }

    @Override
    public PetFullDto findFullById(Long id) {
        log.debug("Finding full pet by id: {}", id);
        PetEntity entity = findByIdExecutor(id, PetEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return mapper.toDto(entity);
    }

    @Override
    public PetPreviewDto findPreviewById(Long id) {
        log.debug("Finding preview pet by id: {}", id);
        PetEntity entity = findByIdExecutor(id, PetEntityGraphConstants.PREVIEW_GRAPH_ATTRIBUTES);
        return mapper.toPreviewDto(entity);
    }

    @Override
    @Transactional
    public PetFullDto create(PetCreateDto createDto) {
        log.debug("Creating new pet from PetCreateDto");
        
        // Check that type exists
        PetTypeEntity type = petTypeRepository.findById(createDto.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("PetTypeEntity", createDto.getTypeId()));
        
        PetEntity entity = mapper.fromCreateDto(createDto);
        entity.setType(type);
        
        // Create PetInfoEntity if details are provided
        if (createDto.getDetails() != null) {
            PetInfoEntity info = PetInfoEntity.builder()
                    .details(createDto.getDetails())
                    .active(true)
                    .build();
            entity.setInfo(info);
            info.setPet(entity);
        }
        
        entity = repository.save(entity);
        // Entity is managed after save, relationships (type, info) are already loaded in persistence context
        // No need to reload - entity already has relationships initialized
        PetFullDto dto = mapper.toDto(entity);
        log.debug("Created pet with ID: {}", entity.getId());
        return dto;
    }

    @Override
    @Transactional
    public PetFullDto update(Long id, PetUpdateDto updateDto) {
        log.debug("Updating pet with ID: {} from PetUpdateDto", id);
        PetEntity existingEntity = findByIdExecutor(id, PetEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        
        // Update type if provided
        if (updateDto.getTypeId() != null) {
            PetTypeEntity type = petTypeRepository.findById(updateDto.getTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("PetTypeEntity", updateDto.getTypeId()));
            existingEntity.setType(type);
        }
        
        mapper.updateFromUpdateDto(updateDto, existingEntity);
        
        // Update PetInfoEntity if details are provided
        if (updateDto.getDetails() != null) {
            if (existingEntity.getInfo() == null) {
                PetInfoEntity info = PetInfoEntity.builder()
                        .pet(existingEntity)
                        .details(updateDto.getDetails())
                        .active(true)
                        .build();
                existingEntity.setInfo(info);
            } else {
                existingEntity.getInfo().setDetails(updateDto.getDetails());
            }
        }
        
        existingEntity = repository.save(existingEntity);
        // Entity is managed and already has relationships loaded, no need to reload
        PetFullDto dto = mapper.toDto(existingEntity);
        log.debug("Updated pet with ID: {}", id);
        return dto;
    }

    @Override
    public List<PetPreviewDto> findByOwnerId(Long ownerId) {
        log.debug("Finding pets by ownerId: {}", ownerId);
        List<PetEntity> entities = petRepository.findByOwnerIdAndActiveTrue(ownerId);
        return entities.stream()
                .map(mapper::toPreviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PetFullDto> search(String rsqlQuery) {
        log.debug("Searching pets with RSQL query: {} (always with type and info)", rsqlQuery);
        List<PetEntity> entities = petSearchService.search(rsqlQuery, PetEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return entities.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PetFullDto> searchWithPagination(String rsqlQuery, Pageable pageable) {
        log.debug("Searching pets with RSQL query: {}, pagination: {} (always with type and info)", 
                rsqlQuery, pageable);
        Page<PetEntity> page = petSearchService.searchWithPagination(
                rsqlQuery, 
                pageable, 
                PetEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return page.map(mapper::toDto);
    }

    @Override
    public List<PetPreviewDto> searchPreview(String rsqlQuery) {
        log.debug("Searching preview pets with RSQL query: {}", rsqlQuery);
        return petSearchService.search(rsqlQuery, PetEntityGraphConstants.PREVIEW_GRAPH_ATTRIBUTES).stream()
                .map(mapper::toPreviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PetPreviewDto> searchPreviewWithPagination(String rsqlQuery, Pageable pageable) {
        log.debug("Searching preview pets with RSQL query: {} and pagination: {}", rsqlQuery, pageable);
        return petSearchService.searchWithPagination(rsqlQuery, pageable, PetEntityGraphConstants.PREVIEW_GRAPH_ATTRIBUTES)
                .map(mapper::toPreviewDto);
    }

    @Override
    public Map<Long, Boolean> checkExistence(List<Long> ids) {
        log.debug("Checking existence of {} pets", ids.size());
        Map<Long, Boolean> result = new HashMap<>();
        // Use optimized single query instead of two separate queries per ID
        for (Long id : ids) {
            result.put(id, petRepository.existsAndActive(id));
        }
        return result;
    }

    @Override
    public List<PetPreviewDto> findPreviewsByIds(List<Long> ids) {
        log.debug("Getting previews for {} pets", ids.size());
        return findPreviewsByIds(ids, PetEntityGraphConstants.PREVIEW_GRAPH_ATTRIBUTES);
    }

    @Override
    public long countActive() {
        log.debug("Counting active pets");
        return petRepository.countActive();
    }

    @Override
    public Map<String, Long> countByType() {
        log.debug("Counting pets by type");
        List<Object[]> results = petRepository.countByTypeGrouped();
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : results) {
            String typeName = (String) row[0];
            Long count = (Long) row[1];
            result.put(typeName, count);
        }
        return result;
    }
}


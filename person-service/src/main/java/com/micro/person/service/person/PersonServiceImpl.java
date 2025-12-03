package com.micro.person.service.person;

import com.micro.person.data.constants.EntityConstants;
import com.micro.person.data.constants.PersonEntityGraphConstants;
import com.micro.person.data.dto.mapper.PersonMapper;
import com.micro.person.data.dto.person.PersonCreateDto;
import com.micro.person.data.dto.person.PersonDto;
import com.micro.person.data.dto.person.PersonFullDto;
import com.micro.person.data.dto.person.PersonFullExtendedDto;
import com.micro.person.data.dto.person.PersonFullPreviewDto;
import com.micro.person.data.dto.person.PersonPreviewDto;
import com.micro.person.data.dto.person.PersonUpdateDto;
import com.micro.person.data.entities.PersonEntity;
import com.micro.person.exception.ResourceNotFoundException;
import com.micro.person.repository.PersonRepository;
import com.micro.person.repository.rsql.PersonSearchService;
import com.micro.person.service.base.AbstractBaseService;
import com.micro.person.service.graph.GraphBuilderMappingService;
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
 * Service implementation for managing persons.
 * 
 * Version: 2.0 (Refactored - Clean API)
 * 
 * Main changes:
 * - Removed duplicate methods (searchWithContacts, searchWithAddresses, etc.)
 * - search() always loads contacts and addresses
 * - Added FullPreview API methods
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class PersonServiceImpl
        extends AbstractBaseService<
                PersonEntity,
                PersonDto,
                PersonPreviewDto,
                PersonDto,
                PersonRepository,
                PersonMapper>
        implements PersonService {

    private final PersonRepository personRepository;
    private final PersonSearchService personSearchService;

    public PersonServiceImpl(
            PersonRepository repository,
            PersonMapper mapper,
            GraphBuilderMappingService graphBuilderMappingService,
            PersonSearchService personSearchService) {
        super(repository, mapper, graphBuilderMappingService);
        this.personRepository = repository;
        this.personSearchService = personSearchService;
    }

    // ========== Optimized methods with PersonFullDto ==========

    @Override
    public PersonFullDto findFullById(Long id) {
        log.debug("Finding full person by id: {}", id);
        // Load with contacts and addresses through Entity Graph
        PersonEntity entity = findByIdExecutor(id, PersonEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return mapper.toFullDto(entity);
    }

    @Override
    public PersonPreviewDto findPreviewById(Long id) {
        log.debug("Finding preview person by id: {}", id);
        PersonEntity entity = findByIdExecutor(id);
        return mapper.toPreviewDto(entity);
    }
    
    @Override
    public PersonFullPreviewDto findFullPreviewById(Long id) {
        log.debug("Finding full preview person by id: {}", id);
        // Load with contacts and addresses to get primary contacts/address
        PersonEntity entity = findByIdExecutor(id, PersonEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return mapper.toFullPreviewDto(entity);
    }
    
    @Override
    public PersonFullExtendedDto findFullExtendedById(Long id) {
        log.debug("Finding full extended person by id: {}", id);
        // Load with contacts and addresses through Entity Graph
        PersonEntity entity = findByIdExecutor(id, PersonEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return mapper.toFullExtendedDto(entity);
    }

    @Override
    @Transactional
    public PersonFullDto create(PersonCreateDto createDto) {
        log.debug("Creating new person from PersonCreateDto");
        PersonEntity entity = mapper.fromCreateDto(createDto);
        entity = repository.save(entity);
        // Entity is managed after save, new Person has empty contacts/addresses collections (correct state)
        // No need to reload - entity already has relationships initialized as empty collections
        PersonFullDto dto = mapper.toFullDto(entity);
        log.debug("Created person with ID: {}", entity.getId());
        return dto;
    }

    @Override
    @Transactional
    public PersonFullDto update(Long id, PersonUpdateDto updateDto) {
        log.debug("Updating person with ID: {} from PersonUpdateDto", id);
        // Load with Entity Graph from start to avoid reload after save
        PersonEntity existingEntity = findByIdExecutor(id, PersonEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        mapper.updateFromUpdateDto(updateDto, existingEntity);
        existingEntity = repository.save(existingEntity);
        // Entity is managed and already has relationships loaded, no need to reload
        PersonFullDto dto = mapper.toFullDto(existingEntity);
        log.debug("Updated person with ID: {}", id);
        return dto;
    }

    @Override
    public long countActive() {
        log.debug("Counting active persons");
        return personRepository.countActive();
    }

    // ========== List All Methods ==========
    
    @Override
    public List<PersonFullDto> findAllFull() {
        log.debug("Finding all full persons (always with contacts and addresses)");
        // Always load contacts and addresses for FullDto
        List<PersonEntity> entities = personSearchService.search(
                EntityConstants.ACTIVE_RSQL_QUERY, 
                PersonEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return entities.stream()
                .map(mapper::toFullDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<PersonFullDto> findAllFullWithPagination(Pageable pageable) {
        log.debug("Finding all full persons with pagination: {} (always with contacts and addresses)", pageable);
        // Always load contacts and addresses for FullDto
        Page<PersonEntity> page = personSearchService.searchWithPagination(
                EntityConstants.ACTIVE_RSQL_QUERY, 
                pageable, 
                PersonEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return page.map(mapper::toFullDto);
    }
    
    @Override
    public List<PersonPreviewDto> findAllPreviews() {
        log.debug("Finding all preview persons");
        return findAllActivePreview();
    }
    
    @Override
    public Page<PersonPreviewDto> findAllPreviewsWithPagination(Pageable pageable) {
        log.debug("Finding all preview persons with pagination: {}", pageable);
        return findAllActivePreview(pageable);
    }

    // ========== RSQL Search Methods ==========

    @Override
    public List<PersonFullDto> search(String rsqlQuery) {
        log.debug("Searching persons with RSQL query: {} (always with contacts and addresses)", rsqlQuery);
        // Always load contacts and addresses for FullDto
        List<PersonEntity> entities = personSearchService.search(
                rsqlQuery, 
                PersonEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return entities.stream()
                .map(mapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PersonFullDto> searchWithPagination(String rsqlQuery, Pageable pageable) {
        log.debug("Searching persons with RSQL query: {}, pagination: {} (always with contacts and addresses)", 
                rsqlQuery, pageable);
        // Always load contacts and addresses for FullDto
        Page<PersonEntity> page = personSearchService.searchWithPagination(
                rsqlQuery, 
                pageable, 
                PersonEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return page.map(mapper::toFullDto);
    }
    
    @Override
    public List<PersonPreviewDto> searchPreview(String rsqlQuery) {
        log.debug("Searching preview persons with RSQL query: {}", rsqlQuery);
        return personSearchService.search(rsqlQuery).stream()
                .map(mapper::toPreviewDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<PersonPreviewDto> searchPreviewWithPagination(String rsqlQuery, Pageable pageable) {
        log.debug("Searching preview persons with RSQL query: {} and pagination: {}", rsqlQuery, pageable);
        return personSearchService.searchWithPagination(rsqlQuery, pageable)
                .map(mapper::toPreviewDto);
    }
    
    @Override
    public List<PersonFullPreviewDto> searchFullPreview(String rsqlQuery) {
        log.debug("Searching full preview persons with RSQL query: {}", rsqlQuery);
        // Завантажуємо контакти та адреси для отримання primary contacts/address
        List<PersonEntity> entities = personSearchService.search(
                rsqlQuery, 
                PersonEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return entities.stream()
                .map(mapper::toFullPreviewDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<PersonFullPreviewDto> searchFullPreviewWithPagination(String rsqlQuery, Pageable pageable) {
        log.debug("Searching full preview persons with RSQL query: {} and pagination: {}", rsqlQuery, pageable);
        // Завантажуємо контакти та адреси для отримання primary contacts/address
        Page<PersonEntity> page = personSearchService.searchWithPagination(
                rsqlQuery, 
                pageable, 
                PersonEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return page.map(mapper::toFullPreviewDto);
    }
    
    @Override
    public List<PersonFullPreviewDto> findAllFullPreviews() {
        log.debug("Finding all full preview persons");
        // Завантажуємо контакти та адреси для отримання primary contacts/address
        List<PersonEntity> entities = personSearchService.search(
                EntityConstants.ACTIVE_RSQL_QUERY, 
                PersonEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return entities.stream()
                .map(mapper::toFullPreviewDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<PersonFullPreviewDto> findAllFullPreviewsWithPagination(Pageable pageable) {
        log.debug("Finding all full preview persons with pagination: {}", pageable);
        // Завантажуємо контакти та адреси для отримання primary contacts/address
        Page<PersonEntity> page = personSearchService.searchWithPagination(
                EntityConstants.ACTIVE_RSQL_QUERY, 
                pageable, 
                PersonEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return page.map(mapper::toFullPreviewDto);
    }

    // ========== Microservice Integration Methods ==========

    @Override
    public Map<Long, Boolean> checkPersonsExist(List<Long> ids) {
        log.debug("Checking existence of {} persons", ids.size());
        Map<Long, Boolean> result = new HashMap<>();
        // Use optimized single query instead of two separate queries per ID
        for (Long id : ids) {
            result.put(id, personRepository.existsAndActive(id));
        }
        return result;
    }

    @Override
    public List<PersonPreviewDto> getPreviewsByIds(List<Long> ids) {
        log.debug("Getting previews for {} persons", ids.size());
        return findPreviewsByIds(ids);
    }
}


package com.micro.person.service.contact;

import com.micro.person.data.dto.contact.ContactCreateDto;
import com.micro.person.data.dto.contact.ContactDto;
import com.micro.person.data.dto.contact.ContactPreviewDto;
import com.micro.person.data.dto.contact.ContactUpdateDto;
import com.micro.person.data.dto.mapper.ContactMapper;
import com.micro.person.data.entities.ContactEntity;
import com.micro.person.data.entities.enums.ContactType;
import com.micro.person.repository.ContactRepository;
import com.micro.person.repository.rsql.ContactSearchService;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for managing contacts.
 * 
 * All basic CRUD operations are inherited from AbstractBaseService.
 * Only specific methods for Contact are implemented here.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ContactServiceImpl
        extends AbstractBaseService<
                ContactEntity,
                ContactDto,
                ContactPreviewDto,
                ContactDto,
                ContactRepository,
                ContactMapper>
        implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactSearchService contactSearchService;

    public ContactServiceImpl(
            ContactRepository repository,
            ContactMapper mapper,
            GraphBuilderMappingService graphBuilderMappingService,
            ContactSearchService contactSearchService) {
        super(repository, mapper, graphBuilderMappingService);
        this.contactRepository = repository;
        this.contactSearchService = contactSearchService;
    }

    // ========== Override create/update methods with proper DTOs ==========

    @Override
    @Transactional
    public ContactDto create(ContactCreateDto createDto) {
        log.debug("Creating new contact from ContactCreateDto");
        ContactEntity entity = mapper.fromCreateDto(createDto);
        entity = repository.save(entity);
        ContactDto dto = mapper.toDto(entity);
        log.debug("Created contact with ID: {}", entity.getId());
        return dto;
    }

    @Override
    @Transactional
    public ContactDto update(Long id, ContactUpdateDto updateDto) {
        log.debug("Updating contact with ID: {} from ContactUpdateDto", id);
        ContactEntity existingEntity = findByIdExecutor(id);
        mapper.updateFromUpdateDto(updateDto, existingEntity);
        existingEntity = repository.save(existingEntity);
        ContactDto dto = mapper.toDto(existingEntity);
        log.debug("Updated contact with ID: {}", id);
        return dto;
    }

    @Override
    public List<ContactDto> findByPersonId(Long personId) {
        log.debug("Finding contacts by personId: {}", personId);
        return contactRepository
                .findByPersonIdAndActiveTrue(personId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ContactDto> findPrimaryByPersonId(Long personId) {
        log.debug("Finding primary contact by personId: {}", personId);
        return contactRepository
                .findByPersonIdAndIsPrimaryTrueAndActiveTrue(personId)
                .map(mapper::toDto);
    }

    @Override
    public Optional<ContactPreviewDto> findPrimaryPreviewByPersonId(Long personId) {
        log.debug("Finding primary contact preview by personId: {}", personId);
        return contactRepository
                .findByPersonIdAndIsPrimaryTrueAndActiveTrue(personId)
                .map(mapper::toPreviewDto);
    }

    // ========== RSQL Search Methods ==========

    @Override
    public List<ContactDto> search(String rsqlQuery) {
        log.debug("Searching contacts with RSQL query: {}", rsqlQuery);
        return contactSearchService.search(rsqlQuery).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ContactDto> searchWithPagination(String rsqlQuery, Pageable pageable) {
        log.debug("Searching contacts with RSQL query: {} and pagination: {}", rsqlQuery, pageable);
        return contactSearchService.searchWithPagination(rsqlQuery, pageable)
                .map(mapper::toDto);
    }
}


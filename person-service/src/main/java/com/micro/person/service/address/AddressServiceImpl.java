package com.micro.person.service.address;

import com.micro.person.data.dto.address.AddressCreateDto;
import com.micro.person.data.dto.address.AddressDto;
import com.micro.person.data.dto.address.AddressPreviewDto;
import com.micro.person.data.dto.address.AddressUpdateDto;
import com.micro.person.data.dto.mapper.AddressMapper;
import com.micro.person.data.entities.AddressEntity;
import com.micro.person.data.entities.enums.AddressType;
import com.micro.person.repository.AddressRepository;
import com.micro.person.repository.rsql.AddressSearchService;
import com.micro.person.service.base.AbstractBaseService;
import com.micro.person.service.graph.GraphBuilderMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for managing addresses.
 * 
 * All basic CRUD operations are inherited from AbstractBaseService.
 * Only specific methods for Address are implemented here.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class AddressServiceImpl
        extends AbstractBaseService<
                AddressEntity,
                AddressDto,
                AddressPreviewDto,
                AddressDto,
                AddressRepository,
                AddressMapper>
        implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressSearchService addressSearchService;

    public AddressServiceImpl(
            AddressRepository repository,
            AddressMapper mapper,
            GraphBuilderMappingService graphBuilderMappingService,
            AddressSearchService addressSearchService) {
        super(repository, mapper, graphBuilderMappingService);
        this.addressRepository = repository;
        this.addressSearchService = addressSearchService;
    }

    // ========== Override create/update methods with proper DTOs ==========

    @Override
    @Transactional
    public AddressDto create(AddressCreateDto createDto) {
        log.debug("Creating new address from AddressCreateDto");
        AddressEntity entity = mapper.fromCreateDto(createDto);
        entity = repository.save(entity);
        AddressDto dto = mapper.toDto(entity);
        log.debug("Created address with ID: {}", entity.getId());
        return dto;
    }

    @Override
    @Transactional
    public AddressDto update(Long id, AddressUpdateDto updateDto) {
        log.debug("Updating address with ID: {} from AddressUpdateDto", id);
        AddressEntity existingEntity = findByIdExecutor(id);
        mapper.updateFromUpdateDto(updateDto, existingEntity);
        existingEntity = repository.save(existingEntity);
        AddressDto dto = mapper.toDto(existingEntity);
        log.debug("Updated address with ID: {}", id);
        return dto;
    }

    @Override
    public List<AddressDto> findByPersonId(Long personId) {
        log.debug("Finding addresses by personId: {}", personId);
        return addressRepository
                .findByPersonIdAndActiveTrue(personId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AddressDto> findPrimaryByPersonId(Long personId) {
        log.debug("Finding primary address by personId: {}", personId);
        return addressRepository
                .findByPersonIdAndAddressTypeAndActiveTrue(personId, AddressType.HOME)
                .stream()
                .findFirst()
                .map(mapper::toDto);
    }

    @Override
    public Optional<AddressPreviewDto> findPrimaryPreviewByPersonId(Long personId) {
        log.debug("Finding primary address preview by personId: {}", personId);
        return addressRepository
                .findByPersonIdAndAddressTypeAndActiveTrue(personId, AddressType.HOME)
                .stream()
                .findFirst()
                .map(mapper::toPreviewDto);
    }

    // ========== RSQL Search Methods ==========

    @Override
    public List<AddressDto> search(String rsqlQuery) {
        log.debug("Searching addresses with RSQL query: {}", rsqlQuery);
        return addressSearchService.search(rsqlQuery).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AddressDto> searchWithPagination(String rsqlQuery, Pageable pageable) {
        log.debug("Searching addresses with RSQL query: {} and pagination: {}", rsqlQuery, pageable);
        return addressSearchService.searchWithPagination(rsqlQuery, pageable)
                .map(mapper::toDto);
    }
}


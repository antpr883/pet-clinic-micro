package com.micro.person.service.base;

import com.micro.person.data.constants.EntityConstants;
import com.micro.person.data.dto.base.BaseDto;
import com.micro.person.data.dto.base.BasePreviewDto;
import com.micro.person.data.dto.base.DtoMarker;
import com.micro.person.data.dto.mapper.BaseMapper;
import com.micro.person.data.entities.BaseActiveEntity;
import com.micro.person.exception.ResourceNotFoundException;
import com.micro.person.repository.CustomJpaRepository;
import com.micro.person.service.graph.GraphBuilderMappingService;
import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Abstract base service with CRUD operations implementation.
 * 
 * Uses Template Method Pattern to eliminate code duplication.
 * Adapted for our project:
 * - AppResponse wrapper for responses
 * - Soft delete instead of status enum
 * - BaseActiveEntity as base class
 * - Cosium Entity Graph support
 * 
 * @param <E> Entity type (must extend BaseActiveEntity)
 * @param <D> DTO type (must extend BaseDto)
 * @param <P> Preview DTO type (must extend BasePreviewDto)
 * @param <R> Request DTO type (must extend DtoMarker)
 * @param <REPO> Repository type (must extend CustomJpaRepository<E, Long>)
 * @param <M> Mapper type (must extend BaseMapper<E, D, P, R>)
 */
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public abstract class AbstractBaseService<
        E extends BaseActiveEntity,
        D extends BaseDto,
        P extends BasePreviewDto,
        R extends DtoMarker,
        REPO extends CustomJpaRepository<E, Long>,
        M extends BaseMapper<E, D, P, R>>
        implements BaseService<D, R>, PreviewBaseService<P> {

    protected final REPO repository;
    protected final M mapper;
    protected final Class<E> entityClass;
    protected final GraphBuilderMappingService graphBuilderMappingService;

    @SuppressWarnings("unchecked")
    protected AbstractBaseService(REPO repository, M mapper, 
                                  GraphBuilderMappingService graphBuilderMappingService) {
        this.repository = repository;
        this.mapper = mapper;
        this.graphBuilderMappingService = graphBuilderMappingService;
        
        // Reflection to determine entityClass from generic parameters
        this.entityClass = (Class<E>) ((ParameterizedType) 
            getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        
        log.debug("Initialized AbstractBaseService for entity: {}", entityClass.getSimpleName());
    }

    // ========== BaseService Implementation ==========

    @Override
    public D findById(Long id, String... graphAttributes) {
        E entity = findByIdExecutor(id, graphAttributes);
        return mapper.toDto(entity);
    }

    @Override
    public List<D> findAll(String... graphAttributes) {
        return findAllExecutor(graphAttributes).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<D> findAll(Pageable pageable, String... graphAttributes) {
        EntityGraph graph = graphBuilderMappingService
                .getGraphWithAttributes(entityClass, graphAttributes);
        
        Page<E> entityPage = graph != null 
                ? repository.findAll(pageable, graph)
                : repository.findAll(pageable);
        
        return entityPage.map(mapper::toDto);
    }

    @Override
    public List<D> findAllActive(String... graphAttributes) {
        List<E> entities = repository.findAllActive();
        
        // If Entity Graph is needed, apply it through Specification
        if (graphAttributes != null && graphAttributes.length > 0) {
            return findAllActiveWithGraph(graphAttributes).stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        }
        
        return entities.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<D> findAllActive(Pageable pageable, String... graphAttributes) {
        EntityGraph graph = graphBuilderMappingService
                .getGraphWithAttributes(entityClass, graphAttributes);
        
        // Use Specification to filter active
        Specification<E> spec = (root, query, cb) -> 
            cb.equal(root.get(EntityConstants.ACTIVE_FIELD), true);
        
        Page<E> entityPage = graph != null
                ? repository.findAll(spec, pageable, graph)
                : repository.findAll(spec, pageable);
        
        return entityPage.map(mapper::toDto);
    }

    @Override
    public List<D> findByIds(List<Long> ids, String... graphAttributes) {
        return findByIdsExecutor(ids, graphAttributes).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public D create(R requestDto) {
        log.debug("Creating new {} from request DTO", entityClass.getSimpleName());
        E entity = mapper.toEntity(requestDto);
        entity = repository.save(entity);
        D dto = mapper.toDto(entity);
        log.debug("Created {} with ID: {}", entityClass.getSimpleName(), entity.getId());
        return dto;
    }

    @Override
    @Transactional
    public D update(Long id, R requestDto) {
        log.debug("Updating {} with ID: {}", entityClass.getSimpleName(), id);
        E existingEntity = findByIdExecutor(id);
        mapper.updateEntityFromDto(requestDto, existingEntity);
        existingEntity = repository.save(existingEntity);
        D dto = mapper.toDto(existingEntity);
        log.debug("Updated {} with ID: {}", entityClass.getSimpleName(), id);
        return dto;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting {} with ID: {}", entityClass.getSimpleName(), id);
        findByIdExecutor(id); // Check that exists
        repository.deleteById(id);
        log.debug("Deleted {} with ID: {}", entityClass.getSimpleName(), id);
    }

    @Override
    @Transactional
    public D softDelete(Long id) {
        log.debug("Soft deleting {} with ID: {}", entityClass.getSimpleName(), id);
        E entity = repository.softDelete(id, null)
                .orElseThrow(() -> new ResourceNotFoundException(
                        entityClass.getSimpleName(), id));
        D dto = mapper.toDto(entity);
        log.debug("Soft deleted {} with ID: {}", entityClass.getSimpleName(), id);
        return dto;
    }

    @Override
    @Transactional
    public int softDeleteAll(List<Long> ids) {
        log.debug("Soft deleting {} entities with IDs: {}", entityClass.getSimpleName(), ids);
        int deletedCount = repository.softDeleteAll(ids, null);
        log.debug("Soft deleted {} entities", deletedCount);
        return deletedCount;
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public boolean isActive(Long id) {
        return repository.isActive(id);
    }

    @Override
    public Map<Long, Boolean> existsByIds(List<Long> ids) {
        // Use method from Repository if exists, otherwise check through existsById
        Map<Long, Boolean> result = new HashMap<>();
        for (Long id : ids) {
            result.put(id, repository.existsById(id));
        }
        return result;
    }

    @Override
    @Transactional
    public List<D> createAll(List<R> requestDtos) {
        log.debug("Creating batch of {} {}", requestDtos.size(), entityClass.getSimpleName());
        List<E> entities = requestDtos.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        entities = repository.saveAll(entities);
        List<D> dtos = entities.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        log.debug("Created {} {} entities", dtos.size(), entityClass.getSimpleName());
        return dtos;
    }

    // ========== PreviewBaseService Implementation ==========

    @Override
    public P findPreviewById(Long id, String... graphAttributes) {
        E entity = findByIdExecutor(id, graphAttributes);
        return mapper.toPreviewDto(entity);
    }

    @Override
    public List<P> findAllPreview(String... graphAttributes) {
        return findAllExecutor(graphAttributes).stream()
                .map(mapper::toPreviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<P> findAllPreview(Pageable pageable, String... graphAttributes) {
        EntityGraph graph = graphBuilderMappingService
                .getGraphWithAttributes(entityClass, graphAttributes);
        
        Page<E> entityPage = graph != null 
                ? repository.findAll(pageable, graph)
                : repository.findAll(pageable);
        
        return entityPage.map(mapper::toPreviewDto);
    }

    @Override
    public List<P> findPreviewsByIds(List<Long> ids, String... graphAttributes) {
        return findByIdsExecutor(ids, graphAttributes).stream()
                .map(mapper::toPreviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<P> findAllActivePreview(String... graphAttributes) {
        // Use direct entity query with Entity Graph to avoid N+1 problem
        List<E> entities = findAllActiveWithGraph(graphAttributes);
        return entities.stream()
                .map(mapper::toPreviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<P> findAllActivePreview(Pageable pageable, String... graphAttributes) {
        // Use direct entity query with Entity Graph to avoid N+1 problem
        EntityGraph graph = graphBuilderMappingService
                .getGraphWithAttributes(entityClass, graphAttributes);
        
        Specification<E> spec = (root, query, cb) -> 
            cb.equal(root.get(EntityConstants.ACTIVE_FIELD), true);
        
        Page<E> entityPage = graph != null
                ? repository.findAll(spec, pageable, graph)
                : repository.findAll(spec, pageable);
        
        return entityPage.map(mapper::toPreviewDto);
    }

    // ========== Protected Template Methods ==========

    /**
     * Executor for findById with Entity Graph support.
     */
    protected E findByIdExecutor(Long id, String... graphAttributes) {
        EntityGraph graph = graphBuilderMappingService
                .getGraphWithAttributes(entityClass, graphAttributes);
        
        return graph != null
                ? repository.findById(id, graph)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                entityClass.getSimpleName(), id))
                : repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                entityClass.getSimpleName(), id));
    }

    /**
     * Executor for findById without Entity Graph.
     */
    protected E findByIdExecutor(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        entityClass.getSimpleName(), id));
    }

    /**
     * Executor for findAll with Entity Graph support.
     */
    protected List<E> findAllExecutor(String... graphAttributes) {
        EntityGraph graph = graphBuilderMappingService
                .getGraphWithAttributes(entityClass, graphAttributes);
        
        Iterable<E> entitiesIterable = graph != null
                ? repository.findAll(graph)
                : repository.findAll();
        
        return StreamSupport.stream(entitiesIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Executor for findByIds with Entity Graph support.
     */
    protected List<E> findByIdsExecutor(List<Long> ids, String... graphAttributes) {
        EntityGraph graph = graphBuilderMappingService
                .getGraphWithAttributes(entityClass, graphAttributes);
        
        Iterable<E> entitiesIterable = graph != null
                ? repository.findAllById(ids, graph)
                : repository.findAllById(ids);
        
        return StreamSupport.stream(entitiesIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Find all active with Entity Graph through Specification.
     */
    protected List<E> findAllActiveWithGraph(String... graphAttributes) {
        EntityGraph graph = graphBuilderMappingService
                .getGraphWithAttributes(entityClass, graphAttributes);
        
        Specification<E> spec = (root, query, cb) -> 
            cb.equal(root.get(EntityConstants.ACTIVE_FIELD), true);
        
        Iterable<E> entitiesIterable = graph != null
                ? repository.findAll(spec, graph)
                : repository.findAll(spec);
        
        return StreamSupport.stream(entitiesIterable.spliterator(), false)
                .collect(Collectors.toList());
    }
}


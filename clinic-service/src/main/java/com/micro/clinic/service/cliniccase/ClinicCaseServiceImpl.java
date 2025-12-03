package com.micro.clinic.service.cliniccase;

import com.micro.clinic.data.constants.ClinicEntityGraphConstants;
import com.micro.clinic.data.dto.cliniccase.*;
import com.micro.clinic.data.dto.mapper.ClinicCaseMapper;
import com.micro.clinic.data.entities.ClinicCaseEntity;
import com.micro.clinic.data.entities.enums.ClinicCaseStatus;
import com.micro.clinic.repository.ClinicCaseRepository;
import com.micro.clinic.service.base.AbstractBaseService;
import com.micro.clinic.service.graph.GraphBuilderMappingService;
import com.micro.clinic.statemachine.ClinicCaseEvent;
import com.micro.clinic.statemachine.ClinicCaseStateMachineService;
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
 * Service implementation for managing clinic cases.
 * 
 * Includes State Machine logic for status transitions.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ClinicCaseServiceImpl
        extends AbstractBaseService<
                ClinicCaseEntity,
                ClinicCaseFullDto,
                ClinicCasePreviewDto,
                ClinicCaseCreateDto,
                ClinicCaseRepository,
                ClinicCaseMapper>
        implements ClinicCaseService {

    private final ClinicCaseRepository clinicCaseRepository;
    private final ClinicCaseStateMachineService stateMachineService;

    public ClinicCaseServiceImpl(
            ClinicCaseRepository repository,
            ClinicCaseMapper mapper,
            GraphBuilderMappingService graphBuilderMappingService,
            ClinicCaseStateMachineService stateMachineService) {
        super(repository, mapper, graphBuilderMappingService);
        this.clinicCaseRepository = repository;
        this.stateMachineService = stateMachineService;
    }

    @Override
    public ClinicCaseFullDto findFullById(Long id) {
        log.debug("Finding full clinic case by id: {}", id);
        ClinicCaseEntity entity = findByIdExecutor(id, ClinicEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        return mapper.toDto(entity);
    }

    @Override
    public ClinicCasePreviewDto findPreviewById(Long id) {
        log.debug("Finding preview clinic case by id: {}", id);
        ClinicCaseEntity entity = findByIdExecutor(id, ClinicEntityGraphConstants.PREVIEW_GRAPH_ATTRIBUTES);
        return mapper.toPreviewDto(entity);
    }

    @Override
    @Transactional
    public ClinicCaseFullDto create(ClinicCaseCreateDto createDto) {
        log.debug("Creating new clinic case from ClinicCaseCreateDto");
        
        // TODO: Validate pet and owner existence via Person/Pet Services
        // For now, we'll add this validation later when RestTemplate/WebClient is configured
        
        ClinicCaseEntity entity = mapper.fromCreateDto(createDto);
        entity.setStatus(ClinicCaseStatus.REGISTERED);
        entity.setPriority(createDto.getPriority() != null ? createDto.getPriority() : com.micro.clinic.data.entities.enums.CasePriority.NORMAL);
        
        entity = repository.save(entity);
        // Entity is managed after save, no need to reload
        ClinicCaseFullDto dto = mapper.toDto(entity);
        log.debug("Created clinic case with ID: {}", entity.getId());
        return dto;
    }

    @Override
    @Transactional
    public ClinicCaseFullDto update(Long id, ClinicCaseUpdateDto updateDto) {
        log.debug("Updating clinic case with ID: {} from ClinicCaseUpdateDto", id);
        ClinicCaseEntity existingEntity = findByIdExecutor(id, ClinicEntityGraphConstants.FULL_GRAPH_ATTRIBUTES);
        
        mapper.updateFromUpdateDto(updateDto, existingEntity);
        
        existingEntity = repository.save(existingEntity);
        // Entity is managed and already has relationships loaded, no need to reload
        ClinicCaseFullDto dto = mapper.toDto(existingEntity);
        log.debug("Updated clinic case with ID: {}", id);
        return dto;
    }

    @Override
    public List<ClinicCasePreviewDto> findByPetId(Long petId) {
        log.debug("Finding cases by petId: {}", petId);
        List<ClinicCaseEntity> entities = clinicCaseRepository.findByPetIdAndActiveTrue(petId);
        return entities.stream()
                .map(mapper::toPreviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicCasePreviewDto> findByOwnerId(Long ownerId) {
        log.debug("Finding cases by ownerId: {}", ownerId);
        List<ClinicCaseEntity> entities = clinicCaseRepository.findByOwnerIdAndActiveTrue(ownerId);
        return entities.stream()
                .map(mapper::toPreviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicCasePreviewDto> findByAssignedVetId(Long vetId) {
        log.debug("Finding cases by assignedVetId: {}", vetId);
        List<ClinicCaseEntity> entities = clinicCaseRepository.findByAssignedVetIdAndActiveTrue(vetId);
        return entities.stream()
                .map(mapper::toPreviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicCaseFullDto> search(String rsqlQuery) {
        // TODO: Implement RSQL search
        throw new UnsupportedOperationException("RSQL search not yet implemented");
    }

    @Override
    public Page<ClinicCaseFullDto> searchWithPagination(String rsqlQuery, Pageable pageable) {
        // TODO: Implement RSQL search with pagination
        throw new UnsupportedOperationException("RSQL search not yet implemented");
    }

    @Override
    public List<ClinicCasePreviewDto> searchPreview(String rsqlQuery) {
        // TODO: Implement RSQL search preview
        throw new UnsupportedOperationException("RSQL search not yet implemented");
    }

    @Override
    public Page<ClinicCasePreviewDto> searchPreviewWithPagination(String rsqlQuery, Pageable pageable) {
        // TODO: Implement RSQL search preview with pagination
        throw new UnsupportedOperationException("RSQL search not yet implemented");
    }

    @Override
    public Map<Long, Boolean> checkExistence(List<Long> ids) {
        log.debug("Checking existence of {} clinic cases", ids.size());
        Map<Long, Boolean> result = new HashMap<>();
        // Use optimized single query instead of two separate queries per ID
        for (Long id : ids) {
            result.put(id, clinicCaseRepository.existsAndActive(id));
        }
        return result;
    }

    @Override
    public List<ClinicCasePreviewDto> findPreviewsByIds(List<Long> ids) {
        log.debug("Getting previews for {} clinic cases", ids.size());
        return findPreviewsByIds(ids, ClinicEntityGraphConstants.PREVIEW_GRAPH_ATTRIBUTES);
    }

    @Override
    public long countActive() {
        log.debug("Counting active clinic cases");
        return clinicCaseRepository.countActive();
    }

    // ========== State Machine Methods ==========

    @Override
    @Transactional
    public void completeInitialCheckup(Long caseId) {
        log.debug("Completing initial checkup for case ID: {}", caseId);
        stateMachineService.sendEvent(caseId, ClinicCaseEvent.INITIAL_CHECKUP_COMPLETED);
    }

    @Override
    @Transactional
    public void startDiagnostics(Long caseId) {
        log.debug("Starting diagnostics for case ID: {}", caseId);
        stateMachineService.sendEvent(caseId, ClinicCaseEvent.START_DIAGNOSTICS);
    }

    @Override
    @Transactional
    public void planTreatment(Long caseId) {
        log.debug("Planning treatment for case ID: {}", caseId);
        stateMachineService.sendEvent(caseId, ClinicCaseEvent.TREATMENT_PLANNED);
    }

    @Override
    @Transactional
    public void startProcedures(Long caseId) {
        log.debug("Starting procedures for case ID: {}", caseId);
        stateMachineService.sendEvent(caseId, ClinicCaseEvent.PROCEDURES_STARTED);
    }

    @Override
    @Transactional
    public void startHospitalization(Long caseId) {
        log.debug("Starting hospitalization for case ID: {}", caseId);
        stateMachineService.sendEvent(caseId, ClinicCaseEvent.HOSPITALIZATION_STARTED);
    }

    @Override
    @Transactional
    public void discharge(Long caseId) {
        log.debug("Discharging case ID: {}", caseId);
        stateMachineService.sendEvent(caseId, ClinicCaseEvent.DISCHARGED);
    }

    @Override
    @Transactional
    public void cancel(Long caseId) {
        log.debug("Cancelling case ID: {}", caseId);
        stateMachineService.sendEvent(caseId, ClinicCaseEvent.CANCELLED);
    }
}


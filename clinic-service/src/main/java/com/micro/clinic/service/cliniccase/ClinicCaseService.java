package com.micro.clinic.service.cliniccase;

import com.micro.clinic.data.dto.cliniccase.*;
import com.micro.clinic.service.base.BaseService;
import com.micro.clinic.service.base.PreviewBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service for managing clinic cases.
 */
public interface ClinicCaseService extends 
        BaseService<ClinicCaseFullDto, ClinicCaseCreateDto>,  
        PreviewBaseService<ClinicCasePreviewDto> {
    
    /**
     * Get full clinic case information by ID.
     * Always returns ClinicCaseFullDto with all relationships.
     */
    ClinicCaseFullDto findFullById(Long id);
    
    /**
     * Get clinic case preview by ID.
     */
    ClinicCasePreviewDto findPreviewById(Long id);
    
    /**
     * Create new clinic case from ClinicCaseCreateDto.
     * Validates pet and owner existence via Person/Pet Services.
     */
    ClinicCaseFullDto create(ClinicCaseCreateDto createDto);
    
    /**
     * Update clinic case from ClinicCaseUpdateDto.
     */
    ClinicCaseFullDto update(Long id, ClinicCaseUpdateDto updateDto);
    
    /**
     * Find all cases by pet ID.
     */
    List<ClinicCasePreviewDto> findByPetId(Long petId);
    
    /**
     * Find all cases by owner ID.
     */
    List<ClinicCasePreviewDto> findByOwnerId(Long ownerId);
    
    /**
     * Find all cases by assigned vet ID.
     */
    List<ClinicCasePreviewDto> findByAssignedVetId(Long vetId);
    
    /**
     * RSQL search for clinic cases.
     */
    List<ClinicCaseFullDto> search(String rsqlQuery);
    
    /**
     * RSQL search for clinic cases with pagination.
     */
    Page<ClinicCaseFullDto> searchWithPagination(String rsqlQuery, Pageable pageable);
    
    /**
     * RSQL search for clinic cases (Preview).
     */
    List<ClinicCasePreviewDto> searchPreview(String rsqlQuery);
    
    /**
     * RSQL search for clinic cases (Preview) with pagination.
     */
    Page<ClinicCasePreviewDto> searchPreviewWithPagination(String rsqlQuery, Pageable pageable);
    
    /**
     * Bulk check clinic case existence.
     */
    Map<Long, Boolean> checkExistence(List<Long> ids);
    
    /**
     * Get Preview cases by list of IDs.
     */
    List<ClinicCasePreviewDto> findPreviewsByIds(List<Long> ids);
    
    /**
     * Count active cases.
     */
    long countActive();
    
    // ========== State Machine Methods ==========
    
    /**
     * Complete initial checkup (REGISTERED → INITIAL_CHECKUP_COMPLETED).
     * Requires at least one visit with type=INITIAL.
     */
    void completeInitialCheckup(Long caseId);
    
    /**
     * Start diagnostics (INITIAL_CHECKUP_COMPLETED → DIAGNOSTICS_IN_PROGRESS).
     */
    void startDiagnostics(Long caseId);
    
    /**
     * Complete diagnostics and plan treatment (DIAGNOSTICS_IN_PROGRESS → TREATMENT_PLANNED).
     * Requires at least one confirmed diagnosis.
     */
    void planTreatment(Long caseId);
    
    /**
     * Start procedures (TREATMENT_PLANNED → PROCEDURES_IN_PROGRESS).
     * Requires at least one planned procedure.
     */
    void startProcedures(Long caseId);
    
    /**
     * Start hospitalization (PROCEDURES_IN_PROGRESS → HOSPITALIZED).
     */
    void startHospitalization(Long caseId);
    
    /**
     * Discharge from hospitalization (HOSPITALIZED → DISCHARGED).
     * Sets endAt in HospitalizationEntity.
     */
    void discharge(Long caseId);
    
    /**
     * Cancel case (any status → CANCELLED).
     */
    void cancel(Long caseId);
}


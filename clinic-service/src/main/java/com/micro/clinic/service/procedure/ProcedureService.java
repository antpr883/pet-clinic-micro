package com.micro.clinic.service.procedure;

import com.micro.clinic.data.dto.procedure.ProcedureCreateDto;
import com.micro.clinic.data.dto.procedure.ProcedureDto;
import com.micro.clinic.data.dto.procedure.ProcedureStatusUpdateDto;

import java.util.List;

/**
 * Service for managing procedures.
 */
public interface ProcedureService {
    
    /**
     * Create a new procedure.
     */
    ProcedureDto create(ProcedureCreateDto createDto);
    
    /**
     * Update procedure status.
     */
    ProcedureDto updateStatus(Long id, ProcedureStatusUpdateDto updateDto);
    
    /**
     * Find all procedures for a clinic case.
     */
    List<ProcedureDto> findByClinicCaseId(Long caseId);
}


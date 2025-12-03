package com.micro.clinic.service.hospitalization;

import com.micro.clinic.data.dto.hospitalization.HospitalizationCreateDto;
import com.micro.clinic.data.dto.hospitalization.HospitalizationDto;

/**
 * Service for managing hospitalizations.
 */
public interface HospitalizationService {
    
    /**
     * Create a new hospitalization.
     */
    HospitalizationDto create(HospitalizationCreateDto createDto);
    
    /**
     * Finish hospitalization (set endAt).
     */
    HospitalizationDto finish(Long id);
    
    /**
     * Find hospitalization for a clinic case.
     */
    HospitalizationDto findByClinicCaseId(Long caseId);
}


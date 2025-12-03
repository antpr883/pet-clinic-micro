package com.micro.clinic.service.clinicvisit;

import com.micro.clinic.data.dto.clinicvisit.ClinicVisitCreateDto;
import com.micro.clinic.data.dto.clinicvisit.ClinicVisitDto;

import java.util.List;

/**
 * Service for managing clinic visits.
 */
public interface ClinicVisitService {
    
    /**
     * Create a new clinic visit.
     */
    ClinicVisitDto create(ClinicVisitCreateDto createDto);
    
    /**
     * Find all visits for a clinic case.
     */
    List<ClinicVisitDto> findByClinicCaseId(Long caseId);
}


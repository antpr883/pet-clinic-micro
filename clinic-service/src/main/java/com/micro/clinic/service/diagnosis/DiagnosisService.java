package com.micro.clinic.service.diagnosis;

import com.micro.clinic.data.dto.diagnosis.DiagnosisCreateDto;
import com.micro.clinic.data.dto.diagnosis.DiagnosisDto;
import com.micro.clinic.data.dto.diagnosis.DiagnosisUpdateDto;

import java.util.List;

/**
 * Service for managing diagnoses.
 */
public interface DiagnosisService {
    
    /**
     * Create a new diagnosis.
     */
    DiagnosisDto create(DiagnosisCreateDto createDto);
    
    /**
     * Update diagnosis.
     */
    DiagnosisDto update(Long id, DiagnosisUpdateDto updateDto);
    
    /**
     * Find all diagnoses for a clinic case.
     */
    List<DiagnosisDto> findByClinicCaseId(Long caseId);
}


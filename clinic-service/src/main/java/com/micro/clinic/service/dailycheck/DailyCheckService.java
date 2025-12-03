package com.micro.clinic.service.dailcheck;

import com.micro.clinic.data.dto.dailycheck.DailyCheckCreateDto;
import com.micro.clinic.data.dto.dailycheck.DailyCheckDto;

import java.util.List;

/**
 * Service for managing daily checks.
 */
public interface DailyCheckService {
    
    /**
     * Create a new daily check.
     */
    DailyCheckDto create(DailyCheckCreateDto createDto);
    
    /**
     * Find all daily checks for a hospitalization.
     */
    List<DailyCheckDto> findByHospitalizationId(Long hospitalizationId);
}


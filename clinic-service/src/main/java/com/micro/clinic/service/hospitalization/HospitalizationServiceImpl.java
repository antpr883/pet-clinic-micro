package com.micro.clinic.service.hospitalization;

import com.micro.clinic.data.dto.hospitalization.HospitalizationCreateDto;
import com.micro.clinic.data.dto.hospitalization.HospitalizationDto;
import com.micro.clinic.data.dto.mapper.HospitalizationMapper;
import com.micro.clinic.data.entities.ClinicCaseEntity;
import com.micro.clinic.data.entities.HospitalizationEntity;
import com.micro.clinic.data.entities.enums.HospitalizationStatus;
import com.micro.clinic.exception.ResourceNotFoundException;
import com.micro.clinic.repository.ClinicCaseRepository;
import com.micro.clinic.repository.HospitalizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementation for managing hospitalizations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalizationServiceImpl implements HospitalizationService {

    private final HospitalizationRepository hospitalizationRepository;
    private final ClinicCaseRepository caseRepository;
    private final HospitalizationMapper mapper;

    @Override
    @Transactional
    public HospitalizationDto create(HospitalizationCreateDto createDto) {
        log.debug("Creating new hospitalization for case ID: {}", createDto.getClinicCaseId());
        
        // Validate clinic case exists
        ClinicCaseEntity clinicCase = caseRepository.findById(createDto.getClinicCaseId())
                .orElseThrow(() -> new ResourceNotFoundException("ClinicCaseEntity", createDto.getClinicCaseId()));
        
        // Check if hospitalization already exists
        hospitalizationRepository.findByClinicCaseIdAndActiveTrue(createDto.getClinicCaseId())
                .ifPresent(h -> {
                    throw new IllegalStateException("Hospitalization already exists for this case");
                });
        
        HospitalizationEntity entity = mapper.toEntity(createDto);
        entity.setClinicCase(clinicCase);
        entity.setStatus(HospitalizationStatus.ACTIVE);
        
        entity = hospitalizationRepository.save(entity);
        HospitalizationDto dto = mapper.toDto(entity);
        log.debug("Created hospitalization with ID: {}", entity.getId());
        return dto;
    }

    @Override
    @Transactional
    public HospitalizationDto finish(Long id) {
        log.debug("Finishing hospitalization with ID: {}", id);
        HospitalizationEntity existingEntity = hospitalizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HospitalizationEntity", id));
        
        existingEntity.setEndAt(LocalDateTime.now());
        existingEntity.setStatus(HospitalizationStatus.DISCHARGED);
        
        existingEntity = hospitalizationRepository.save(existingEntity);
        HospitalizationDto dto = mapper.toDto(existingEntity);
        log.debug("Finished hospitalization with ID: {}", id);
        return dto;
    }

    @Override
    public HospitalizationDto findByClinicCaseId(Long caseId) {
        log.debug("Finding hospitalization for clinic case ID: {}", caseId);
        HospitalizationEntity entity = hospitalizationRepository.findByClinicCaseIdAndActiveTrue(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("HospitalizationEntity", "clinicCaseId", caseId));
        return mapper.toDto(entity);
    }
}


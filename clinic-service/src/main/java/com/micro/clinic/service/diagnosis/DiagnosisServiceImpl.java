package com.micro.clinic.service.diagnosis;

import com.micro.clinic.data.dto.diagnosis.DiagnosisCreateDto;
import com.micro.clinic.data.dto.diagnosis.DiagnosisDto;
import com.micro.clinic.data.dto.diagnosis.DiagnosisUpdateDto;
import com.micro.clinic.data.dto.mapper.DiagnosisMapper;
import com.micro.clinic.data.entities.ClinicCaseEntity;
import com.micro.clinic.data.entities.DiagnosisEntity;
import com.micro.clinic.exception.ResourceNotFoundException;
import com.micro.clinic.repository.ClinicCaseRepository;
import com.micro.clinic.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing diagnoses.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final ClinicCaseRepository caseRepository;
    private final DiagnosisMapper mapper;

    @Override
    @Transactional
    public DiagnosisDto create(DiagnosisCreateDto createDto) {
        log.debug("Creating new diagnosis for case ID: {}", createDto.getClinicCaseId());
        
        // Validate clinic case exists
        ClinicCaseEntity clinicCase = caseRepository.findById(createDto.getClinicCaseId())
                .orElseThrow(() -> new ResourceNotFoundException("ClinicCaseEntity", createDto.getClinicCaseId()));
        
        DiagnosisEntity entity = mapper.toEntity(createDto);
        entity.setClinicCase(clinicCase);
        
        entity = diagnosisRepository.save(entity);
        DiagnosisDto dto = mapper.toDto(entity);
        log.debug("Created diagnosis with ID: {}", entity.getId());
        return dto;
    }

    @Override
    @Transactional
    public DiagnosisDto update(Long id, DiagnosisUpdateDto updateDto) {
        log.debug("Updating diagnosis with ID: {}", id);
        DiagnosisEntity existingEntity = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DiagnosisEntity", id));
        
        mapper.updateFromUpdateDto(updateDto, existingEntity);
        existingEntity = diagnosisRepository.save(existingEntity);
        DiagnosisDto dto = mapper.toDto(existingEntity);
        log.debug("Updated diagnosis with ID: {}", id);
        return dto;
    }

    @Override
    public List<DiagnosisDto> findByClinicCaseId(Long caseId) {
        log.debug("Finding diagnoses for clinic case ID: {}", caseId);
        List<DiagnosisEntity> entities = diagnosisRepository.findByClinicCaseIdAndActiveTrue(caseId);
        return entities.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}


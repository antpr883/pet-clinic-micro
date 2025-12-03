package com.micro.clinic.service.procedure;

import com.micro.clinic.data.dto.procedure.ProcedureCreateDto;
import com.micro.clinic.data.dto.procedure.ProcedureDto;
import com.micro.clinic.data.dto.procedure.ProcedureStatusUpdateDto;
import com.micro.clinic.data.dto.mapper.ProcedureMapper;
import com.micro.clinic.data.entities.ClinicCaseEntity;
import com.micro.clinic.data.entities.ProcedureEntity;
import com.micro.clinic.data.entities.enums.ProcedureStatus;
import com.micro.clinic.exception.ResourceNotFoundException;
import com.micro.clinic.repository.ClinicCaseRepository;
import com.micro.clinic.repository.ProcedureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing procedures.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProcedureServiceImpl implements ProcedureService {

    private final ProcedureRepository procedureRepository;
    private final ClinicCaseRepository caseRepository;
    private final ProcedureMapper mapper;

    @Override
    @Transactional
    public ProcedureDto create(ProcedureCreateDto createDto) {
        log.debug("Creating new procedure for case ID: {}", createDto.getClinicCaseId());
        
        // Validate clinic case exists
        ClinicCaseEntity clinicCase = caseRepository.findById(createDto.getClinicCaseId())
                .orElseThrow(() -> new ResourceNotFoundException("ClinicCaseEntity", createDto.getClinicCaseId()));
        
        ProcedureEntity entity = mapper.toEntity(createDto);
        entity.setClinicCase(clinicCase);
        
        entity = procedureRepository.save(entity);
        ProcedureDto dto = mapper.toDto(entity);
        log.debug("Created procedure with ID: {}", entity.getId());
        return dto;
    }

    @Override
    @Transactional
    public ProcedureDto updateStatus(Long id, ProcedureStatusUpdateDto updateDto) {
        log.debug("Updating procedure status with ID: {}", id);
        ProcedureEntity existingEntity = procedureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProcedureEntity", id));
        
        existingEntity.setStatus(updateDto.getStatus());
        
        // Set completedDate if status is COMPLETED
        if (updateDto.getStatus() == ProcedureStatus.COMPLETED) {
            existingEntity.setCompletedDate(LocalDateTime.now());
        }
        
        existingEntity = procedureRepository.save(existingEntity);
        ProcedureDto dto = mapper.toDto(existingEntity);
        log.debug("Updated procedure status with ID: {}", id);
        return dto;
    }

    @Override
    public List<ProcedureDto> findByClinicCaseId(Long caseId) {
        log.debug("Finding procedures for clinic case ID: {}", caseId);
        List<ProcedureEntity> entities = procedureRepository.findByClinicCaseIdAndActiveTrue(caseId);
        return entities.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}


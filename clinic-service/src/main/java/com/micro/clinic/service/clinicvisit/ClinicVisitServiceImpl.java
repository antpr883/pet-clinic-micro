package com.micro.clinic.service.clinicvisit;

import com.micro.clinic.data.dto.clinicvisit.ClinicVisitCreateDto;
import com.micro.clinic.data.dto.clinicvisit.ClinicVisitDto;
import com.micro.clinic.data.dto.mapper.ClinicVisitMapper;
import com.micro.clinic.data.entities.ClinicCaseEntity;
import com.micro.clinic.data.entities.ClinicVisitEntity;
import com.micro.clinic.exception.ResourceNotFoundException;
import com.micro.clinic.repository.ClinicCaseRepository;
import com.micro.clinic.repository.ClinicVisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing clinic visits.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClinicVisitServiceImpl implements ClinicVisitService {

    private final ClinicVisitRepository visitRepository;
    private final ClinicCaseRepository caseRepository;
    private final ClinicVisitMapper mapper;

    @Override
    @Transactional
    public ClinicVisitDto create(ClinicVisitCreateDto createDto) {
        log.debug("Creating new clinic visit for case ID: {}", createDto.getClinicCaseId());
        
        // Validate clinic case exists
        ClinicCaseEntity clinicCase = caseRepository.findById(createDto.getClinicCaseId())
                .orElseThrow(() -> new ResourceNotFoundException("ClinicCaseEntity", createDto.getClinicCaseId()));
        
        ClinicVisitEntity entity = mapper.toEntity(createDto);
        entity.setClinicCase(clinicCase);
        
        entity = visitRepository.save(entity);
        ClinicVisitDto dto = mapper.toDto(entity);
        log.debug("Created clinic visit with ID: {}", entity.getId());
        return dto;
    }

    @Override
    public List<ClinicVisitDto> findByClinicCaseId(Long caseId) {
        log.debug("Finding visits for clinic case ID: {}", caseId);
        List<ClinicVisitEntity> entities = visitRepository.findByClinicCaseIdAndActiveTrue(caseId);
        return entities.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}


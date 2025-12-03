package com.micro.clinic.service.dailcheck;

import com.micro.clinic.data.dto.dailycheck.DailyCheckCreateDto;
import com.micro.clinic.data.dto.dailycheck.DailyCheckDto;
import com.micro.clinic.data.dto.mapper.DailyCheckMapper;
import com.micro.clinic.data.entities.DailyCheckEntity;
import com.micro.clinic.data.entities.HospitalizationEntity;
import com.micro.clinic.exception.ResourceNotFoundException;
import com.micro.clinic.repository.DailyCheckRepository;
import com.micro.clinic.repository.HospitalizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing daily checks.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyCheckServiceImpl implements DailyCheckService {

    private final DailyCheckRepository dailyCheckRepository;
    private final HospitalizationRepository hospitalizationRepository;
    private final DailyCheckMapper mapper;

    @Override
    @Transactional
    public DailyCheckDto create(DailyCheckCreateDto createDto) {
        log.debug("Creating new daily check for hospitalization ID: {}", createDto.getHospitalizationId());
        
        // Validate hospitalization exists
        HospitalizationEntity hospitalization = hospitalizationRepository.findById(createDto.getHospitalizationId())
                .orElseThrow(() -> new ResourceNotFoundException("HospitalizationEntity", createDto.getHospitalizationId()));
        
        DailyCheckEntity entity = mapper.toEntity(createDto);
        entity.setHospitalization(hospitalization);
        
        entity = dailyCheckRepository.save(entity);
        DailyCheckDto dto = mapper.toDto(entity);
        log.debug("Created daily check with ID: {}", entity.getId());
        return dto;
    }

    @Override
    public List<DailyCheckDto> findByHospitalizationId(Long hospitalizationId) {
        log.debug("Finding daily checks for hospitalization ID: {}", hospitalizationId);
        List<DailyCheckEntity> entities = dailyCheckRepository.findByHospitalizationIdAndActiveTrue(hospitalizationId);
        return entities.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}


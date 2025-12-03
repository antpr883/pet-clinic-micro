package com.micro.clinic.data.dto.hospitalization;

import com.micro.clinic.data.dto.base.BaseDto;
import com.micro.clinic.data.dto.dailycheck.DailyCheckDto;
import com.micro.clinic.data.entities.enums.HospitalizationStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for hospitalization.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class HospitalizationDto extends BaseDto {
    
    private String ward;
    
    private String bed;
    
    private LocalDateTime startAt;
    
    private LocalDateTime endAt;
    
    private HospitalizationStatus status;
    
    private String notes;
    
    private Long clinicCaseId;
    
    // Relationships
    private List<DailyCheckDto> dailyChecks;
}


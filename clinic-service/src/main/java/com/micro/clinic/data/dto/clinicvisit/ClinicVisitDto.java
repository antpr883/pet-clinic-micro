package com.micro.clinic.data.dto.clinicvisit;

import com.micro.clinic.data.dto.base.BaseDto;
import com.micro.clinic.data.entities.enums.VisitType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * DTO for clinic visit.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class ClinicVisitDto extends BaseDto {
    
    private VisitType visitType;
    
    private LocalDateTime visitDate;
    
    private Long vetId;
    
    private String notes;
    
    private Double temperature;
    
    private Double weight;
    
    private String generalCondition;
    
    private Long clinicCaseId;
}


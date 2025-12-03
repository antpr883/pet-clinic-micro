package com.micro.clinic.data.dto.diagnosis;

import com.micro.clinic.data.dto.base.BaseDto;
import com.micro.clinic.data.entities.enums.DiagnosisStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for diagnosis.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class DiagnosisDto extends BaseDto {
    
    private String diagnosisCode;
    
    private String description;
    
    private DiagnosisStatus status;
    
    private Long diagnosedByVetId;
    
    private String notes;
    
    private Long clinicCaseId;
}


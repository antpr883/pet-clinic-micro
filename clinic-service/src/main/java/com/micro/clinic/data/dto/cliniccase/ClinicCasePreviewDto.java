package com.micro.clinic.data.dto.cliniccase;

import com.micro.clinic.data.dto.base.BasePreviewDto;
import com.micro.clinic.data.entities.enums.CasePriority;
import com.micro.clinic.data.entities.enums.ClinicCaseStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Short clinic case view for lists and tables.
 * Used for GET /api/v1/clinic/cases/{id}/preview
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class ClinicCasePreviewDto extends BasePreviewDto {
    
    private Long petId;
    
    private Long ownerId;
    
    private ClinicCaseStatus status;
    
    private CasePriority priority;
    
    private String reason;
}


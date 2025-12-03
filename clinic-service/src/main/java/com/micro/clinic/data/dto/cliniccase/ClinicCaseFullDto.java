package com.micro.clinic.data.dto.cliniccase;

import com.micro.clinic.data.dto.base.BaseDto;
import com.micro.clinic.data.dto.clinicvisit.ClinicVisitDto;
import com.micro.clinic.data.dto.diagnosis.DiagnosisDto;
import com.micro.clinic.data.dto.hospitalization.HospitalizationDto;
import com.micro.clinic.data.dto.procedure.ProcedureDto;
import com.micro.clinic.data.entities.enums.CasePriority;
import com.micro.clinic.data.entities.enums.ClinicCaseStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Full clinic case information with all relationships.
 * Used for GET /api/v1/clinic/cases/{id}
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class ClinicCaseFullDto extends BaseDto {
    
    private Long petId;
    
    private Long ownerId;
    
    private Long clinicId;
    
    private ClinicCaseStatus status;
    
    private CasePriority priority;
    
    private String reason;
    
    private Long assignedVetId;
    
    private String notes;
    
    private Long version; // For optimistic locking
    
    // Relationships
    private List<ClinicVisitDto> visits;
    
    private List<DiagnosisDto> diagnoses;
    
    private List<ProcedureDto> procedures;
    
    private HospitalizationDto hospitalization;
}


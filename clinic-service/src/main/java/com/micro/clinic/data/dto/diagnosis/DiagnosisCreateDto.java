package com.micro.clinic.data.dto.diagnosis;

import com.micro.clinic.data.dto.base.DtoMarker;
import com.micro.clinic.data.entities.enums.DiagnosisStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for creating a new diagnosis.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DiagnosisCreateDto extends DtoMarker {
    
    @NotNull(message = "Clinic case ID is required")
    private Long clinicCaseId;
    
    @NotBlank(message = "Diagnosis code is required")
    @Size(max = 100, message = "Diagnosis code must not exceed 100 characters")
    private String diagnosisCode;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    private DiagnosisStatus status;
    
    @NotNull(message = "Vet ID is required")
    private Long diagnosedByVetId;
    
    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
}


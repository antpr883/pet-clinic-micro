package com.micro.clinic.data.dto.diagnosis;

import com.micro.clinic.data.entities.enums.DiagnosisStatus;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for updating diagnosis (all fields optional).
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class DiagnosisUpdateDto {
    
    @Size(max = 100, message = "Diagnosis code must not exceed 100 characters")
    private String diagnosisCode;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    private DiagnosisStatus status;
    
    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
}


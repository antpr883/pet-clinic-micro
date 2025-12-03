package com.micro.clinic.data.dto.cliniccase;

import com.micro.clinic.data.entities.enums.CasePriority;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for updating clinic case (all fields optional).
 * Used for PUT /api/v1/clinic/cases/{id}
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ClinicCaseUpdateDto {
    
    private Long petId;
    
    private Long ownerId;
    
    private Long clinicId;
    
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;
    
    private CasePriority priority;
    
    private Long assignedVetId;
    
    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
}


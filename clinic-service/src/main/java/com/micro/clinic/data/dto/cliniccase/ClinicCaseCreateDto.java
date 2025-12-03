package com.micro.clinic.data.dto.cliniccase;

import com.micro.clinic.data.dto.base.DtoMarker;
import com.micro.clinic.data.entities.enums.CasePriority;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for creating a new clinic case.
 * Used for POST /api/v1/clinic/cases
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClinicCaseCreateDto extends DtoMarker {
    
    @NotNull(message = "Pet ID is required")
    private Long petId;
    
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    
    private Long clinicId;
    
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;
    
    private CasePriority priority;
    
    private Long assignedVetId;
}


package com.micro.clinic.data.dto.clinicvisit;

import com.micro.clinic.data.dto.base.DtoMarker;
import com.micro.clinic.data.entities.enums.VisitType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * DTO for creating a new clinic visit.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClinicVisitCreateDto extends DtoMarker {
    
    @NotNull(message = "Clinic case ID is required")
    private Long clinicCaseId;
    
    @NotNull(message = "Visit type is required")
    private VisitType visitType;
    
    @NotNull(message = "Visit date is required")
    private LocalDateTime visitDate;
    
    @NotNull(message = "Vet ID is required")
    private Long vetId;
    
    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
    
    private Double temperature;
    
    private Double weight;
    
    @Size(max = 500, message = "General condition must not exceed 500 characters")
    private String generalCondition;
}


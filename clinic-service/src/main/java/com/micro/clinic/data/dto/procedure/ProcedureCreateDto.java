package com.micro.clinic.data.dto.procedure;

import com.micro.clinic.data.dto.base.DtoMarker;
import com.micro.clinic.data.entities.enums.ProcedureStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for creating a new procedure.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProcedureCreateDto extends DtoMarker {
    
    @NotNull(message = "Clinic case ID is required")
    private Long clinicCaseId;
    
    @NotBlank(message = "Procedure name is required")
    @Size(max = 200, message = "Procedure name must not exceed 200 characters")
    private String procedureName;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    private ProcedureStatus status;
    
    private LocalDateTime plannedDate;
    
    private Long performedByVetId;
    
    private BigDecimal cost;
    
    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
}


package com.micro.clinic.data.dto.procedure;

import com.micro.clinic.data.dto.base.BaseDto;
import com.micro.clinic.data.entities.enums.ProcedureStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for procedure.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class ProcedureDto extends BaseDto {
    
    private String procedureName;
    
    private String description;
    
    private ProcedureStatus status;
    
    private LocalDateTime plannedDate;
    
    private LocalDateTime completedDate;
    
    private Long performedByVetId;
    
    private BigDecimal cost;
    
    private String notes;
    
    private Long clinicCaseId;
}


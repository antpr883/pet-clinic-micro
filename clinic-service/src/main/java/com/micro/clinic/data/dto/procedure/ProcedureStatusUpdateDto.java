package com.micro.clinic.data.dto.procedure;

import com.micro.clinic.data.entities.enums.ProcedureStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for updating procedure status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProcedureStatusUpdateDto {
    
    @NotNull(message = "Status is required")
    private ProcedureStatus status;
}


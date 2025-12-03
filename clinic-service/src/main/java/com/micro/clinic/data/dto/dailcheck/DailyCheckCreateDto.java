package com.micro.clinic.data.dto.dailcheck;

import com.fasterxml.jackson.databind.JsonNode;
import com.micro.clinic.data.dto.base.DtoMarker;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * DTO for creating a new daily check.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DailyCheckCreateDto extends DtoMarker {
    
    @NotNull(message = "Hospitalization ID is required")
    private Long hospitalizationId;
    
    @NotNull(message = "Check date is required")
    private LocalDateTime checkDate;
    
    @NotNull(message = "Vet ID is required")
    private Long checkedByVetId;
    
    /**
     * JSONB data containing:
     * - weight
     * - temperature
     * - state
     * - behavior
     * - appetite
     * - notes
     */
    @NotNull(message = "Check data is required")
    private JsonNode checkData;
}


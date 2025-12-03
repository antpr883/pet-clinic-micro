package com.micro.clinic.data.dto.dailcheck;

import com.fasterxml.jackson.databind.JsonNode;
import com.micro.clinic.data.dto.base.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * DTO for daily check.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class DailyCheckDto extends BaseDto {
    
    private LocalDateTime checkDate;
    
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
    private JsonNode checkData;
    
    private Long hospitalizationId;
}


package com.micro.clinic.data.dto.hospitalization;

import com.micro.clinic.data.dto.base.DtoMarker;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * DTO for creating a new hospitalization.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HospitalizationCreateDto extends DtoMarker {
    
    @NotNull(message = "Clinic case ID is required")
    private Long clinicCaseId;
    
    @Size(max = 50, message = "Ward must not exceed 50 characters")
    private String ward;
    
    @Size(max = 50, message = "Bed must not exceed 50 characters")
    private String bed;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startAt;
    
    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
}


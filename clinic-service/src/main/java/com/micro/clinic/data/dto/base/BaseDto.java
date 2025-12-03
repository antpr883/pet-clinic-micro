package com.micro.clinic.data.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseDto extends DtoMarker {
    
    private Boolean active;
    
    private String createdBy;
    
    private String modifiedBy;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}


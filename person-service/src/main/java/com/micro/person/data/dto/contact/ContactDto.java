package com.micro.person.data.dto.contact;

import com.micro.person.data.dto.base.BaseDto;
import com.micro.person.data.entities.enums.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class ContactDto extends BaseDto {
    
    @NotNull(message = "Contact type is required")
    private ContactType contactType;
    
    @NotBlank(message = "Contact value is required")
    private String value;
    
    @Size(max = 100, message = "Label must not exceed 100 characters")
    private String label;
    
    @NotNull(message = "Primary flag is required")
    private Boolean isPrimary;
}


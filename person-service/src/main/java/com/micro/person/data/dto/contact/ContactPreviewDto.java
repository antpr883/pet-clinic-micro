package com.micro.person.data.dto.contact;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.micro.person.data.dto.base.BasePreviewDto;
import com.micro.person.data.entities.enums.ContactType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactPreviewDto extends BasePreviewDto {
    
    private ContactType contactType;
    
    private String value;
    
    private String label;
    
    private Boolean isPrimary;
}


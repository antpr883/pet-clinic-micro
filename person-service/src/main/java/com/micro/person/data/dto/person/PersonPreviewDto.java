package com.micro.person.data.dto.person;

import com.micro.person.data.dto.base.BasePreviewDto;
import com.micro.person.data.entities.enums.PersonRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class PersonPreviewDto extends BasePreviewDto {
    
    private String firstName;
    
    private String lastName;
    
    private LocalDate dateOfBirth;
    
    private PersonRole role;
    
    private String email;
    
    private String phone;
}


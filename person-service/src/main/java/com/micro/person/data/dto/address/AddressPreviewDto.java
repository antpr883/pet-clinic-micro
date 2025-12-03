package com.micro.person.data.dto.address;

import com.micro.person.data.dto.base.BasePreviewDto;
import com.micro.person.data.entities.enums.AddressType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class AddressPreviewDto extends BasePreviewDto {
    
    private AddressType addressType;
    
    private String country;
    
    private String city;
    
    private String street;
    
    private String building;
    
    private String apartment;
    
    private String postalCode;
    
    private String description;
}


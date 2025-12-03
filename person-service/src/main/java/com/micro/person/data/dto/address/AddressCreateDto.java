package com.micro.person.data.dto.address;

import com.micro.person.data.entities.enums.AddressType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO для створення нової адреси.
 * Використовується для POST /api/v1/persons/{personId}/addresses
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AddressCreateDto {
    
    @NotNull(message = "Address type is required")
    private AddressType addressType;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Size(max = 255, message = "Street must not exceed 255 characters")
    private String street;
    
    @Size(max = 50, message = "Building must not exceed 50 characters")
    private String building;
    
    @Size(max = 50, message = "Apartment must not exceed 50 characters")
    private String apartment;
    
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}


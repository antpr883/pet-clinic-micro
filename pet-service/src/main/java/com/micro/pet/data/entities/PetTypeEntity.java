package com.micro.pet.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entity for pet types (cat, dog, exotic, etc.).
 * 
 * Table: pet_schema.pet_types
 */
@Entity
@Table(name = "pet_types", schema = "pet_schema")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class PetTypeEntity extends BaseActiveEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "type_name", nullable = false, unique = true, length = 50)
    private String typeName; // DOG, CAT, PARROT, IGUANA

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @Builder.Default
    @Column(name = "exotic", nullable = false)
    private Boolean exotic = false;
}


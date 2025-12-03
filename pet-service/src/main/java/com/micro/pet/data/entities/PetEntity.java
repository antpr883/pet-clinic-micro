package com.micro.pet.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Main entity for pets.
 * 
 * Table: pet_schema.pets
 * 
 * Relationships:
 * - ownerId (Long) - reference to PersonEntity.id from Person Service (via API, no FK)
 * - type (PetTypeEntity) - pet type (Many-to-One)
 * - info (PetInfoEntity) - detailed information in JSONB (One-to-One)
 */
@Entity
@Table(name = "pets", schema = "pet_schema")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"type", "info"})
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class PetEntity extends BaseActiveEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Past
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * Owner (PersonEntity.id from Person Service).
     * Stored as Long, without FK (microservice architecture).
     */
    @NotNull
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    /**
     * Pet type (cat, dog, etc.)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private PetTypeEntity type;

    /**
     * Detailed information in JSON (via PetInfoEntity)
     */
    @OneToOne(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PetInfoEntity info;

    /**
     * Photo (S3 or URL)
     */
    @Size(max = 255)
    @Column(name = "photo_url", length = 255)
    private String photoUrl;
}


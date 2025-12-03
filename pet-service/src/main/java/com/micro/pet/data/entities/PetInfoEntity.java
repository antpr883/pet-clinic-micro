package com.micro.pet.data.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

/**
 * Entity for storing detailed pet information in JSONB format.
 * 
 * Table: pet_schema.pet_info
 * 
 * Uses hibernate-types-60 for JsonNode → jsonb support.
 * JSONB allows storing arbitrary characteristics and searching through them.
 */
@Entity
@Table(name = "pet_info", schema = "pet_schema")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"pet"})
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class PetInfoEntity extends BaseActiveEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false, unique = true)
    private PetEntity pet;

    /**
     * JSONB: stores arbitrary characteristics.
     * Type - JsonNode, for convenient RSQL search and Jackson serialization.
     * 
     * Example:
     * {
     *   "weight": 6.4,
     *   "breed": "Scottish Fold",
     *   "favoriteFood": "chicken",
     *   "notes": "Requires walks 2 times a day"
     * }
     */
    @Type(JsonType.class)
    @Column(name = "details", columnDefinition = "jsonb", nullable = false)
    @NotNull
    private JsonNode details;
}


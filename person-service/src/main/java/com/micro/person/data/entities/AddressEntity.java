package com.micro.person.data.entities;

import com.micro.person.data.entities.enums.AddressType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Address entity - окрема таблиця для адрес персон.
 * Одна персона може мати багато адрес різних типів.
 * 
 * Приклади:
 * - HOME: вул. Хрещатик, 1, кв. 5, м. Київ, Україна
 * - WORK: вул. Банкова, 10, офіс 201, м. Київ, Україна
 * - TEMPORARY: вул. Лісова, 20, м. Львів, Україна (для тимчасового перебування)
 */
@Entity
@Table(name = "addresses", schema = "person_schema", indexes = {
    @Index(name = "idx_address_person_id", columnList = "person_id"),
    @Index(name = "idx_address_type", columnList = "address_type"),
    @Index(name = "idx_address_city", columnList = "city"),
    @Index(name = "idx_address_country", columnList = "country")
})
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = "person")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class AddressEntity extends BaseActiveEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false, foreignKey = @ForeignKey(name = "fk_address_person"))
    private PersonEntity person;

    @NotNull(message = "Address type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false, length = 30)
    private AddressType addressType;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank(message = "Street is required")
    @Size(max = 255, message = "Street must not exceed 255 characters")
    @Column(name = "street", nullable = false, length = 255)
    private String street;

    @Size(max = 50, message = "Building must not exceed 50 characters")
    @Column(name = "building", length = 50)
    private String building;

    @Size(max = 50, message = "Apartment must not exceed 50 characters")
    @Column(name = "apartment", length = 50)
    private String apartment;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description; // наприклад "робочий офіс №2", "дача"
}


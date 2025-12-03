package com.micro.clinic.data.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

/**
 * Щоденний огляд під час госпіталізації.
 * 
 * Використовує JSONB для гнучкого зберігання даних:
 * - weight (вага)
 * - temperature (температура)
 * - state (стан)
 * - behavior (поведінка)
 * - appetite (апетит)
 * - notes (нотатки)
 * 
 * JSONB дозволяє додавати нові поля без зміни схеми БД.
 */
@Entity
@Table(name = "daily_checks", schema = "clinic_schema")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = "hospitalization")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class DailyCheckEntity extends BaseActiveEntity {

    /**
     * Дата та час огляду.
     */
    @Column(name = "check_date", nullable = false)
    private LocalDateTime checkDate;

    /**
     * ID лікаря, який провів огляд (з Person Service).
     */
    @Column(name = "checked_by_vet_id", nullable = false)
    private Long checkedByVetId;

    /**
     * JSONB: зберігає дані огляду.
     * 
     * Приклад структури:
     * {
     *   "weight": 6.4,
     *   "temperature": 38.5,
     *   "state": "Stable",
     *   "behavior": "Active",
     *   "appetite": "Good",
     *   "notes": "Patient is recovering well"
     * }
     * 
     * Використовує hibernate-types-60 для JsonNode → jsonb підтримки.
     */
    @Type(JsonType.class)
    @Column(name = "check_data", columnDefinition = "jsonb", nullable = false)
    @NotNull
    private JsonNode checkData;

    // ========== Relationships ==========

    /**
     * Госпіталізація, до якої належить огляд.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospitalization_id", nullable = false, foreignKey = @ForeignKey(name = "fk_daily_check_hospitalization"))
    private HospitalizationEntity hospitalization;
}


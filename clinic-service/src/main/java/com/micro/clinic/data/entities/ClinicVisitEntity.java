package com.micro.clinic.data.entities;

import com.micro.clinic.data.entities.enums.VisitType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Візит у рамках клінічного кейсу.
 * 
 * Може бути:
 * - Первинний огляд (INITIAL)
 * - Повторний огляд (FOLLOW_UP)
 * - Консультація (CONSULTATION)
 * - Екстрений випадок (EMERGENCY)
 */
@Entity
@Table(name = "clinic_visits", schema = "clinic_schema")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = "clinicCase")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ClinicVisitEntity extends BaseActiveEntity {

    /**
     * Тип візиту.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "visit_type", nullable = false, length = 20)
    private VisitType visitType;

    /**
     * Дата та час візиту.
     */
    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;

    /**
     * ID лікаря, який провів огляд (з Person Service).
     */
    @Column(name = "vet_id", nullable = false)
    private Long vetId;

    /**
     * Нотатки з огляду.
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Температура тварини.
     */
    @Column(name = "temperature")
    private Double temperature;

    /**
     * Вага тварини.
     */
    @Column(name = "weight")
    private Double weight;

    /**
     * Загальний стан тварини.
     */
    @Column(name = "general_condition", length = 500)
    private String generalCondition;

    // ========== Relationships ==========

    /**
     * Клінічний кейс, до якого належить візит.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_case_id", nullable = false, foreignKey = @ForeignKey(name = "fk_clinic_visit_case"))
    private ClinicCaseEntity clinicCase;
}


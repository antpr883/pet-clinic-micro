package com.micro.clinic.data.entities;

import com.micro.clinic.data.entities.enums.DiagnosisStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Діагноз у рамках клінічного кейсу.
 * 
 * Може бути кілька діагнозів для одного кейсу.
 */
@Entity
@Table(name = "diagnoses", schema = "clinic_schema")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = "clinicCase")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class DiagnosisEntity extends BaseActiveEntity {

    /**
     * Назва діагнозу (ICD-10 код або опис).
     */
    @Column(name = "diagnosis_code", nullable = false, length = 100)
    private String diagnosisCode;

    /**
     * Опис діагнозу.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Статус діагнозу.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private DiagnosisStatus status = DiagnosisStatus.PRELIMINARY;

    /**
     * ID лікаря, який встановив діагноз (з Person Service).
     */
    @Column(name = "diagnosed_by_vet_id", nullable = false)
    private Long diagnosedByVetId;

    /**
     * Додаткові нотатки.
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Relationships ==========

    /**
     * Клінічний кейс, до якого належить діагноз.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_case_id", nullable = false, foreignKey = @ForeignKey(name = "fk_diagnosis_case"))
    private ClinicCaseEntity clinicCase;
}


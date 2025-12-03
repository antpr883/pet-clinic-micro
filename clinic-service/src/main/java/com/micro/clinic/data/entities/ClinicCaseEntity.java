package com.micro.clinic.data.entities;

import com.micro.clinic.data.entities.enums.CasePriority;
import com.micro.clinic.data.entities.enums.ClinicCaseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Головна сутність клінічного кейсу (aggregate root).
 * 
 * Керує повним життєвим циклом лікування тварини:
 * - Реєстрація
 * - Первинний огляд
 * - Діагностика
 * - План лікування
 * - Процедури
 * - Госпіталізація
 * - Виписка
 */
@Entity
@Table(name = "clinic_cases", schema = "clinic_schema")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"visits", "diagnoses", "procedures", "hospitalization"})
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ClinicCaseEntity extends BaseActiveEntity {

    /**
     * Version field for optimistic locking.
     * Захищає від concurrent modifications.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * ID тварини (з Pet Service).
     * Не FK, оскільки це мікросервісна архітектура.
     */
    @Column(name = "pet_id", nullable = false)
    private Long petId;

    /**
     * ID власника (з Person Service).
     * Не FK, оскільки це мікросервісна архітектура.
     */
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    /**
     * ID філії клініки (опційно).
     */
    @Column(name = "clinic_id")
    private Long clinicId;

    /**
     * Статус кейсу (State Machine).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private ClinicCaseStatus status = ClinicCaseStatus.REGISTERED;

    /**
     * Пріоритет кейсу.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    @Builder.Default
    private CasePriority priority = CasePriority.NORMAL;

    /**
     * Причина звернення.
     */
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    /**
     * ID призначеного лікаря (з Person Service).
     */
    @Column(name = "assigned_vet_id")
    private Long assignedVetId;

    /**
     * Загальні нотатки.
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Relationships ==========

    /**
     * Візити в рамках кейсу.
     */
    @OneToMany(mappedBy = "clinicCase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClinicVisitEntity> visits = new ArrayList<>();

    /**
     * Діагнози.
     */
    @OneToMany(mappedBy = "clinicCase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DiagnosisEntity> diagnoses = new ArrayList<>();

    /**
     * Процедури.
     */
    @OneToMany(mappedBy = "clinicCase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProcedureEntity> procedures = new ArrayList<>();

    /**
     * Госпіталізація (OneToOne).
     */
    @OneToOne(mappedBy = "clinicCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private HospitalizationEntity hospitalization;
}


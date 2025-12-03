package com.micro.clinic.data.entities;

import com.micro.clinic.data.entities.enums.ProcedureStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Медична процедура у рамках клінічного кейсу.
 * 
 * Може бути:
 * - Операція
 * - Ін'єкція
 * - Маніпуляція
 * - Курс терапії
 */
@Entity
@Table(name = "procedures", schema = "clinic_schema")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = "clinicCase")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ProcedureEntity extends BaseActiveEntity {

    /**
     * Назва процедури.
     */
    @Column(name = "procedure_name", nullable = false, length = 200)
    private String procedureName;

    /**
     * Опис процедури.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Статус процедури.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ProcedureStatus status = ProcedureStatus.PLANNED;

    /**
     * Запланована дата та час процедури.
     */
    @Column(name = "planned_date")
    private LocalDateTime plannedDate;

    /**
     * Фактична дата та час виконання процедури.
     */
    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    /**
     * ID лікаря, який виконує процедуру (з Person Service).
     */
    @Column(name = "performed_by_vet_id")
    private Long performedByVetId;

    /**
     * Вартість процедури.
     */
    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    /**
     * Додаткові нотатки.
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Relationships ==========

    /**
     * Клінічний кейс, до якого належить процедура.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_case_id", nullable = false, foreignKey = @ForeignKey(name = "fk_procedure_case"))
    private ClinicCaseEntity clinicCase;
}


package com.micro.clinic.data.entities;

import com.micro.clinic.data.entities.enums.HospitalizationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Госпіталізація тварини (стаціонар).
 * 
 * OneToOne з ClinicCaseEntity.
 * Містить щоденні огляди (DailyCheckEntity).
 */
@Entity
@Table(name = "hospitalizations", schema = "clinic_schema")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"clinicCase", "dailyChecks"})
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class HospitalizationEntity extends BaseActiveEntity {

    /**
     * Номер палати.
     */
    @Column(name = "ward", length = 50)
    private String ward;

    /**
     * Номер ліжка.
     */
    @Column(name = "bed", length = 50)
    private String bed;

    /**
     * Дата початку госпіталізації.
     */
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    /**
     * Дата завершення госпіталізації (виписки).
     */
    @Column(name = "end_at")
    private LocalDateTime endAt;

    /**
     * Статус госпіталізації.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private HospitalizationStatus status = HospitalizationStatus.ACTIVE;

    /**
     * Додаткові нотатки.
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Relationships ==========

    /**
     * Клінічний кейс, до якого належить госпіталізація.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_case_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_hospitalization_case"))
    private ClinicCaseEntity clinicCase;

    /**
     * Щоденні огляди під час госпіталізації.
     */
    @OneToMany(mappedBy = "hospitalization", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DailyCheckEntity> dailyChecks = new ArrayList<>();
}


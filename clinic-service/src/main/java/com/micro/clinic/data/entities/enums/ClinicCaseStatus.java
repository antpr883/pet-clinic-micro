package com.micro.clinic.data.entities.enums;

/**
 * Статуси клінічного кейсу (State Machine).
 */
public enum ClinicCaseStatus {
    /**
     * Кейс зареєстровано, очікує первинного огляду.
     */
    REGISTERED,
    
    /**
     * Первинний огляд завершено.
     */
    INITIAL_CHECKUP_COMPLETED,
    
    /**
     * Діагностика в процесі.
     */
    DIAGNOSTICS_IN_PROGRESS,
    
    /**
     * План лікування сформовано.
     */
    TREATMENT_PLANNED,
    
    /**
     * Процедури в процесі.
     */
    PROCEDURES_IN_PROGRESS,
    
    /**
     * Тварина госпіталізована.
     */
    HOSPITALIZED,
    
    /**
     * Тварина виписана.
     */
    DISCHARGED,
    
    /**
     * Кейс скасовано.
     */
    CANCELLED
}


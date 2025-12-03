package com.micro.clinic.data.entities.enums;

/**
 * Статус діагнозу.
 */
public enum DiagnosisStatus {
    /**
     * Попередній діагноз (потребує підтвердження).
     */
    PRELIMINARY,
    
    /**
     * Підтверджений діагноз.
     */
    CONFIRMED,
    
    /**
     * Виключений діагноз.
     */
    RULED_OUT
}


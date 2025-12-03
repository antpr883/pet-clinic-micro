package com.micro.clinic.data.entities.enums;

/**
 * Статус процедури.
 */
public enum ProcedureStatus {
    /**
     * Запланована процедура.
     */
    PLANNED,
    
    /**
     * Процедура в процесі.
     */
    IN_PROGRESS,
    
    /**
     * Процедура завершена.
     */
    COMPLETED,
    
    /**
     * Процедура скасована.
     */
    CANCELLED
}


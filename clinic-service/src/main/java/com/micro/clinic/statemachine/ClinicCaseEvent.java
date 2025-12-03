package com.micro.clinic.statemachine;

/**
 * Events for Clinic Case State Machine.
 * 
 * Events trigger state transitions.
 */
public enum ClinicCaseEvent {
    
    /**
     * Initial checkup completed (requires at least one INITIAL visit).
     */
    INITIAL_CHECKUP_COMPLETED,
    
    /**
     * Start diagnostics (vet decision).
     */
    START_DIAGNOSTICS,
    
    /**
     * Treatment planned (requires at least one confirmed diagnosis).
     */
    TREATMENT_PLANNED,
    
    /**
     * Procedures started (requires at least one planned procedure).
     */
    PROCEDURES_STARTED,
    
    /**
     * Hospitalization started.
     */
    HOSPITALIZATION_STARTED,
    
    /**
     * Discharged (requires hospitalization endAt to be set).
     */
    DISCHARGED,
    
    /**
     * Case cancelled (can be from any state except DISCHARGED).
     */
    CANCELLED
}


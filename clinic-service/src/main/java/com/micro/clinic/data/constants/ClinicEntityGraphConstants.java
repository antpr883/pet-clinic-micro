package com.micro.clinic.data.constants;

/**
 * Constants for Entity Graph attributes in Clinic Service.
 */
public final class ClinicEntityGraphConstants {
    
    public static final String VISITS = "visits";
    public static final String DIAGNOSES = "diagnoses";
    public static final String PROCEDURES = "procedures";
    public static final String HOSPITALIZATION = "hospitalization";
    public static final String DAILY_CHECKS = "dailyChecks";
    
    public static final String[] FULL_GRAPH_ATTRIBUTES = {
            VISITS,
            DIAGNOSES,
            PROCEDURES,
            HOSPITALIZATION
    };
    
    public static final String[] PREVIEW_GRAPH_ATTRIBUTES = {};
    
    private ClinicEntityGraphConstants() {
        // Utility class
    }
}

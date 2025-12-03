package com.micro.pet.data.constants;

/**
 * Constants for PetEntity Entity Graph attributes.
 * Eliminates hardcoded relationship names throughout the codebase.
 */
public final class PetEntityGraphConstants {
    
    private PetEntityGraphConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Entity Graph attribute for type relationship.
     */
    public static final String TYPE = "type";
    
    /**
     * Entity Graph attribute for info relationship.
     */
    public static final String INFO = "info";
    
    /**
     * Array of all Entity Graph attributes for PetEntity FullDto.
     * Used when loading PetEntity with all relationships.
     */
    public static final String[] FULL_GRAPH_ATTRIBUTES = {TYPE, INFO};
    
    /**
     * Array of Entity Graph attributes for PetEntity PreviewDto.
     * Used when loading PetEntity with minimal relationships (only type for preview).
     */
    public static final String[] PREVIEW_GRAPH_ATTRIBUTES = {TYPE};
}


package com.micro.person.data.constants;

/**
 * Constants for PersonEntity Entity Graph attributes.
 * Eliminates hardcoded relationship names throughout the codebase.
 */
public final class PersonEntityGraphConstants {
    
    private PersonEntityGraphConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Entity Graph attribute for contacts relationship.
     */
    public static final String CONTACTS = "contacts";
    
    /**
     * Entity Graph attribute for addresses relationship.
     */
    public static final String ADDRESSES = "addresses";
    
    /**
     * Array of all Entity Graph attributes for PersonEntity FullDto.
     * Used when loading PersonEntity with all relationships.
     */
    public static final String[] FULL_GRAPH_ATTRIBUTES = {CONTACTS, ADDRESSES};
    
    /**
     * Array of Entity Graph attributes for PersonEntity PreviewDto.
     * Used when loading PersonEntity with minimal relationships.
     */
    public static final String[] PREVIEW_GRAPH_ATTRIBUTES = {};
}


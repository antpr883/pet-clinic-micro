package com.micro.person.data.constants;

/**
 * Constants for entity field names, RSQL queries, and JPQL fragments.
 * Eliminates hardcoded strings throughout the codebase.
 */
public final class EntityConstants {
    
    private EntityConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Field name for active status in BaseActiveEntity.
     */
    public static final String ACTIVE_FIELD = "active";
    
    /**
     * RSQL query string for filtering active entities.
     */
    public static final String ACTIVE_RSQL_QUERY = "active==true";
    
    /**
     * RSQL separator for combining conditions.
     */
    public static final String RSQL_SEPARATOR = ";";
    
    /**
     * JPQL fragment for active = true condition.
     * Used in @Query annotations to avoid hardcoding "active = true".
     */
    public static final String JPQL_ACTIVE_TRUE = "active = true";
    
    /**
     * JPQL fragment for active = true condition with entity alias.
     * Example: "e.active = true" or "p.active = true"
     */
    public static final String JPQL_ACTIVE_TRUE_WITH_ALIAS = "%s.active = true";
    
    /**
     * JPQL fragment for AND active = true condition.
     * Used to append active filter to existing queries.
     */
    public static final String JPQL_AND_ACTIVE_TRUE = " AND active = true";
    
    /**
     * JPQL fragment for CASE WHEN active = true condition.
     * Used in boolean queries.
     */
    public static final String JPQL_CASE_WHEN_ACTIVE_TRUE = "active = true";
}


package com.micro.pet.data.constants;

/**
 * Constants for API responses and messages.
 */
public final class ApiConstants {
    
    public static final String RESOURCE_FOUNDED = "Resource founded";
    public static final String RESOURCE_CREATED = "Resource created successfully";
    public static final String RESOURCE_UPDATED = "Resource updated successfully";
    public static final String RESOURCE_DELETED = "Resource deleted successfully";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    
    private ApiConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}


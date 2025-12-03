package com.micro.person.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Error response for validation errors (@Valid).
 * 
 * Contains details about validation errors for each field.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** Error timestamp */
    private LocalDateTime timestamp;
    
    /** HTTP status code */
    private int status;
    
    /** Error type */
    private String error;
    
    /** Error message */
    private String message;
    
    /** Request path that caused the error */
    private String path;
    
    /** Map where key = field name, value = error message */
    private Map<String, String> errors;
    
    /**
     * Factory method for creating ValidationErrorResponse.
     */
    public static ValidationErrorResponse of(Map<String, String> errors, String path) {
        return ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(400)
                .error("Validation Failed")
                .message("Validation failed for one or more fields")
                .path(path)
                .errors(errors)
                .build();
    }
}


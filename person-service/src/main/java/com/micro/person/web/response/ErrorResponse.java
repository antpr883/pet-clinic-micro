package com.micro.person.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Standard error response DTO.
 * 
 * Used in ControllerExceptionHandler for wrapping errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** Error timestamp */
    private LocalDateTime timestamp;
    
    /** HTTP status code */
    private int status;
    
    /** Error type (e.g., "Not Found", "Bad Request") */
    private String error;
    
    /** Error message */
    private String message;
    
    /** Request path that caused the error */
    private String path;
    
    /** Additional details (optional) */
    private String details;
}

package com.micro.person.web.response;

import com.micro.person.data.constants.ApiConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Standard wrapper for successful API responses.
 * 
 * Used in Controller Layer for wrapping DTOs and other objects.
 * Jackson automatically serializes all types, so Serializable constraint is not critical.
 * 
 * @param <P> response content type (DTO, List, Map, etc.)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppResponse<P> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String message;
    private P content;
    private boolean success;

    public static <P> AppResponse<P> successful(P content) {
        return new AppResponse<>(ApiConstants.RESOURCE_FOUNDED, content, true);
    }
}

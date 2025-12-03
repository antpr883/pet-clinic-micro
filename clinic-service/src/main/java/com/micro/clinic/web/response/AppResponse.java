package com.micro.clinic.web.response;

import com.micro.clinic.data.constants.ApiConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Standard wrapper for successful API responses.
 * 
 * Used in Controller Layer for wrapping DTOs and other objects.
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


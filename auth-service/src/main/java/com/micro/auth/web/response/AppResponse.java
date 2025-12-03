package com.micro.auth.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private T content;
    private String message;
    private String error;

    public static <T> AppResponse<T> successful(T content) {
        return AppResponse.<T>builder()
                .success(true)
                .content(content)
                .build();
    }

    public static <T> AppResponse<T> error(String message) {
        return AppResponse.<T>builder()
                .success(false)
                .error(message)
                .build();
    }
}


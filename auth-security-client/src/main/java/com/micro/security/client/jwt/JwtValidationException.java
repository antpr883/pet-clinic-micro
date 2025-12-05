package com.micro.security.client.jwt;

/**
 * Виняток для помилок валідації JWT токенів.
 * 
 * Викидається при:
 * - Невалідному підписі токена
 * - Неправильному issuer
 * - Неправильному типі токена
 * - Простроченому токені
 */
public class JwtValidationException extends RuntimeException {
    
    public JwtValidationException(String message) {
        super(message);
    }

    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}


package com.micro.security.client.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Валідатор JWT токенів для мікросервісів.
 * 
 * Використовується в Person, Pet, Clinic Services для валідації токенів від Auth Service.
 * Перевіряє підпис, issuer, тип токена та термін дії.
 * 
 * BEST PRACTICE: Використовується через Spring Boot Starter auto-configuration.
 * Не потрібно створювати вручну - автоматично налаштовується при підключенні auth-security-client.
 */
@Slf4j
public class JwtTokenValidator {

    private final SecretKey secretKey;
    private final String issuer;

    public JwtTokenValidator(
            @Value("${jwt.signing.secret}") String secret,
            @Value("${jwt.issuer:pet-clinic-auth}") String issuer) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
    }

    /**
     * Парсить та валідує JWT токен.
     * 
     * Перевіряє:
     * - Підпис токена (використовується secret key)
     * - Issuer (має відповідати налаштованому issuer)
     * - Тип токена (має бути "access")
     * 
     * @param token JWT токен з Authorization header
     * @return Claims з токена
     * @throws JwtValidationException якщо токен невалідний
     */
    public Claims parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Validate issuer
            if (!issuer.equals(claims.getIssuer())) {
                throw new JwtValidationException("Invalid token issuer");
            }

            // Validate token type (must be access token)
            String type = claims.get("type", String.class);
            if (!"access".equals(type)) {
                throw new JwtValidationException("Invalid token type, expected 'access'");
            }

            return claims;
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            throw new JwtValidationException("Invalid JWT token: " + e.getMessage(), e);
        }
    }

    public Long getUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    public String getUsername(Claims claims) {
        return claims.get("username", String.class);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRoles(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        return roles != null ? new HashSet<>(roles) : Set.of();
    }

    @SuppressWarnings("unchecked")
    public Set<String> getPermissions(Claims claims) {
        List<String> permissions = claims.get("permissions", List.class);
        return permissions != null ? new HashSet<>(permissions) : Set.of();
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}


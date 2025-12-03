package com.micro.auth.client.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Валідатор JWT токенів для інших мікросервісів.
 * 
 * Використовується в Person, Pet, Clinic Services для валідації токенів від Auth Service.
 * Не використовує Apache Shiro - тільки Spring Security.
 * 
 * Цей компонент можна використовувати в інших сервісах, додавши auth-service як залежність
 * або скопіювавши цей клас в інші сервіси.
 */
/**
 * ⚠️ ВАЖЛИВО: Цей клас НЕ використовується в auth-service!
 * 
 * Він призначений ТІЛЬКИ для інших мікросервісів (Person, Pet, Clinic).
 * 
 * В auth-service використовується JwtTokenService для генерації токенів.
 */
// @Component  // ЗАКОМЕНТОВАНО - не активувати в auth-service!
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


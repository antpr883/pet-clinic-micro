package com.micro.auth.service.jwt;

import com.micro.auth.data.entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtTokenService {

    private final SecretKey secretKey;
    private final String issuer;
    private final Long accessTokenExpirationSeconds;
    private final Long refreshTokenExpirationSeconds;

    public JwtTokenService(
            @Value("${jwt.signing.secret}") String secret,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.access-token.expiration-seconds}") Long accessTokenExpirationSeconds,
            @Value("${jwt.refresh-token.expiration-seconds}") Long refreshTokenExpirationSeconds) {
        
        // Validate secret key length (HS256 requires at least 256 bits = 32 bytes)
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 32 bytes (256 bits) for HS256. Current length: " + secretBytes.length);
        }
        
        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
        this.issuer = issuer;
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
        
        log.info("JwtTokenService initialized with issuer: {}, access token expiration: {}s, refresh token expiration: {}s",
                issuer, accessTokenExpirationSeconds, refreshTokenExpirationSeconds);
    }

    public String generateAccessToken(UserEntity user, Set<String> roles, Set<String> permissions) {
        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenExpirationSeconds, ChronoUnit.SECONDS);
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("username", user.getUsername())
                .claim("roles", new ArrayList<>(roles))
                .claim("permissions", new ArrayList<>(permissions))
                .claim("type", "access")
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(UserEntity user, String tokenId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(refreshTokenExpirationSeconds, ChronoUnit.SECONDS);

        return Jwts.builder()
                .id(tokenId)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("username", user.getUsername())
                .claim("type", "refresh")
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (io.jsonwebtoken.JwtException e) {
            log.error("JWT parsing failed: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage(), e);
        }
    }

    public String getTokenType(Claims claims) {
        return claims.get("type", String.class);
    }

    public String getTokenId(Claims claims) {
        return claims.getId();
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
        return claims.getExpiration().before(Date.from(Instant.now()));
    }

    public Long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationSeconds;
    }
}


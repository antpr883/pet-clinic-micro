package com.micro.auth.client.security;

import com.micro.auth.client.jwt.JwtTokenValidator;
import com.micro.auth.client.jwt.JwtUserDetails;
import com.micro.auth.client.jwt.JwtValidationException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фільтр для автоматичної валідації JWT токенів у всіх мікросервісах.
 * 
 * Використовується в Person, Pet, Clinic Services.
 * Валідує токен, витягує ролі/пермішени та встановлює Authentication в SecurityContext.
 * 
 * Цей компонент можна використовувати в інших сервісах, додавши auth-service як залежність
 * або скопіювавши цей клас в інші сервіси.
 * 
 * Приклад використання в інших сервісах:
 * 1. Додати auth-service як залежність (або скопіювати ці класи)
 * 2. Додати SecurityConfig з цим фільтром
 * 3. Додати @PreAuthorize на endpoints
 */
/**
 * ⚠️ ВАЖЛИВО: Цей клас НЕ використовується в auth-service!
 * 
 * Він призначений ТІЛЬКИ для інших мікросервісів (Person, Pet, Clinic).
 * 
 * В auth-service використовується Apache Shiro, а не Spring Security.
 */
// @Component  // ЗАКОМЕНТОВАНО - не активувати в auth-service!
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenValidator jwtTokenValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = jwtTokenValidator.parseToken(token);
            
            if (jwtTokenValidator.isTokenExpired(claims)) {
                log.warn("Token expired for user: {}", jwtTokenValidator.getUsername(claims));
                chain.doFilter(request, response);
                return;
            }

            JwtUserDetails userDetails = new JwtUserDetails(claims, jwtTokenValidator);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authenticated user: {} with roles: {}, permissions: {}", 
                    userDetails.getUsername(), userDetails.getRoles(), userDetails.getPermissions());

        } catch (JwtValidationException ex) {
            log.warn("JWT validation failed: {}", ex.getMessage());
            // Don't set authentication - let Spring Security handle unauthorized requests
        }

        chain.doFilter(request, response);
    }
}


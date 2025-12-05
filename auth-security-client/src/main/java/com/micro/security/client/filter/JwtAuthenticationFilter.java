package com.micro.security.client.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.security.client.jwt.JwtTokenValidator;
import com.micro.security.client.jwt.JwtUserDetails;
import com.micro.security.client.jwt.JwtValidationException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * Фільтр для автоматичної валідації JWT токенів у всіх мікросервісах.
 * 
 * BEST PRACTICE: Автоматично додається до SecurityFilterChain через Spring Boot Starter.
 * 
 * Поведінка:
 * - Читає Bearer токен з Authorization header
 * - Валідує токен через JwtTokenValidator
 * - Створює JwtUserDetails та встановлює Authentication в SecurityContext
 * - При помилках повертає 401 з JSON помилкою (не продовжує chain)
 * 
 * Використовується в Person, Pet, Clinic Services.
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenValidator jwtTokenValidator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        // Якщо немає Authorization header - продовжуємо (може бути public endpoint)
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = jwtTokenValidator.parseToken(token);
            
            // BEST PRACTICE: Перевіряємо термін дії та повертаємо 401, а не продовжуємо chain
            if (jwtTokenValidator.isTokenExpired(claims)) {
                log.warn("Token expired for user: {}", jwtTokenValidator.getUsername(claims));
                sendUnauthorized(response, "Token expired");
                return;
            }

            // Створюємо JwtUserDetails з claims
            JwtUserDetails userDetails = new JwtUserDetails(claims, jwtTokenValidator);

            // Створюємо Authentication та встановлюємо в SecurityContext
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
            // BEST PRACTICE: При невалідному токені повертаємо 401, а не продовжуємо chain
            log.warn("JWT validation failed: {}", ex.getMessage());
            sendUnauthorized(response, "Invalid token: " + ex.getMessage());
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Відправляє 401 Unauthorized відповідь з JSON помилкою.
     * 
     * BEST PRACTICE: Явна відповідь при помилках валідації токена.
     * Не продовжуємо chain - клієнт має знати про помилку.
     * 
     * @param response HttpServletResponse
     * @param message Повідомлення про помилку
     * @throws IOException якщо не вдалося записати відповідь
     */
    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, String> errorResponse = Map.of(
                "error", "Unauthorized",
                "message", message,
                "status", String.valueOf(HttpStatus.UNAUTHORIZED.value())
        );
        
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}


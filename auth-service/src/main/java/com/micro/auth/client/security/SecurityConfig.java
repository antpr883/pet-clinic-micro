package com.micro.auth.client.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Конфігурація Spring Security для мікросервісів.
 * 
 * Використовується в Person, Pet, Clinic Services.
 * Налаштовує JWT валідацію та метод security (@PreAuthorize).
 * 
 * Цей компонент можна використовувати в інших сервісах, додавши auth-service як залежність
 * або скопіювавши цей клас в інші сервіси.
 * 
 * Приклад використання в інших сервісах:
 * 1. Додати auth-service як залежність (або скопіювати ці класи)
 * 2. Імпортувати цей SecurityConfig
 * 3. Додати @PreAuthorize на endpoints
 */
/**
 * ⚠️ ВАЖЛИВО: Цей клас НЕ використовується в auth-service!
 * 
 * Він призначений ТІЛЬКИ для інших мікросервісів (Person, Pet, Clinic).
 * 
 * В auth-service використовується Apache Shiro, а не Spring Security.
 * 
 * Щоб використати в інших сервісах:
 * 1. Скопіювати цей клас в інший сервіс
 * 2. Або додати auth-service як залежність та імпортувати з умовою
 */
// @Configuration  // ЗАКОМЕНТОВАНО - не активувати в auth-service!
// @EnableWebSecurity
// @EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}


package com.micro.security.client.config;

import com.micro.security.client.filter.JwtAuthenticationFilter;
import com.micro.security.client.jwt.JwtTokenValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Boot Auto-Configuration для JWT аутентифікації в мікросервісах.
 * 
 * BEST PRACTICE: Автоматично налаштовує SecurityFilterChain з JWT валідацією.
 * 
 * Умови активації:
 * - JwtTokenValidator клас присутній в classpath
 * - Власний SecurityFilterChain не визначений (@ConditionalOnMissingBean)
 * - Можна вимкнути через jwt.security.enabled=false
 * 
 * Використання:
 * 1. Додати dependency на auth-security-client
 * 2. Налаштувати jwt.signing.secret та jwt.issuer в application.yml
 * 3. Використовувати @PreAuthorize на endpoints
 * 
 * Якщо потрібні додаткові правила - створити свій SecurityFilterChain з @Order(1).
 */
@AutoConfiguration
@ConditionalOnClass(JwtTokenValidator.class)
@ConditionalOnProperty(prefix = "jwt.security", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(AuthClientSecurityProperties.class)
public class AuthClientSecurityAutoConfiguration {

    /**
     * Створює JwtTokenValidator bean для валідації JWT токенів.
     * 
     * BEST PRACTICE: Використовує @ConditionalOnMissingBean для можливості override.
     * 
     * @param secret JWT signing secret з application.yml (jwt.signing.secret)
     * @param issuer JWT issuer з application.yml (jwt.issuer, default: pet-clinic-auth)
     * @return JwtTokenValidator bean
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtTokenValidator jwtTokenValidator(
            @Value("${jwt.signing.secret}") String secret,
            @Value("${jwt.issuer:pet-clinic-auth}") String issuer) {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("jwt.signing.secret must be configured in application.yml");
        }
        return new JwtTokenValidator(secret, issuer);
    }

    /**
     * Створює JwtAuthenticationFilter bean для автоматичної валідації токенів.
     * 
     * BEST PRACTICE: Автоматично додається до SecurityFilterChain.
     * 
     * @param jwtTokenValidator JwtTokenValidator для валідації токенів
     * @return JwtAuthenticationFilter bean
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator) {
        return new JwtAuthenticationFilter(jwtTokenValidator);
    }

    /**
     * Створює SecurityFilterChain з JWT аутентифікацією.
     * 
     * BEST PRACTICE: 
     * - Stateless сесії (SessionCreationPolicy.STATELESS)
     * - CSRF вимкнено (для REST API)
     * - Публічні endpoints: /actuator/health, /actuator/info, /swagger-ui/**, /v3/api-docs/**
     * - Всі інші endpoints потребують аутентифікації
     * - JwtAuthenticationFilter додається перед UsernamePasswordAuthenticationFilter
     * 
     * Можна override через @Order(1) та створення свого SecurityFilterChain.
     * 
     * @param http HttpSecurity для конфігурації
     * @param jwtFilter JwtAuthenticationFilter для валідації токенів
     * @return SecurityFilterChain bean
     * @throws Exception якщо конфігурація не вдалася
     */
    @Bean
    @ConditionalOnMissingBean(name = "securityFilterChain")
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Actuator endpoints
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // Swagger UI та документація
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api-docs/**").permitAll()
                .requestMatchers("/webjars/**", "/swagger-resources/**").permitAll()
                // Всі інші endpoints потребують аутентифікації
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}


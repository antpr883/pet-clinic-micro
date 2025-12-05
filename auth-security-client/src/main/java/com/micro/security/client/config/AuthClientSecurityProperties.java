package com.micro.security.client.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties для auth-security-client.
 * 
 * BEST PRACTICE: Зовнішня конфігурація через application.yml.
 * 
 * Приклад конфігурації:
 * ```yaml
 * jwt:
 *   signing:
 *     secret: "your-secret-key-min-32-characters"
 *   issuer: "pet-clinic-auth"
 *   security:
 *     enabled: true
 * ```
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class AuthClientSecurityProperties {

    /**
     * JWT signing configuration.
     */
    private Signing signing = new Signing();

    /**
     * JWT issuer (default: pet-clinic-auth).
     */
    private String issuer = "pet-clinic-auth";

    /**
     * Security auto-configuration enabled (default: true).
     */
    private Security security = new Security();

    @Getter
    @Setter
    public static class Signing {
        /**
         * JWT signing secret key.
         * BEST PRACTICE: Мінімум 32 символи для HS256 (256 bits).
         * Для продакшену використовувати RS256 з приватним/публічним ключем.
         */
        private String secret;
    }

    @Getter
    @Setter
    public static class Security {
        /**
         * Enable/disable security auto-configuration (default: true).
         * Встановити false для повного контролю над SecurityFilterChain.
         */
        private boolean enabled = true;
    }
}


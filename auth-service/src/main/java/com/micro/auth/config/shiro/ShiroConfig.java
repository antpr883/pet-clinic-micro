package com.micro.auth.config.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфігурація Apache Shiro для Auth Service.
 * 
 * Shiro використовується для:
 * 1. Аутентифікації користувачів (username + password) через JpaUserRealm
 * 2. Авторизації через @RequiresRoles та @RequiresPermissions аннотації
 * 3. Роботи з JWT токенами в stateless архітектурі
 * 
 * Best Practices:
 * - SecurityManager встановлюється в SecurityUtils через ShiroSecurityManagerInitializer після повної ініціалізації контексту
 * - Stateless архітектура (сесії вимкнені)
 * - Правильний порядок ініціалізації без циклічних залежностей (@DependsOn для advisor)
 */
@Configuration
public class ShiroConfig implements WebMvcConfigurer {

    @Autowired
    private JwtShiroInterceptor jwtShiroInterceptor;
    
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Створює та налаштовує SecurityManager - головний компонент Shiro.
     * 
     * SecurityManager:
     * - Керує всією безпекою в Shiro
     * - Координує роботу Realm, Session, Cache
     * - Використовується для аутентифікації та авторизації
     * 
     * ВАЖЛИВО: НЕ встановлюємо SecurityManager в SecurityUtils тут,
     * щоб уникнути проблем з порядком ініціалізації Spring AOP.
     * Встановлення відбувається в onApplicationEvent() після повної ініціалізації.
     */
    @Bean(name = "securityManager")
    public SecurityManager securityManager(JpaUserRealm jpaUserRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        
        // Встановлюємо наш кастомний Realm для аутентифікації та авторизації
        securityManager.setRealm(jpaUserRealm);
        
        // Налаштовуємо SessionManager для stateless архітектури
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);  // Вимкнути валідацію сесій
        sessionManager.setSessionIdCookieEnabled(false);              // Вимкнути cookie для сесій
        securityManager.setSessionManager(sessionManager);
        
        return securityManager;
    }

    /**
     * LifecycleBeanPostProcessor забезпечує правильну ініціалізацію Shiro компонентів.
     * 
     * Викликає lifecycle методи (init, destroy) для Shiro beans.
     * Необхідний для коректної роботи Shiro в Spring контексті.
     * 
     * ВАЖЛИВО: Статичний метод, щоб уникнути проблем з BeanPostProcessor порядком ініціалізації.
     */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * AuthorizationAttributeSourceAdvisor дозволяє використовувати Shiro аннотації
     * (@RequiresRoles, @RequiresPermissions) в Spring контролерах.
     * 
     * Працює через Spring AOP - перехоплює виклики методів з Shiro аннотаціями
     * та перевіряє права доступу через SecurityManager.
     * 
     * ВАЖЛИВО: Отримуємо SecurityManager через ApplicationContext, щоб уникнути
     * циклічної залежності з Spring AOP. SecurityManager буде отриманий після
     * його створення, коли буде потрібен.
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        // Отримуємо SecurityManager через ApplicationContext, щоб уникнути циклічної залежності
        SecurityManager securityManager = applicationContext.getBean("securityManager", SecurityManager.class);
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();

        // Публічні endpoints (anon = anonymous, доступ без аутентифікації)
        // Swagger UI та документація
        chainDefinition.addPathDefinition("/swagger-ui/**", "anon");
        chainDefinition.addPathDefinition("/swagger-ui.html", "anon");
        chainDefinition.addPathDefinition("/swagger-ui/index.html", "anon");
        chainDefinition.addPathDefinition("/v3/api-docs/**", "anon");
        chainDefinition.addPathDefinition("/api-docs", "anon");
        chainDefinition.addPathDefinition("/api-docs/**", "anon");
        chainDefinition.addPathDefinition("/webjars/**", "anon");
        chainDefinition.addPathDefinition("/swagger-resources/**", "anon");
        
        // Actuator endpoints
        chainDefinition.addPathDefinition("/actuator/health", "anon");
        chainDefinition.addPathDefinition("/actuator/info", "anon");
        
        // Публічні auth endpoints
        chainDefinition.addPathDefinition("/api/v1/auth/login", "anon");
        chainDefinition.addPathDefinition("/api/v1/auth/refresh", "anon");
        chainDefinition.addPathDefinition("/api/v1/auth/logout", "anon");
        
        // Вимкнути дефолтну сторінку логіну Shiro
        chainDefinition.addPathDefinition("/login", "anon");
        chainDefinition.addPathDefinition("/login.html", "anon");

        // Всі інші endpoints потребують аутентифікації (authc = authenticated)
        chainDefinition.addPathDefinition("/**", "authc");

        return chainDefinition;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtShiroInterceptor)
                .addPathPatterns("/api/v1/auth/**")
                .excludePathPatterns("/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/logout");
    }
}


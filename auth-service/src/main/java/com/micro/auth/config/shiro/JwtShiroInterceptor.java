package com.micro.auth.config.shiro;

import com.micro.auth.service.jwt.JwtTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor для встановлення Shiro Subject з JWT токена.
 * 
 * Витягує JWT токен з Authorization header та встановлює Subject
 * для роботи @RequiresRoles та інших Shiro аннотацій.
 * 
 * Це дозволяє використовувати Shiro аннотації (@RequiresRoles) в контролерах
 * навіть коли аутентифікація виконується через JWT токени (stateless архітектура).
 */
@Component
@Slf4j
public class JwtShiroInterceptor implements HandlerInterceptor {

    private final JwtTokenService jwtTokenService;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public JwtShiroInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            // Немає токена - продовжуємо (буде перевірено в ShiroFilterChainDefinition)
            return true;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            // Парсимо та валідуємо JWT токен
            Claims claims = jwtTokenService.parseToken(token);

            // Перевірка типу токена (має бути access token)
            String tokenType = jwtTokenService.getTokenType(claims);
            if (!"access".equals(tokenType)) {
                log.debug("Invalid token type: {}, expected 'access'", tokenType);
                return true;
            }

            // Отримуємо userId з токена
            Long userId = jwtTokenService.getUserId(claims);

            // Встановлюємо Subject з JWT токена
            Subject subject = SecurityUtils.getSubject();
            
            // BEST PRACTICE: Перевіряємо, чи Subject вже аутентифікований з цим UserEntity
            // Якщо principal - це UserEntity, перевіряємо userId
            Object principal = subject.getPrincipal();
            boolean needsReauth = !subject.isAuthenticated();
            
            if (principal instanceof com.micro.auth.data.entities.UserEntity) {
                // Principal - це UserEntity, перевіряємо userId
                needsReauth = needsReauth || !userId.equals(((com.micro.auth.data.entities.UserEntity) principal).getId());
            } else if (principal != null) {
                // Principal - це userId (fallback для сумісності)
                needsReauth = needsReauth || !userId.equals(principal);
            }
            
            if (needsReauth) {
                // Створюємо JwtAuthenticationToken для аутентифікації через Realm
                // Realm завантажить UserEntity з ролями одним запитом через Entity Graph
                JwtAuthenticationToken authToken = new JwtAuthenticationToken(userId, token);
                
                try {
                    // Викликаємо login - Realm обробить JwtAuthenticationToken
                    // JpaUserRealm.doGetAuthenticationInfo() завантажить UserEntity з ролями
                    subject.login(authToken);
                    log.debug("Subject authenticated via JWT for userId: {}", userId);
                } catch (AuthenticationException e) {
                    log.warn("Failed to authenticate Subject via JWT: {}", e.getMessage());
                    // Продовжуємо - помилка буде оброблена в ShiroFilterChainDefinition
                }
            }

        } catch (Exception e) {
            log.debug("JWT token parsing failed: {}", e.getMessage());
            // Продовжуємо виконання - помилка буде оброблена в ShiroFilterChainDefinition
        }

        return true;
    }
}


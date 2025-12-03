package com.micro.auth.service.auth;

import com.micro.auth.data.dto.auth.LoginRequestDto;
import com.micro.auth.data.dto.auth.TokenResponseDto;
import com.micro.auth.data.entities.RefreshTokenEntity;
import com.micro.auth.data.entities.UserEntity;
import com.micro.auth.exception.AuthenticationFailedException;
import com.micro.auth.exception.InvalidTokenException;
import com.micro.auth.repository.RefreshTokenRepository;
import com.micro.auth.service.jwt.JwtTokenService;
import com.micro.auth.service.user.UserEntityGraphService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final UserEntityGraphService userEntityGraphService;

    @Value("${jwt.access-token.expiration-seconds}")
    private Long accessTokenExpirationSeconds;

    /**
     * Аутентифікація користувача та генерація JWT токенів.
     * 
     * BEST PRACTICE: Використовуємо UserEntity з Subject (вже завантажений в JpaUserRealm).
     * Це уникнення повторного запиту до БД - користувач з ролями вже завантажений через Entity Graph.
     * 
     * Процес:
     * 1. Створюємо UsernamePasswordToken з username та password
     * 2. Викликаємо subject.login() - Shiro викликає JpaUserRealm.doGetAuthenticationInfo()
     * 3. JpaUserRealm завантажує UserEntity з ролями одним запитом через Entity Graph
     * 4. UserEntity зберігається в Subject як principal
     * 5. Отримуємо UserEntity з Subject - НЕ робимо повторний запит до БД
     * 6. Генеруємо access та refresh токени
     * 7. Зберігаємо refresh токен в БД
     * 
     * Оптимізація:
     * - 1 запит до БД замість 2-3 (завдяки Entity Graph та збереженню UserEntity в Subject)
     * - Уникнення N+1 проблеми (ролі та пермішени завантажуються одним JOIN)
     * 
     * @param request LoginRequestDto з username та password
     * @return TokenResponseDto з access та refresh токенами
     * @throws AuthenticationFailedException якщо аутентифікація не вдала
     */
    @Transactional
    public TokenResponseDto login(LoginRequestDto request) {
        log.debug("Login attempt for username: {}", request.getUsername());

        // ============================================================
        // Крок 1: Створюємо UsernamePasswordToken для Shiro
        // ============================================================
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(
                request.getUsername(),
                request.getPassword()
        );

        // ============================================================
        // Крок 2: Викликаємо Shiro аутентифікацію
        // Shiro викликає JpaUserRealm.doGetAuthenticationInfo(), який:
        // - Завантажує UserEntity з ролями одним запитом через Entity Graph
        // - Зберігає UserEntity в Subject як principal
        // ============================================================
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", request.getUsername());
            throw new AuthenticationFailedException("Invalid username or password", e);
        }

        // ============================================================
        // Крок 3: BEST PRACTICE - Отримуємо UserEntity з Subject
        // UserEntity вже завантажений з ролями в JpaUserRealm.doGetAuthenticationInfo()
        // НЕ робимо повторний запит до БД - використовуємо дані з Subject
        // ============================================================
        Object principal = subject.getPrincipal();
        UserEntity user;
        
        if (principal instanceof UserEntity) {
            // Оптимально: UserEntity вже в Subject (завантажений з ролями через Entity Graph)
            user = (UserEntity) principal;
            log.debug("Using UserEntity from Subject (no additional DB query)");
        } else {
            // Fallback для сумісності (якщо principal - це userId)
            log.warn("Principal is not UserEntity, falling back to DB query");
            // BEST PRACTICE: Використовуємо UserEntityGraphService для уникнення дублювання коду
            user = userEntityGraphService.findByUsernameAndActiveTrueWithRolesAndPermissions(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found after authentication"));
        }

        // ============================================================
        // Крок 4: Витягуємо ролі та пермішени з UserEntity
        // Ролі та пермішени вже завантажені через Entity Graph - немає N+1 проблеми
        // ============================================================
        Set<String> roles = extractRoles(user);
        Set<String> permissions = extractPermissions(user);

        // ============================================================
        // Крок 5: Генеруємо JWT токени
        // ============================================================
        String refreshTokenId = UUID.randomUUID().toString();
        String accessToken = jwtTokenService.generateAccessToken(user, roles, permissions);
        String refreshToken = jwtTokenService.generateRefreshToken(user, refreshTokenId);

        // ============================================================
        // Крок 6: Зберігаємо refresh токен в БД
        // ============================================================
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .user(user)
                .tokenId(refreshTokenId)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenService.getRefreshTokenExpirationSeconds()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        log.info("User {} successfully logged in", user.getUsername());

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationSeconds)
                .build();
    }

    /**
     * Оновлення JWT токенів через refresh token.
     * 
     * BEST PRACTICE: Завантажуємо користувача з ролями одним запитом через Entity Graph.
     * Це уникнення N+1 проблеми та забезпечення консистентності даних.
     * 
     * Процес:
     * 1. Парсимо та валідуємо refresh token
     * 2. Знаходимо refresh token в БД та перевіряємо валідність
     * 3. Завантажуємо користувача з ролями одним запитом через Entity Graph
     * 4. Генеруємо нові access та refresh токени
     * 5. Відкликаємо старий refresh token
     * 6. Зберігаємо новий refresh token в БД
     * 
     * @param refreshTokenString refresh token з Authorization header
     * @return TokenResponseDto з новими access та refresh токенами
     * @throws InvalidTokenException якщо refresh token невалідний
     */
    @Transactional
    public TokenResponseDto refresh(String refreshTokenString) {
        log.debug("Refresh token request");

        // ============================================================
        // Крок 1: Парсимо та валідуємо refresh token
        // ============================================================
        Claims claims;
        try {
            claims = jwtTokenService.parseToken(refreshTokenString);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid refresh token", e);
        }
        
        if (!"refresh".equals(jwtTokenService.getTokenType(claims))) {
            throw new InvalidTokenException("Invalid token type, expected 'refresh'");
        }

        // ============================================================
        // Крок 2: Знаходимо refresh token в БД та перевіряємо валідність
        // ============================================================
        String tokenId = jwtTokenService.getTokenId(claims);
        RefreshTokenEntity refreshToken = refreshTokenRepository.findValidToken(
                tokenId, LocalDateTime.now())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired refresh token"));

        // ============================================================
        // Крок 3: BEST PRACTICE - Отримуємо userId з JWT claims замість refreshToken.getUser()
        // refreshToken.getUser() викликає lazy loading (окремий запит до БД)
        // Використовуємо userId з JWT claims - він вже є в токені, не потрібен додатковий запит
        // ============================================================
        Long userId = jwtTokenService.getUserId(claims);
        
        // ============================================================
        // Крок 3: BEST PRACTICE - Завантажуємо користувача з ролями одним запитом через Entity Graph
        // Використовуємо UserEntityGraphService для уникнення дублювання коду
        // Це уникнення N+1 проблеми та забезпечення консистентності даних
        // ============================================================
        UserEntity user = userEntityGraphService.findByIdWithRolesAndPermissions(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        // Витягуємо ролі та пермішени з UserEntity (вже завантажені через Entity Graph)
        Set<String> roles = extractRoles(user);
        Set<String> permissions = extractPermissions(user);

        // Generate new tokens
        String newRefreshTokenId = UUID.randomUUID().toString();
        String newAccessToken = jwtTokenService.generateAccessToken(user, roles, permissions);
        String newRefreshToken = jwtTokenService.generateRefreshToken(user, newRefreshTokenId);

        // Revoke old refresh token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // Save new refresh token
        RefreshTokenEntity newRefreshTokenEntity = RefreshTokenEntity.builder()
                .user(user)
                .tokenId(newRefreshTokenId)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenService.getRefreshTokenExpirationSeconds()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);

        log.info("Token refreshed for user {}", user.getUsername());

        return TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationSeconds)
                .build();
    }

    @Transactional
    public void logout(String refreshTokenString) {
        log.debug("Logout request");

        Claims claims;
        try {
            claims = jwtTokenService.parseToken(refreshTokenString);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid refresh token", e);
        }
        
        String tokenId = jwtTokenService.getTokenId(claims);

        RefreshTokenEntity refreshToken = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.info("User logged out, token revoked");
    }

    /**
     * Витягує активні ролі з UserEntity.
     * 
     * BEST PRACTICE: Ролі вже завантажені через Entity Graph - немає N+1 проблеми.
     * 
     * @param user UserEntity з завантаженими ролями
     * @return Set активних ролей (назви)
     */
    private Set<String> extractRoles(UserEntity user) {
        return user.getRoles().stream()
                .filter(role -> Boolean.TRUE.equals(role.getActive()))
                .map(role -> role.getName())
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Витягує активні пермішени з ролей UserEntity.
     * 
     * BEST PRACTICE: Пермішени вже завантажені через Entity Graph (roles.permissions) - немає N+1 проблеми.
     * 
     * @param user UserEntity з завантаженими ролями та пермішенами
     * @return Set активних пермішнів (назви)
     */
    private Set<String> extractPermissions(UserEntity user) {
        return user.getRoles().stream()
                .filter(role -> Boolean.TRUE.equals(role.getActive()))
                .flatMap(role -> role.getPermissions().stream())
                .filter(permission -> Boolean.TRUE.equals(permission.getActive()))
                .map(permission -> permission.getName())
                .collect(java.util.stream.Collectors.toSet());
    }
}


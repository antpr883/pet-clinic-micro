package com.micro.auth.config.shiro;

import com.micro.auth.data.entities.PermissionEntity;
import com.micro.auth.data.entities.RoleEntity;
import com.micro.auth.data.entities.UserEntity;
import com.micro.auth.service.user.UserEntityGraphService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JpaUserRealm extends AuthorizingRealm {

    private final UserEntityGraphService userEntityGraphService;

    public JpaUserRealm(UserEntityGraphService userEntityGraphService) {
        this.userEntityGraphService = userEntityGraphService;
        setCredentialsMatcher(passwordMatcher());
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken || token instanceof JwtAuthenticationToken;
    }

    /**
     * Аутентифікація користувача.
     * 
     * BEST PRACTICE: Зберігаємо UserEntity в Subject як principal замість userId.
     * Це дозволяє уникнути повторних запитів до БД в AuthService.login().
     * 
     * Оптимізація:
     * - Завантажуємо користувача з ролями та пермішенами одним запитом через Entity Graph
     * - Зберігаємо повний UserEntity в Subject - не потрібен повторний запит
     * - Використовуємо Entity Graph для уникнення N+1 проблеми
     * 
     * @param token AuthenticationToken (UsernamePasswordToken або JwtAuthenticationToken)
     * @return AuthenticationInfo з UserEntity як principal
     * @throws AuthenticationException якщо аутентифікація не вдала
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {

        // ============================================================
        // JWT токен для аутентифікації через JWT (stateless)
        // Використовується в JwtShiroInterceptor для встановлення Subject
        // ============================================================
        if (token instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) token;
            Long userId = jwtToken.getUserId();
            
            log.info("JpaUserRealm: Authenticating user via JWT: {}", userId);
            
            // BEST PRACTICE: Завантажуємо користувача з ролями та пермішенами одним запитом через Entity Graph
            // Використовуємо UserEntityGraphService для уникнення дублювання коду
            UserEntity user = userEntityGraphService.findByIdWithRolesAndPermissions(userId)
                    .orElseThrow(() -> new UnknownAccountException("User not found: " + userId));
            
            log.info("JpaUserRealm: User found: {} (id: {})", user.getUsername(), user.getId());
            
            if (!Boolean.TRUE.equals(user.getActive())) {
                throw new DisabledAccountException("User is disabled: " + userId);
            }
            
            // Для JWT токенів не перевіряємо пароль - токен вже валідний
            // BEST PRACTICE: Зберігаємо UserEntity як principal замість userId
            // Це дозволяє використовувати дані користувача без повторних запитів до БД
            SimpleAuthenticationInfo authInfo = new SimpleAuthenticationInfo(
                    user,                    // principal (UserEntity) - зберігаємо повну сутність
                    jwtToken.getJwtToken(),  // credentials (JWT token)
                    getName()
            );
            
            log.info("JpaUserRealm: Returning AuthenticationInfo with principal: {} (type: {})", 
                    authInfo.getPrincipals().getPrimaryPrincipal(), 
                    authInfo.getPrincipals().getPrimaryPrincipal().getClass().getSimpleName());
            
            return authInfo;
        }

        // ============================================================
        // UsernamePasswordToken для login (username + password)
        // Використовується в AuthService.login() для первинної аутентифікації
        // ============================================================
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        log.debug("Authenticating user: {}", username);

        // BEST PRACTICE: Завантажуємо користувача з ролями та пермішенами одним запитом через Entity Graph
        // Використовуємо UserEntityGraphService для уникнення дублювання коду
        // Це оптимізує запити:
        // 1. Уникаємо N+1 проблеми (ролі та пермішени завантажуються одним JOIN)
        // 2. Не потрібен повторний запит в AuthService.login() - UserEntity вже в Subject
        UserEntity user = userEntityGraphService.findByUsernameAndActiveTrueWithRolesAndPermissions(username)
                .orElseThrow(() -> new UnknownAccountException("User not found: " + username));

        log.debug("User found: {} with {} roles", user.getUsername(), user.getRoles().size());

        // BEST PRACTICE: Зберігаємо UserEntity як principal замість userId
        // Це дозволяє:
        // - Уникнути повторного запиту до БД в AuthService.login()
        // - Використовувати дані користувача (ролі, пермішени) без додаткових запитів
        // - Покращити продуктивність (1 запит замість 2-3)
        return new SimpleAuthenticationInfo(
                user,                        // principal (UserEntity) - зберігаємо повну сутність
                user.getPasswordHash(),      // hashed password для перевірки
                ByteSource.Util.bytes(user.getPasswordSalt()),
                getName()
        );
    }

    /**
     * Авторизація користувача - отримання ролей та пермішнів.
     * 
     * BEST PRACTICE: Використовуємо UserEntity з principal (вже завантажений в doGetAuthenticationInfo).
     * Це уникнення повторного запиту до БД - ролі та пермішени вже завантажені через Entity Graph.
     * 
     * Викликається Shiro автоматично при використанні @RequiresRoles, @RequiresPermissions тощо.
     * 
     * @param principals PrincipalCollection з UserEntity як principal
     * @return AuthorizationInfo з ролями та пермішенами
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // BEST PRACTICE: Отримуємо UserEntity з principal (вже завантажений в doGetAuthenticationInfo)
        // Це уникнення повторного запиту до БД - ролі та пермішени вже завантажені через Entity Graph
        Object principal = principals.getPrimaryPrincipal();
        
        UserEntity user;
        if (principal instanceof UserEntity) {
            // UserEntity вже в principal - використовуємо його (оптимально)
            user = (UserEntity) principal;
            log.debug("Authorizing user from principal: {}", user.getUsername());
        } else {
            // Fallback для сумісності (якщо principal - це userId)
            Long userId = (Long) principal;
            log.debug("Authorizing user ID: {} (fallback - loading from DB)", userId);
            
            // BEST PRACTICE: Завантажуємо користувача з ролями та пермішенами одним запитом через Entity Graph
            // Використовуємо UserEntityGraphService для уникнення дублювання коду
            user = userEntityGraphService.findByIdWithRolesAndPermissions(userId)
                    .orElseThrow(() -> new UnknownAccountException("User not found: " + userId));
        }

        // Витягуємо активні ролі з UserEntity (вже завантажені через Entity Graph)
        Set<String> roles = user.getRoles().stream()
                .filter(role -> Boolean.TRUE.equals(role.getActive()))
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());

        // Витягуємо активні пермішени з ролей (вже завантажені через Entity Graph)
        Set<String> permissions = user.getRoles().stream()
                .filter(role -> Boolean.TRUE.equals(role.getActive()))
                .flatMap(r -> r.getPermissions().stream())
                .filter(permission -> Boolean.TRUE.equals(permission.getActive()))
                .map(PermissionEntity::getName)
                .collect(Collectors.toSet());

        log.debug("User {} has roles: {}, permissions: {}", user.getUsername(), roles, permissions);

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(permissions);
        return info;
    }

    /**
     * Перевизначаємо перевірку credentials для JWT токенів.
     * 
     * Для JWT токенів не потрібно перевіряти пароль через HashedCredentialsMatcher,
     * оскільки JWT токен вже валідований в JwtShiroFilter перед викликом login().
     * 
     * Для UsernamePasswordToken використовується стандартний HashedCredentialsMatcher.
     */
    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) 
            throws AuthenticationException {
        // Для JWT токенів не перевіряємо credentials - токен вже валідний
        if (token instanceof JwtAuthenticationToken) {
            log.info("JpaUserRealm: Skipping credentials check for JWT token - token already validated");
            log.info("JpaUserRealm: AuthenticationInfo principal: {}", info.getPrincipals().getPrimaryPrincipal());
            return; // JWT токен вже валідований в JwtShiroFilter
        }
        
        // Для UsernamePasswordToken використовуємо стандартну перевірку через HashedCredentialsMatcher
        log.debug("JpaUserRealm: Using HashedCredentialsMatcher for UsernamePasswordToken");
        super.assertCredentialsMatch(token, info);
    }

    private HashedCredentialsMatcher passwordMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("SHA-256");
        matcher.setHashIterations(1024);
        matcher.setStoredCredentialsHexEncoded(true);
        return matcher;
    }
}


package com.micro.auth.service.auth;

import com.micro.auth.data.dto.auth.LoginRequestDto;
import com.micro.auth.data.dto.auth.TokenResponseDto;
import com.micro.auth.data.entities.PermissionEntity;
import com.micro.auth.data.entities.RefreshTokenEntity;
import com.micro.auth.data.entities.RoleEntity;
import com.micro.auth.data.entities.UserEntity;
import com.micro.auth.exception.AuthenticationFailedException;
import com.micro.auth.exception.InvalidTokenException;
import com.micro.auth.repository.RefreshTokenRepository;
import com.micro.auth.service.jwt.JwtTokenService;
import com.micro.auth.service.user.UserEntityGraphService;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserEntityGraphService userEntityGraphService;

    @InjectMocks
    private AuthService authService;

    private UserEntity testUser;
    private RoleEntity adminRole;
    private PermissionEntity permission;
    private Subject mockSubject;
    private SecurityManager mockSecurityManager;

    @BeforeEach
    void setUp() {
        // Очищаємо ThreadContext перед кожним тестом
        ThreadContext.remove();
        
        // Створюємо моки для Shiro
        mockSecurityManager = mock(SecurityManager.class);
        mockSubject = mock(Subject.class);
        
        // Встановлюємо SecurityManager в ThreadContext
        ThreadContext.bind(mockSecurityManager);
        // Створюємо permission
        permission = PermissionEntity.builder()
                .name("PERSON_READ")
                .active(true)
                .build();

        // Створюємо роль ADMIN
        adminRole = RoleEntity.builder()
                .name("ADMIN")
                .active(true)
                .build();
        adminRole.getPermissions().add(permission);

        // Створюємо користувача
        testUser = UserEntity.builder()
                .id(1L)
                .username("testuser")
                .passwordHash("hash123")
                .passwordSalt("salt123")
                .email("testuser@example.com")
                .active(true)
                .build();
        testUser.getRoles().add(adminRole);
    }
    
    @AfterEach
    void tearDown() {
        // Очищаємо ThreadContext після кожного тесту
        ThreadContext.remove();
    }

    @Test
    @DisplayName("login - should return tokens when credentials are valid")
    void login_shouldReturnTokens_whenCredentialsValid() {
        // Given
        LoginRequestDto request = new LoginRequestDto("testuser", "password123");
        
        // Мокуємо SecurityUtils.getSubject()
        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getSubject).thenReturn(mockSubject);
            
            // Мокуємо subject.login() - успішна аутентифікація
            doNothing().when(mockSubject).login(any());
            
            // BEST PRACTICE: Після login() Subject містить UserEntity як principal
            // Мокуємо getPrincipal() щоб повернути testUser
            // Це імітує поведінку JpaUserRealm.doGetAuthenticationInfo(), який зберігає UserEntity в Subject
            when(mockSubject.getPrincipal()).thenReturn(testUser);
            when(jwtTokenService.generateAccessToken(any(), any(), any()))
                    .thenReturn("access-token");
            when(jwtTokenService.generateRefreshToken(any(), anyString()))
                    .thenReturn("refresh-token");
            when(jwtTokenService.getRefreshTokenExpirationSeconds())
                    .thenReturn(604800L);

            // When
            TokenResponseDto result = authService.login(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo("access-token");
            assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
            assertThat(result.getTokenType()).isEqualTo("Bearer");
            verify(refreshTokenRepository, times(1)).save(any(RefreshTokenEntity.class));
            verify(mockSubject, times(1)).login(any());
        }
    }

    @Test
    @DisplayName("login - should throw AuthenticationFailedException when credentials are invalid")
    void login_shouldThrowException_whenCredentialsInvalid() {
        // Given
        LoginRequestDto request = new LoginRequestDto("testuser", "wrongpassword");
        
        // Мокуємо SecurityUtils.getSubject()
        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getSubject).thenReturn(mockSubject);
            
            // Мокуємо subject.login() - невдала аутентифікація
            doThrow(new org.apache.shiro.authc.AuthenticationException("Invalid credentials"))
                    .when(mockSubject).login(any());

            // When/Then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(AuthenticationFailedException.class)
                    .hasMessageContaining("Invalid username or password");
        }
    }

    @Test
    @DisplayName("refresh - should return new tokens when refresh token is valid")
    void refresh_shouldReturnNewTokens_whenRefreshTokenValid() {
        // Given
        String refreshTokenString = "valid-refresh-token";
        Claims claims = mock(Claims.class);
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .id(1L)
                .tokenId("token-id-123")
                .user(testUser)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        when(jwtTokenService.parseToken(refreshTokenString)).thenReturn(claims);
        when(jwtTokenService.getTokenType(claims)).thenReturn("refresh");
        when(jwtTokenService.getTokenId(claims)).thenReturn("token-id-123");
        // BEST PRACTICE: Використовуємо userId з JWT claims замість refreshToken.getUser()
        // Це уникнення lazy loading та додаткового запиту до БД
        when(jwtTokenService.getUserId(claims)).thenReturn(1L);
        when(refreshTokenRepository.findValidToken(eq("token-id-123"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(refreshToken));
        
        // BEST PRACTICE: Мокуємо UserEntityGraphService для завантаження користувача з ролями
        // Використовуємо новий сервіс для уникнення дублювання коду
        when(userEntityGraphService.findByIdWithRolesAndPermissions(eq(1L)))
                .thenReturn(Optional.of(testUser));
        
        when(jwtTokenService.generateAccessToken(any(), any(), any()))
                .thenReturn("new-access-token");
        when(jwtTokenService.generateRefreshToken(any(), anyString()))
                .thenReturn("new-refresh-token");
        when(jwtTokenService.getRefreshTokenExpirationSeconds())
                .thenReturn(604800L);

        // When
        TokenResponseDto result = authService.refresh(refreshTokenString);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
        verify(refreshTokenRepository, times(2)).save(any(RefreshTokenEntity.class));
    }

    @Test
    @DisplayName("refresh - should throw InvalidTokenException when refresh token is invalid")
    void refresh_shouldThrowException_whenRefreshTokenInvalid() {
        // Given
        String refreshTokenString = "invalid-token";
        
        when(jwtTokenService.parseToken(refreshTokenString))
                .thenThrow(new IllegalArgumentException("Invalid token"));

        // When/Then
        assertThatThrownBy(() -> authService.refresh(refreshTokenString))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    @DisplayName("logout - should revoke refresh token")
    void logout_shouldRevokeRefreshToken() {
        // Given
        String refreshTokenString = "valid-refresh-token";
        Claims claims = mock(Claims.class);
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .id(1L)
                .tokenId("token-id-123")
                .user(testUser)
                .revoked(false)
                .build();

        when(jwtTokenService.parseToken(refreshTokenString)).thenReturn(claims);
        when(jwtTokenService.getTokenId(claims)).thenReturn("token-id-123");
        when(refreshTokenRepository.findByTokenId("token-id-123"))
                .thenReturn(Optional.of(refreshToken));

        // When
        authService.logout(refreshTokenString);

        // Then
        verify(refreshTokenRepository, times(1)).save(refreshToken);
        // Verify that revoked was set to true
        assertThat(refreshToken.isRevoked()).isTrue();
    }
}


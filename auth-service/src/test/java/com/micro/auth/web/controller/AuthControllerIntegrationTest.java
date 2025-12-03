package com.micro.auth.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.auth.config.jpa.JpaAuditingConfig;
import com.micro.auth.data.dto.auth.LoginRequestDto;
import com.micro.auth.data.dto.auth.TokenResponseDto;
import com.micro.auth.service.auth.AuthService;
import com.micro.auth.web.exception.ControllerExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration.class,
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
    JpaAuditingConfig.class
})
@Import(ControllerExceptionHandler.class)
@ActiveProfiles("test")
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/auth/login - should return tokens when credentials are valid")
    void login_shouldReturnTokens_whenCredentialsValid() throws Exception {
        // Given
        LoginRequestDto request = LoginRequestDto.builder()
                .username("testuser")
                .password("password123")
                .build();

        TokenResponseDto response = TokenResponseDto.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-123")
                .tokenType("Bearer")
                .expiresIn(900L)
                .build();

        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        // When/Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token-123"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(900));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - should return 400 when request is invalid")
    void login_shouldReturn400_whenRequestInvalid() throws Exception {
        // Given
        LoginRequestDto request = LoginRequestDto.builder()
                .username("")  // Empty username
                .password("password123")
                .build();

        // When/Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh - should return new tokens when refresh token is valid")
    void refresh_shouldReturnNewTokens_whenRefreshTokenValid() throws Exception {
        // Given
        TokenResponseDto response = TokenResponseDto.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .tokenType("Bearer")
                .expiresIn(900L)
                .build();

        when(authService.refresh(anyString())).thenReturn(response);

        // When/Then
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .header("Authorization", "Bearer refresh-token-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/logout - should return 204 when logout is successful")
    void logout_shouldReturn204_whenSuccessful() throws Exception {
        // When/Then
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer refresh-token-123"))
                .andExpect(status().isNoContent());
    }
}


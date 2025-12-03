package com.micro.auth.web.controller;

import com.micro.auth.data.dto.auth.LoginRequestDto;
import com.micro.auth.data.dto.auth.TokenResponseDto;
import com.micro.auth.service.auth.AuthService;
import com.micro.auth.web.response.AppResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Public authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and get access/refresh tokens")
    public ResponseEntity<AppResponse<TokenResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        log.debug("Login request for username: {}", request.getUsername());
        TokenResponseDto response = authService.login(request);
        return ResponseEntity.ok(AppResponse.successful(response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get new access/refresh tokens using refresh token")
    public ResponseEntity<AppResponse<TokenResponseDto>> refresh(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        log.debug("Refresh token request");
        String refreshToken = extractToken(authorization);
        TokenResponseDto response = authService.refresh(refreshToken);
        return ResponseEntity.ok(AppResponse.successful(response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revoke refresh token")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        log.debug("Logout request");
        String refreshToken = extractToken(authorization);
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

    private String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }
        return authorization.substring(7);
    }
}


package com.micro.auth.web.controller;

import com.micro.auth.data.dto.auth.LoginRequestDto;
import com.micro.auth.data.dto.auth.TokenResponseDto;
import com.micro.auth.service.auth.AuthService;
import com.micro.auth.web.response.AppResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
        summary = "Login", 
        description = """
            Authenticate user with username and password, receive JWT access and refresh tokens.
            
            **Example request:**
            ```json
            {
              "username": "admin",
              "password": "admin123"
            }
            ```
            
            **Example response:**
            ```json
            {
              "success": true,
              "data": {
                "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
                "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
                "tokenType": "Bearer",
                "expiresIn": 900
              }
            }
            ```
            
            **Seed user credentials:**
            - Username: `admin`
            - Password: `admin123`
            """,
        requestBody = @RequestBody(
            description = "Login credentials",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequestDto.class),
                examples = {
                    @ExampleObject(
                        name = "Admin login",
                        value = """
                            {
                              "username": "admin",
                              "password": "admin123"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "User login",
                        value = """
                            {
                              "username": "vet",
                              "password": "vet123"
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "success": true,
                          "data": {
                            "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOi...",
                            "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOi...",
                            "tokenType": "Bearer",
                            "expiresIn": 900
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AppResponse<TokenResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        log.debug("Login request for username: {}", request.getUsername());
        TokenResponseDto response = authService.login(request);
        return ResponseEntity.ok(AppResponse.successful(response));
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh token", 
        description = """
            Get new access and refresh tokens using a valid refresh token.
            
            **Authorization header format:**
            ```
            Authorization: Bearer <refresh_token>
            ```
            
            **Example:**
            ```
            Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOi...
            ```
            
            **Example response:**
            ```json
            {
              "success": true,
              "data": {
                "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOi...",
                "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOi...",
                "tokenType": "Bearer",
                "expiresIn": 900
              }
            }
            ```
            """,
        security = {}
    )
    @Parameter(
        name = "Authorization",
        description = "Bearer token with refresh token",
        required = true,
        example = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOi...",
        schema = @Schema(type = "string")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tokens refreshed successfully"
        ),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<AppResponse<TokenResponseDto>> refresh(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        log.debug("Refresh token request");
        String refreshToken = extractToken(authorization);
        TokenResponseDto response = authService.refresh(refreshToken);
        return ResponseEntity.ok(AppResponse.successful(response));
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Logout", 
        description = """
            Revoke refresh token and logout user.
            
            **Authorization header format:**
            ```
            Authorization: Bearer <refresh_token>
            ```
            
            **Example:**
            ```
            Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOi...
            ```
            
            **Response:**
            - Status: `204 No Content` (successful logout)
            - No response body
            """,
        security = {}
    )
    @Parameter(
        name = "Authorization",
        description = "Bearer token with refresh token to revoke",
        required = true,
        example = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOi...",
        schema = @Schema(type = "string")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Logout successful, refresh token revoked"
        ),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
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


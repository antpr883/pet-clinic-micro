package com.micro.auth.config.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Конфігурація OpenAPI (Swagger) для Auth Service.
 * 
 * Налаштовує:
 * - API документацію з описом сервісу
 * - JWT Bearer Token security scheme для авторизації в Swagger UI
 * - Сервери для різних середовищ
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Auth Service API",
        version = "1.0.0",
        description = """
            Centralized Authentication Service for Pet Clinic Microservices.
            
            ## Аутентифікація та авторизація
            
            ### Публічні endpoints (не потребують авторизації):
            - `POST /api/v1/auth/login` - Аутентифікація користувача (username + password)
            - `POST /api/v1/auth/refresh` - Оновлення access token через refresh token
            - `POST /api/v1/auth/logout` - Вихід та revoke refresh token
            
            ### Захищені endpoints (потребують JWT токен):
            - `POST /api/v1/auth/users` - Створення користувача (ADMIN)
            - `GET /api/v1/auth/users` - Список користувачів (ADMIN)
            - `POST /api/v1/auth/roles` - Створення ролі (ADMIN)
            - `POST /api/v1/auth/permissions` - Створення пермішена (ADMIN)
            
            ## Як використовувати:
            
            1. **Логін:**
               - Викликайте `POST /api/v1/auth/login` з username та password
               - Отримайте `accessToken` та `refreshToken`
            
            2. **Авторизація в Swagger:**
               - Натисніть кнопку "Authorize" (🔒) в Swagger UI
               - Введіть: `Bearer <accessToken>`
               - Тепер можете викликати захищені endpoints
            
            3. **Оновлення токена:**
               - Якщо access token прострочився, використайте `POST /api/v1/auth/refresh`
               - Передайте refresh token в Authorization header
               - Отримайте нові access та refresh токени
            
            ## Seed дані:
            - Username: `admin`
            - Password: `admin123`
            - Role: `ADMIN`
            """,
        contact = @Contact(
            name = "Pet Clinic Development Team",
            email = "dev@petclinic.local"
        ),
        license = @License(
            name = "Internal Use Only"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8084",
            description = "Development Server"
        ),
        @Server(
            url = "https://api.petclinic.local",
            description = "Production Server"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "JWT Bearer Token авторизація. Отримайте токен через POST /api/v1/auth/login"
)
public class OpenApiConfig {
    // Конфігурація через аннотації
}


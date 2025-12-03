package com.micro.person.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфігурація OpenAPI (Swagger) для документації API.
 * 
 * Production-ready конфігурація з повною інформацією про API.
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:person-service}")
    private String applicationName;

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Person Service API")
                        .version("1.0.0")
                        .description("""
                                RESTful API для управління персонами та контактами.
                                
                                ## Основні можливості:
                                - CRUD операції для персон
                                - CRUD операції для контактів
                                - Пошук з RSQL запитами
                                - Пагінація та сортування
                                - Entity Graph для оптимізації запитів
                                - М'яке видалення (soft delete)
                                
                                ## Відповіді API:
                                - Успішні операції обгортаються в `AppResponse` або `PaginationResponse`
                                - Помилки обробляються глобальним `ControllerExceptionHandler`
                                
                                ## Аутентифікація:
                                В даний момент API не вимагає аутентифікації.
                                """)
                        .contact(new Contact()
                                .name("Person Service Team")
                                .email("support@petclinic.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.petclinic.com")
                                .description("Production Server")));
    }
}


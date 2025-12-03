package com.micro.clinic.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Конфігурація Testcontainers для інтеграційних тестів.
 * 
 * Створює PostgreSQL контейнер для тестів, який автоматично підключається
 * до Spring Boot через @ServiceConnection.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    /**
     * Створює PostgreSQL контейнер для тестів.
     * 
     * @ServiceConnection автоматично налаштовує DataSource в Spring Boot
     * для підключення до цього контейнера.
     */
    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(false) // Перестворюємо базу кожен раз (чисто для тестів)
                .withInitScript("db/test/init-test-schema.sql"); // Створюємо схему ДО запуску Liquibase
        return container;
    }
}



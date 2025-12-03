package com.micro.clinic;

import com.micro.clinic.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * End-to-end тест для перевірки завантаження Spring Boot контексту.
 * 
 * Використовує @SpringBootTest з Testcontainers для повного інтеграційного тестування.
 * 
 * Переваги:
 * - Перевіряє завантаження всього Spring контексту
 * - Використовує реальну PostgreSQL БД через Testcontainers
 * - Виконує Liquibase міграції
 * - Тестує всю інтеграцію компонентів
 */
@SpringBootTest
@ActiveProfiles("testcontainers")
@Import(TestcontainersConfig.class)
@TestPropertySource(properties = {
    "spring.liquibase.enabled=true"
})
class ClinicServiceApplicationTests {

    @Test
    void contextLoads() {
        // Перевіряє, що Spring Boot контекст успішно завантажується
        // з усіма компонентами, репозиторіями, сервісами та конфігураціями
    }
}



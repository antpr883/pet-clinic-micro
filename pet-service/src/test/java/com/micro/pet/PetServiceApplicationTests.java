package com.micro.pet;

import com.micro.pet.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * End-to-end тест для перевірки завантаження Spring Boot контексту.
 */
@SpringBootTest
@ActiveProfiles("testcontainers")
@Import(TestcontainersConfig.class)
@TestPropertySource(properties = {
    "spring.liquibase.enabled=true"
})
class PetServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that Spring Boot context loads successfully
    }
}


package com.micro.person.config.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Конфігурація для JPA Auditing.
 * 
 * Винесено в окремий клас для можливості виключення в тестах (@WebMvcTest).
 * Це дозволяє уникнути помилок "JPA metamodel must not be empty" в slice tests.
 * 
 * Best practice: @EnableJpaAuditing не повинен бути в @SpringBootApplication класі,
 * щоб мати контроль над його активацією в різних контекстах.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // Конфігурація для автоматичного заповнення @CreatedDate, @LastModifiedDate,
    // @CreatedBy, @LastModifiedBy в JPA entities
}


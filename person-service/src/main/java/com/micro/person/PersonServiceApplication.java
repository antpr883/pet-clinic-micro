package com.micro.person;

import com.micro.person.config.envloader.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class.
 * 
 * @EnableJpaAuditing is moved to a separate JpaAuditingConfig class
 * to allow exclusion in tests (@WebMvcTest).
 */
@SpringBootApplication
public class PersonServiceApplication {

    public static void main(String[] args) {
        new EnvLoader();
        SpringApplication.run(PersonServiceApplication.class, args);
    }

}

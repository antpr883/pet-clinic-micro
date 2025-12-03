package com.micro.pet;

import com.micro.pet.config.envloader.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for Pet Service.
 * 
 * @EnableJpaAuditing is moved to a separate JpaAuditingConfig class
 * to allow exclusion in tests (@WebMvcTest).
 */
@SpringBootApplication
public class PetServiceApplication {

    public static void main(String[] args) {
        new EnvLoader();
        SpringApplication.run(PetServiceApplication.class, args);
    }

}


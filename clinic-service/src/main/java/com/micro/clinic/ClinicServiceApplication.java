package com.micro.clinic;

import com.micro.clinic.config.envloader.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for Clinic Service.
 * 
 * @EnableJpaAuditing is moved to a separate JpaAuditingConfig class
 * to allow exclusion in tests (@WebMvcTest).
 */
@SpringBootApplication
public class ClinicServiceApplication {

    public static void main(String[] args) {
        new EnvLoader();
        SpringApplication.run(ClinicServiceApplication.class, args);
    }

}


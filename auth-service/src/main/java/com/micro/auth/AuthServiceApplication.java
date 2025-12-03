package com.micro.auth;

import com.micro.auth.config.envloader.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class AuthServiceApplication {

    public static void main(String[] args) {
        new EnvLoader();
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}


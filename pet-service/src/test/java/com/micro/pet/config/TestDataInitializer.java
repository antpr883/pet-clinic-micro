package com.micro.pet.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Ініціалізатор тестових даних для Pet Service.
 * Завантажує seed data після створення схеми та міграцій Liquibase.
 */
@Slf4j
@TestConfiguration
public class TestDataInitializer {

    /**
     * Завантажує seed data після того, як Liquibase виконає міграції.
     * Викликається через @SqlExecutionListener або вручну в тестах.
     */
    public static void loadSeedData(DataSource dataSource) {
        try {
            log.info("Loading seed data for tests...");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("db/test/seed-test-data.sql"));
            populator.setContinueOnError(false);
            populator.execute(dataSource);
            log.info("Seed data loaded successfully");
        } catch (Exception e) {
            log.error("Failed to load seed data", e);
            throw new RuntimeException("Failed to load seed data", e);
        }
    }
}


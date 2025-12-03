package com.micro.person.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Кастомний PhysicalNamingStrategy для тестів з H2.
 * H2 не підтримує схеми, тому видаляємо schema з імені таблиці.
 * 
 * Цей клас перевизначає методи для видалення schema з усіх SQL запитів,
 * що дозволяє Hibernate коректно працювати з H2 in-memory базою даних.
 */
public class TestPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        // H2 не підтримує catalog, тому завжди повертаємо null
        return null;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        // H2 не підтримує схеми, тому завжди повертаємо null
        // Це призведе до того, що Hibernate не буде додавати schema до SQL запитів
        return null;
    }
}


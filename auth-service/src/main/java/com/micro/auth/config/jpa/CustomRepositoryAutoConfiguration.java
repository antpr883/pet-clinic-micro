package com.micro.auth.config.jpa;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import com.micro.auth.AuthServiceApplication;
import com.micro.auth.repository.CustomJpaRepository;
import com.micro.auth.repository.CustomJpaRepositoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Автоконфігурація для кастомних JPA репозиторіїв з підтримкою Entity Graph.
 */
@Configuration
@ConditionalOnClass(CustomJpaRepository.class)
@Import(CustomRepositoryAutoConfiguration.JpaConfiguration.class)
public class CustomRepositoryAutoConfiguration {

    @Configuration
    @EntityScan(basePackageClasses = AuthServiceApplication.class)
    @EnableJpaRepositories(
            basePackageClasses = AuthServiceApplication.class,
            repositoryBaseClass = CustomJpaRepositoryImpl.class,
            repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class
    )
    public static class JpaConfiguration {
    }
}


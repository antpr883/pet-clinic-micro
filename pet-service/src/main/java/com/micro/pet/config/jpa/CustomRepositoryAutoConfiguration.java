package com.micro.pet.config.jpa;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import com.micro.pet.PetServiceApplication;
import com.micro.pet.repository.CustomJpaRepository;
import com.micro.pet.repository.CustomJpaRepositoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy;

/**
 * Автоконфігурація для кастомних JPA репозиторіїв з підтримкою Entity Graph.
 * 
 * @EnableJpaAuditing винесено в окремий клас JpaAuditingConfig
 * для можливості виключення в тестах (@WebMvcTest).
 */
@Configuration
@ConditionalOnClass(CustomJpaRepository.class)
@Import(CustomRepositoryAutoConfiguration.JpaConfiguration.class)
public class CustomRepositoryAutoConfiguration {

    @Configuration
    @EntityScan(basePackageClasses = PetServiceApplication.class)
    @EnableJpaRepositories(
            basePackageClasses = PetServiceApplication.class,
            repositoryBaseClass = CustomJpaRepositoryImpl.class,
            repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class
    )
    public static class JpaConfiguration {
    }
}


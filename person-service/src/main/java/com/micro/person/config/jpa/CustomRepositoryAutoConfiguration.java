package com.micro.person.config.jpa;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import com.micro.person.PersonServiceApplication;
import com.micro.person.repository.CustomJpaRepository;
import com.micro.person.repository.CustomJpaRepositoryImpl;
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
    @EntityScan(basePackageClasses = PersonServiceApplication.class)
    @EnableJpaRepositories(
            basePackageClasses = PersonServiceApplication.class,
            repositoryBaseClass = CustomJpaRepositoryImpl.class,
            repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class
    )
    public static class JpaConfiguration {
    }
}

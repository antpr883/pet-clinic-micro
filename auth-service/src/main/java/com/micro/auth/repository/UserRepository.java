package com.micro.auth.repository;

import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import com.micro.auth.data.entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CustomJpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.active = true")
    Optional<UserEntity> findByUsernameAndActiveTrue(@Param("username") String username, EntityGraph entityGraph);

    /**
     * Знаходить користувача за username без Entity Graph (для зворотної сумісності).
     */
    default Optional<UserEntity> findByUsernameAndActiveTrue(String username) {
        return findByUsernameAndActiveTrue(username, null);
    }

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.active = true")
    Optional<UserEntity> findByEmailAndActiveTrue(@Param("email") String email, EntityGraph entityGraph);

    /**
     * Знаходить користувача за email без Entity Graph (для зворотної сумісності).
     */
    default Optional<UserEntity> findByEmailAndActiveTrue(String email) {
        return findByEmailAndActiveTrue(email, null);
    }

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}


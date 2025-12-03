package com.micro.auth.repository;

import com.micro.auth.data.entities.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    @Query("SELECT p FROM PermissionEntity p WHERE p.name = :name AND p.active = true")
    Optional<PermissionEntity> findByNameAndActiveTrue(@Param("name") String name);

    boolean existsByName(String name);
}


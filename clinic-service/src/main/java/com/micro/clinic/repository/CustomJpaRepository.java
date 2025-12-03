package com.micro.clinic.repository;

import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface CustomJpaRepository<T, ID extends Serializable>
        extends JpaRepository<T, ID>, 
                EntityGraphJpaRepository<T, ID>,
                EntityGraphJpaSpecificationExecutor<T> {
    
    /**
     * Soft delete entity (sets active = false).
     * Implementation in CustomJpaRepositoryImpl.
     * Without @Query so Spring Data JPA uses implementation from CustomJpaRepositoryImpl.
     */
    Optional<T> softDelete(@Param("id") ID id, EntityGraph entityGraph);
    
    /**
     * Finds all active entities.
     * Uses @Query so Spring Data JPA doesn't try to create query automatically.
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.active = true")
    List<T> findAllActive();
    
    /**
     * Checks if entity is active by ID.
     * Uses @Query so Spring Data JPA doesn't try to create query automatically.
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT CASE WHEN e.active = true THEN true ELSE false END FROM #{#entityName} e WHERE e.id = :id")
    boolean isActive(@Param("id") ID id);
    
    /**
     * Soft delete entity (sets active = false) - overloaded method without EntityGraph.
     * Implementation in CustomJpaRepositoryImpl.
     */
    default Optional<T> softDelete(ID id) {
        return softDelete(id, null);
    }
    
    /**
     * Soft delete multiple entities.
     * Implementation in CustomJpaRepositoryImpl.
     * @Query is needed so Spring Data JPA doesn't try to create query automatically.
     * Uses SELECT to get count, then executes softDelete in implementation.
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.id IN :ids")
    int softDeleteAll(@Param("ids") List<ID> ids, EntityGraph entityGraph);
    
    /**
     * Soft delete multiple entities - overloaded method without EntityGraph.
     * Implementation in CustomJpaRepositoryImpl.
     */
    default int softDeleteAll(List<ID> ids) {
        return softDeleteAll(ids, null);
    }
}


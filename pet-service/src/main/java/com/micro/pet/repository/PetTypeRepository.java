package com.micro.pet.repository;

import com.micro.pet.data.entities.PetTypeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for PetTypeEntity.
 * 
 * Provides methods for working with pet types:
 * - CRUD operations (via CustomJpaRepository)
 * - Search by type name
 * - RSQL search (via CustomJpaRepository + Specification)
 */
@Repository
public interface PetTypeRepository extends CustomJpaRepository<PetTypeEntity, Long> {

    /**
     * Finds pet type by name (case-insensitive).
     * 
     * @param typeName type name (e.g., "DOG", "CAT")
     * @return Optional with type if found
     * Note: Uses "active" field name (see {@link com.micro.pet.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT pt FROM PetTypeEntity pt WHERE LOWER(pt.typeName) = LOWER(:typeName) AND pt.active = true")
    Optional<PetTypeEntity> findByTypeNameIgnoreCase(@Param("typeName") String typeName);

    /**
     * Checks if an active type with the specified name exists.
     * 
     * @param typeName type name
     * @return true if type exists and is active
     * Note: Uses "active" field name (see {@link com.micro.pet.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT CASE WHEN COUNT(pt) > 0 THEN true ELSE false END FROM PetTypeEntity pt WHERE LOWER(pt.typeName) = LOWER(:typeName) AND pt.active = true")
    boolean existsByTypeNameIgnoreCaseAndActiveTrue(@Param("typeName") String typeName);
}


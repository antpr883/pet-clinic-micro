package com.micro.pet.repository;

import com.micro.pet.data.entities.PetEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PetEntity.
 * 
 * Provides methods for working with pets:
 * - CRUD operations (via CustomJpaRepository)
 * - Search by owner (ownerId)
 * - RSQL search (via CustomJpaRepository + Specification)
 */
@Repository
public interface PetRepository extends CustomJpaRepository<PetEntity, Long> {

    /**
     * Finds all active pets of a specific owner.
     * 
     * @param ownerId Owner ID from Person Service
     * @return list of active pets
     * Note: Uses "active" field name (see {@link com.micro.pet.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT p FROM PetEntity p WHERE p.ownerId = :ownerId AND p.active = true")
    List<PetEntity> findByOwnerIdAndActiveTrue(@Param("ownerId") Long ownerId);

    /**
     * Checks if an active pet with the specified ID exists.
     * 
     * @param id Pet ID
     * @return true if pet exists and is active
     * Note: Uses "active" field name (see {@link com.micro.pet.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PetEntity p WHERE p.id = :id AND p.active = true")
    boolean existsByIdAndActiveTrue(@Param("id") Long id);

    /**
     * Finds pet by ID and ownerId (for validation).
     * 
     * @param id Pet ID
     * @param ownerId Owner ID
     * @return Optional with pet if found
     * Note: Uses "active" field name (see {@link com.micro.pet.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT p FROM PetEntity p WHERE p.id = :id AND p.ownerId = :ownerId AND p.active = true")
    Optional<PetEntity> findByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    /**
     * Count active pets.
     * 
     * @return number of active pets
     * Note: Uses "active" field name (see {@link com.micro.pet.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT COUNT(p) FROM PetEntity p WHERE p.active = true")
    long countActive();

    /**
     * Count pets by type.
     * 
     * @return Map where key = type name, value = number of pets
     */
    @Query("SELECT pt.typeName, COUNT(p) FROM PetEntity p " +
           "JOIN p.type pt WHERE p.active = true GROUP BY pt.typeName")
    List<Object[]> countByTypeGrouped();

    /**
     * Check if pet exists and is active in a single query.
     * Optimized version of existsById() && isActive().
     * 
     * @param id Pet ID
     * @return true if pet exists and is active
     * Note: Uses "active" field name (see {@link com.micro.pet.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM PetEntity p WHERE p.id = :id AND p.active = true")
    boolean existsAndActive(@Param("id") Long id);
}


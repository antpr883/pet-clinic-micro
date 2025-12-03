package com.micro.pet.repository;

import com.micro.pet.data.entities.PetInfoEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for PetInfoEntity.
 * 
 * Provides methods for working with detailed pet information:
 * - CRUD operations (via CustomJpaRepository)
 * - Search by petId
 * - RSQL search (via CustomJpaRepository + Specification)
 */
@Repository
public interface PetInfoRepository extends CustomJpaRepository<PetInfoEntity, Long> {

    /**
     * Finds detailed information by pet ID.
     * 
     * @param petId Pet ID
     * @return Optional with detailed information if found
     * Note: Uses "active" field name (see {@link com.micro.pet.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT pi FROM PetInfoEntity pi WHERE pi.pet.id = :petId AND pi.active = true")
    Optional<PetInfoEntity> findByPetId(@Param("petId") Long petId);
}


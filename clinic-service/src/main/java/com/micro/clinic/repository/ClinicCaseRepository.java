package com.micro.clinic.repository;

import com.micro.clinic.data.entities.ClinicCaseEntity;
import com.micro.clinic.data.entities.enums.ClinicCaseStatus;
import com.micro.clinic.data.entities.enums.CasePriority;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ClinicCaseEntity.
 * 
 * Provides methods for working with clinic cases:
 * - CRUD operations (via CustomJpaRepository)
 * - Search by status, priority, petId, ownerId
 * - RSQL search (via CustomJpaRepository + Specification)
 */
@Repository
public interface ClinicCaseRepository extends CustomJpaRepository<ClinicCaseEntity, Long> {

    /**
     * Finds all active cases by status.
     * 
     * @param status Case status
     * @return list of active cases with specified status
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT c FROM ClinicCaseEntity c WHERE c.status = :status AND c.active = true")
    List<ClinicCaseEntity> findByStatusAndActiveTrue(@Param("status") ClinicCaseStatus status);

    /**
     * Finds all active cases by priority.
     * 
     * @param priority Case priority
     * @return list of active cases with specified priority
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT c FROM ClinicCaseEntity c WHERE c.priority = :priority AND c.active = true")
    List<ClinicCaseEntity> findByPriorityAndActiveTrue(@Param("priority") CasePriority priority);

    /**
     * Finds all active cases by petId.
     * 
     * @param petId Pet ID from Pet Service
     * @return list of active cases for the pet
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT c FROM ClinicCaseEntity c WHERE c.petId = :petId AND c.active = true")
    List<ClinicCaseEntity> findByPetIdAndActiveTrue(@Param("petId") Long petId);

    /**
     * Finds all active cases by ownerId.
     * 
     * @param ownerId Owner ID from Person Service
     * @return list of active cases for the owner
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT c FROM ClinicCaseEntity c WHERE c.ownerId = :ownerId AND c.active = true")
    List<ClinicCaseEntity> findByOwnerIdAndActiveTrue(@Param("ownerId") Long ownerId);

    /**
     * Finds all active cases by assigned vet.
     * 
     * @param vetId Vet ID from Person Service
     * @return list of active cases assigned to the vet
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT c FROM ClinicCaseEntity c WHERE c.assignedVetId = :vetId AND c.active = true")
    List<ClinicCaseEntity> findByAssignedVetIdAndActiveTrue(@Param("vetId") Long vetId);

    /**
     * Count active cases.
     * 
     * @return number of active cases
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT COUNT(c) FROM ClinicCaseEntity c WHERE c.active = true")
    long countActive();

    /**
     * Check if case exists and is active in a single query.
     * Optimized version of existsById() && isActive().
     * 
     * @param id Case ID
     * @return true if case exists and is active
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
           "FROM ClinicCaseEntity c WHERE c.id = :id AND c.active = true")
    boolean existsAndActive(@Param("id") Long id);
}


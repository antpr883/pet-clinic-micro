package com.micro.clinic.repository;

import com.micro.clinic.data.entities.ProcedureEntity;
import com.micro.clinic.data.entities.enums.ProcedureStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ProcedureEntity.
 */
@Repository
public interface ProcedureRepository extends CustomJpaRepository<ProcedureEntity, Long> {

    /**
     * Finds all active procedures for a clinic case.
     * 
     * @param caseId Clinic case ID
     * @return list of active procedures
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT p FROM ProcedureEntity p WHERE p.clinicCase.id = :caseId AND p.active = true")
    List<ProcedureEntity> findByClinicCaseIdAndActiveTrue(@Param("caseId") Long caseId);

    /**
     * Finds all active procedures by status for a clinic case.
     * 
     * @param caseId Clinic case ID
     * @param status Procedure status
     * @return list of active procedures with specified status
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT p FROM ProcedureEntity p WHERE p.clinicCase.id = :caseId AND p.status = :status AND p.active = true")
    List<ProcedureEntity> findByClinicCaseIdAndStatusAndActiveTrue(
            @Param("caseId") Long caseId,
            @Param("status") ProcedureStatus status);
}


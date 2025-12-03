package com.micro.clinic.repository;

import com.micro.clinic.data.entities.DiagnosisEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for DiagnosisEntity.
 */
@Repository
public interface DiagnosisRepository extends CustomJpaRepository<DiagnosisEntity, Long> {

    /**
     * Finds all active diagnoses for a clinic case.
     * 
     * @param caseId Clinic case ID
     * @return list of active diagnoses
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT d FROM DiagnosisEntity d WHERE d.clinicCase.id = :caseId AND d.active = true")
    List<DiagnosisEntity> findByClinicCaseIdAndActiveTrue(@Param("caseId") Long caseId);
}


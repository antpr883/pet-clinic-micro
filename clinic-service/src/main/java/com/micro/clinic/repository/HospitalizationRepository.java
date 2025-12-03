package com.micro.clinic.repository;

import com.micro.clinic.data.entities.HospitalizationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for HospitalizationEntity.
 */
@Repository
public interface HospitalizationRepository extends CustomJpaRepository<HospitalizationEntity, Long> {

    /**
     * Finds active hospitalization for a clinic case.
     * 
     * @param caseId Clinic case ID
     * @return Optional with hospitalization if found
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT h FROM HospitalizationEntity h WHERE h.clinicCase.id = :caseId AND h.active = true")
    Optional<HospitalizationEntity> findByClinicCaseIdAndActiveTrue(@Param("caseId") Long caseId);
}


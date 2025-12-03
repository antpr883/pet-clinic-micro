package com.micro.clinic.repository;

import com.micro.clinic.data.entities.ClinicVisitEntity;
import com.micro.clinic.data.entities.enums.VisitType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ClinicVisitEntity.
 */
@Repository
public interface ClinicVisitRepository extends CustomJpaRepository<ClinicVisitEntity, Long> {

    /**
     * Finds all active visits for a clinic case.
     * 
     * @param caseId Clinic case ID
     * @return list of active visits
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT v FROM ClinicVisitEntity v WHERE v.clinicCase.id = :caseId AND v.active = true ORDER BY v.visitDate DESC")
    List<ClinicVisitEntity> findByClinicCaseIdAndActiveTrue(@Param("caseId") Long caseId);

    /**
     * Finds all active visits by type for a clinic case.
     * 
     * @param caseId Clinic case ID
     * @param visitType Visit type
     * @return list of active visits with specified type
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT v FROM ClinicVisitEntity v WHERE v.clinicCase.id = :caseId AND v.visitType = :visitType AND v.active = true")
    List<ClinicVisitEntity> findByClinicCaseIdAndVisitTypeAndActiveTrue(
            @Param("caseId") Long caseId,
            @Param("visitType") VisitType visitType);
}


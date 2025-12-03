package com.micro.clinic.repository;

import com.micro.clinic.data.entities.DailyCheckEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for DailyCheckEntity.
 */
@Repository
public interface DailyCheckRepository extends CustomJpaRepository<DailyCheckEntity, Long> {

    /**
     * Finds all active daily checks for a hospitalization.
     * 
     * @param hospitalizationId Hospitalization ID
     * @return list of active daily checks ordered by date
     * Note: Uses "active" field name (see {@link com.micro.clinic.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT d FROM DailyCheckEntity d WHERE d.hospitalization.id = :hospitalizationId AND d.active = true ORDER BY d.checkDate DESC")
    List<DailyCheckEntity> findByHospitalizationIdAndActiveTrue(@Param("hospitalizationId") Long hospitalizationId);
}


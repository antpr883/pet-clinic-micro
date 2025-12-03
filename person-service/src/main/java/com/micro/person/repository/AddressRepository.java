package com.micro.person.repository;

import com.micro.person.data.entities.AddressEntity;
import com.micro.person.data.entities.enums.AddressType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends CustomJpaRepository<AddressEntity, Long> {
    
    List<AddressEntity> findByPersonIdAndActiveTrue(Long personId);
    
    List<AddressEntity> findByPersonIdAndAddressTypeAndActiveTrue(
            Long personId, 
            AddressType addressType
    );
    
    /**
     * Перевірити чи існує адреса певного типу для персони (без завантаження сутності).
     * Корисно для оптимізації - не завантажує всю сутність, тільки перевіряє наявність.
     */
    boolean existsByPersonIdAndAddressTypeAndActiveTrue(Long personId, AddressType addressType);
    
    /**
     * Count addresses by person ID.
     * Note: Uses "active" field name (see {@link com.micro.person.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT COUNT(a) FROM AddressEntity a WHERE a.person.id = :personId AND a.active = true")
    long countByPersonId(@Param("personId") Long personId);
    
    /**
     * Find addresses by city.
     * Note: Uses "active" field name (see {@link com.micro.person.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT a FROM AddressEntity a WHERE a.city = :city AND a.active = true")
    List<AddressEntity> findByCityAndActiveTrue(@Param("city") String city);
    
    /**
     * Find addresses by country.
     * Note: Uses "active" field name (see {@link com.micro.person.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT a FROM AddressEntity a WHERE a.country = :country AND a.active = true")
    List<AddressEntity> findByCountryAndActiveTrue(@Param("country") String country);
}


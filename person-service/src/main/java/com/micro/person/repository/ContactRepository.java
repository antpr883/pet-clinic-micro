package com.micro.person.repository;

import com.micro.person.data.entities.ContactEntity;
import com.micro.person.data.entities.enums.ContactType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends CustomJpaRepository<ContactEntity, Long> {
    
    List<ContactEntity> findByPersonIdAndActiveTrue(Long personId);
    
    Optional<ContactEntity> findByPersonIdAndIsPrimaryTrueAndActiveTrue(Long personId);
    
    List<ContactEntity> findByPersonIdAndContactTypeAndActiveTrue(
            Long personId, 
            ContactType contactType
    );
    
    /**
     * Знайти контакт по email (шукає в value з contactType=EMAIL).
     * Note: Uses "active" field name (see {@link com.micro.person.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT c FROM ContactEntity c WHERE c.contactType = 'EMAIL' AND LOWER(c.value) = LOWER(:email) AND c.active = true")
    Optional<ContactEntity> findByEmailIgnoreCaseAndActiveTrue(@Param("email") String email);
    
    /**
     * Знайти контакт по телефону (шукає в value з contactType IN (PHONE, MOBILE)).
     * Note: Uses "active" field name (see {@link com.micro.person.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT c FROM ContactEntity c WHERE c.contactType IN ('PHONE', 'MOBILE') AND c.value = :phone AND c.active = true")
    Optional<ContactEntity> findByPhoneAndActiveTrue(@Param("phone") String phone);
    
    boolean existsByPersonIdAndIsPrimaryTrueAndActiveTrue(Long personId);
    
    /**
     * Count contacts by person ID.
     * Note: Uses "active" field name (see {@link com.micro.person.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT COUNT(c) FROM ContactEntity c WHERE c.person.id = :personId AND c.active = true")
    long countByPersonId(@Param("personId") Long personId);
    
    List<ContactEntity> findByIsPrimaryTrueAndActiveTrue();
    
    /**
     * Пошук email контактів по частині адреси (шукає в value з contactType=EMAIL).
     * Note: Uses "active" field name (see {@link com.micro.person.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT c FROM ContactEntity c WHERE c.contactType = 'EMAIL' AND LOWER(c.value) LIKE LOWER(CONCAT('%', :emailPart, '%')) AND c.active = true")
    List<ContactEntity> findByEmailContainingIgnoreCaseAndActiveTrue(@Param("emailPart") String emailPart);
    
    /**
     * Пошук телефонних контактів по частині номера (шукає в value з contactType IN (PHONE, MOBILE)).
     * Note: Uses "active" field name (see {@link com.micro.person.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT c FROM ContactEntity c WHERE c.contactType IN ('PHONE', 'MOBILE') AND c.value LIKE CONCAT('%', :phonePart, '%') AND c.active = true")
    List<ContactEntity> findByPhoneContainingAndActiveTrue(@Param("phonePart") String phonePart);
}

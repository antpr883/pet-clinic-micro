package com.micro.person.repository;

import com.micro.person.data.entities.PersonEntity;
import com.micro.person.data.entities.enums.ContactType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends CustomJpaRepository<PersonEntity, Long> {
    
    Optional<PersonEntity> findByFirstNameAndLastNameAndActiveTrue(
            String firstName, 
            String lastName
    );
    
    List<PersonEntity> findByFirstNameContainingIgnoreCaseAndActiveTrue(String firstNamePart);
    
    List<PersonEntity> findByLastNameContainingIgnoreCaseAndActiveTrue(String lastNamePart);
    
    @Query("""
        SELECT DISTINCT p FROM PersonEntity p 
        JOIN p.contacts c 
        WHERE c.contactType = :contactType 
        AND c.active = true
        AND p.active = true
    """)
    List<PersonEntity> findByContactType(@Param("contactType") ContactType contactType);
    
    /**
     * Count active persons.
     * Note: Uses "active" field name (see {@link com.micro.person.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("SELECT COUNT(p) FROM PersonEntity p WHERE p.active = true")
    long countActive();
    
    @Query("""
        SELECT p FROM PersonEntity p 
        WHERE p.active = true 
        AND (
            LOWER(p.firstName) LIKE LOWER(CONCAT('%', :namePart, '%'))
            OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :namePart, '%'))
        )
    """)
    List<PersonEntity> searchByName(@Param("namePart") String namePart);
    
    /**
     * Check if person exists and is active in a single query.
     * Optimized version of existsById() && isActive().
     * Note: Uses "active" field name (see {@link com.micro.person.data.constants.EntityConstants#ACTIVE_FIELD})
     */
    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END 
        FROM PersonEntity p 
        WHERE p.id = :id AND p.active = true
    """)
    boolean existsAndActive(@Param("id") Long id);
}

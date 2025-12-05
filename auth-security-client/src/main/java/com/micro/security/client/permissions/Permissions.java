package com.micro.security.client.permissions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Централізована система permissions для всіх мікросервісів.
 *
 * BEST PRACTICE: Всі permissions визначені в одному місці для узгодженості.
 *
 * Формат naming: {SERVICE}_{RESOURCE}_{ACTION}
 * - SERVICE: PERSON, PET, CLINIC
 * - RESOURCE: READ, WRITE, CASE, DIAGNOSTICS, HOSPITALIZATION
 * - ACTION: READ, WRITE
 *
 * Використання:
 * ```java
 * @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
 * public PersonDto getPerson(Long id) { ... }
 * ```
 *
 * ВАЖЛИВО: Ці permissions повинні відповідати permissions в auth-service БД.
 * При додаванні нового permission:
 * 1. Додати константу тут
 * 2. Додати в БД (Liquibase migration)
 * 3. Призначити ролям
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Permissions {

    // ========== PERSON SERVICE PERMISSIONS ==========
    
    /**
     * Дозволяє читати дані про персон (власників).
     */
    public static final String PERSON_READ = "PERSON_READ";

    /**
     * Дозволяє створювати та оновлювати дані про персон.
     */
    public static final String PERSON_WRITE = "PERSON_WRITE";

    // ========== PET SERVICE PERMISSIONS ==========
    
    /**
     * Дозволяє читати дані про тварин.
     */
    public static final String PET_READ = "PET_READ";

    /**
     * Дозволяє створювати та оновлювати дані про тварин.
     */
    public static final String PET_WRITE = "PET_WRITE";

    // ========== CLINIC SERVICE PERMISSIONS ==========
    
    /**
     * Дозволяє читати дані про клінічні випадки (clinic cases).
     */
    public static final String CLINIC_CASE_READ = "CLINIC_CASE_READ";

    /**
     * Дозволяє створювати та оновлювати клінічні випадки.
     */
    public static final String CLINIC_CASE_WRITE = "CLINIC_CASE_WRITE";

    /**
     * Дозволяє створювати та оновлювати діагностику.
     */
    public static final String CLINIC_DIAGNOSTICS_WRITE = "CLINIC_DIAGNOSTICS_WRITE";

    /**
     * Дозволяє створювати та оновлювати госпіталізації.
     */
    public static final String CLINIC_HOSPITALIZATION_WRITE = "CLINIC_HOSPITALIZATION_WRITE";
}


--liquibase formatted sql

--changeset clinic_user:009-migrate-home-contacts-to-addresses context:dev,prod,test
--comment: Migration of addresses from contacts (contactType=HOME) to separate addresses table

-- Migrate addresses from contacts to addresses
-- Assume that value contains address in text format
INSERT INTO person_schema.addresses (
    person_id, 
    address_type, 
    city, 
    street, 
    description, 
    active, 
    created_at, 
    updated_at, 
    created_by, 
    modified_by
)
SELECT 
    person_id,
    'HOME'::VARCHAR(30) as address_type,
    -- Try to extract city from value (if format "street ..., city Kyiv")
    CASE 
        WHEN value LIKE '%city %' THEN 
            SUBSTRING(value FROM '%city ([^,]+)%')
        WHEN value LIKE '%City%' THEN 
            SUBSTRING(value FROM '%City ([^,]+)%')
        ELSE NULL
    END as city,
    -- Full address as street
    value as street,
    label as description,
    active,
    created_at,
    updated_at,
    created_by,
    modified_by
FROM person_schema.contacts
WHERE contact_type = 'OTHER' 
  AND value IS NOT NULL 
  AND (label LIKE '%address%' OR label IS NULL OR label = '' OR value NOT LIKE '%@%');

-- Delete addresses from contacts (they are now in addresses)
DELETE FROM person_schema.contacts
WHERE contact_type = 'OTHER' 
  AND (label LIKE '%address%' OR label IS NULL OR label = '' OR value NOT LIKE '%@%');

-- Seed addresses for dev (if table is empty after migration)
--changeset clinic_user:009-seed-addresses context:dev
--preconditions onFail:CONTINUE onError:CONTINUE
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM person_schema.addresses
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM person_schema.persons WHERE id = 1
INSERT INTO person_schema.addresses (id, person_id, address_type, country, city, street, building, apartment, postal_code, description, active, created_at, updated_at, created_by, modified_by)
VALUES 
-- Addresses for Person 1 (Ivan Petrenko)
(1, 1, 'HOME', 'Ukraine', 'Kyiv', 'Khreshchatyk St', '1', '5', '01001', 'Home address', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
-- Addresses for Person 3 (Oleksandr Shevchenko - Veterinarian)
(2, 3, 'HOME', 'Ukraine', 'Kyiv', 'Veterinary St', '15', NULL, '01001', 'Home address', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
-- Addresses for Person 5 (Dmitro Bondarenko)
(3, 5, 'HOME', 'Ukraine', 'Lviv', 'Rabbit St', '7', '12', '79000', 'Home address', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (id) DO NOTHING;

-- Reset sequence
SELECT setval('person_schema.addresses_id_seq', (SELECT MAX(id) FROM person_schema.addresses));

--rollback
-- INSERT INTO person_schema.contacts (person_id, contact_type, value, label, is_primary, active, created_at, updated_at, created_by, modified_by)
-- SELECT person_id, 'HOME', COALESCE(street, city, description), description, false, active, created_at, updated_at, created_by, modified_by
-- FROM person_schema.addresses WHERE address_type = 'HOME';
-- DELETE FROM person_schema.addresses WHERE address_type = 'HOME';


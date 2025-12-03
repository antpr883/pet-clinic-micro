--liquibase formatted sql

-- =========================================
-- ADD PERSON_ROLE FIELD TO PERSONS TABLE
-- =========================================
-- 
-- Adds person_role field for storing person role in the system.
-- Possible values: VETERINARIAN, RECEPTIONIST, OWNER.
-- Default value: OWNER.

--changeset clinic_user:004-add-person-role context:dev,prod,test
ALTER TABLE person_schema.persons 
ADD COLUMN person_role VARCHAR(20);

-- Default value for existing records
UPDATE person_schema.persons 
SET person_role = 'OWNER' 
WHERE person_role IS NULL;

-- Make NOT NULL after update
ALTER TABLE person_schema.persons 
ALTER COLUMN person_role SET NOT NULL;

-- Index for searching by roles
CREATE INDEX idx_person_role ON person_schema.persons(person_role);

--rollback DROP INDEX IF EXISTS person_schema.idx_person_role;
--rollback ALTER TABLE person_schema.persons DROP COLUMN IF EXISTS person_role;

--changeset clinic_user:004-add-person-role-comment context:dev,prod,test
COMMENT ON COLUMN person_schema.persons.person_role IS 'Person role in the system (VETERINARIAN, RECEPTIONIST, OWNER)';

--rollback COMMENT ON COLUMN person_schema.persons.person_role IS NULL;


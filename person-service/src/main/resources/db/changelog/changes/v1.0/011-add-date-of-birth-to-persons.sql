--liquibase formatted sql

-- =========================================
-- ADD DATE_OF_BIRTH FIELD TO PERSONS TABLE
-- =========================================
-- 
-- Adds date_of_birth column for storing person birth date.
-- Field is optional (nullable), as existing records may not have this information.

--changeset clinic_user:011-add-date-of-birth-to-persons context:dev,prod,test
ALTER TABLE person_schema.persons 
ADD COLUMN date_of_birth DATE;

--rollback ALTER TABLE person_schema.persons DROP COLUMN date_of_birth;

--changeset clinic_user:011-add-date-of-birth-comment context:dev,prod,test
COMMENT ON COLUMN person_schema.persons.date_of_birth IS 'Person birth date';

--rollback COMMENT ON COLUMN person_schema.persons.date_of_birth IS NULL;


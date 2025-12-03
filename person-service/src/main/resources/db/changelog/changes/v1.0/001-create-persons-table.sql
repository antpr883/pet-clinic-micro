--liquibase formatted sql

-- =========================================
-- CREATE PERSONS TABLE
-- =========================================
-- 
-- Table for storing persons (veterinarians, pet owners, etc.).
-- Contains basic information: first name, last name, middle name, notes.
-- Contacts are stored in a separate contacts table (One-to-Many relationship).

--changeset clinic_user:001-create-persons-table context:dev,prod,test
CREATE TABLE person_schema.persons (
    -- Primary Key
    id BIGSERIAL PRIMARY KEY,

    -- Personal Information
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    notes VARCHAR(1000),

    -- Active Status
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit Fields
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--rollback DROP TABLE person_schema.persons CASCADE;

--changeset clinic_user:001-create-persons-comment context:dev,prod,test
COMMENT ON TABLE person_schema.persons IS 'Persons table (veterinarians, pet owners)';
COMMENT ON COLUMN person_schema.persons.id IS 'Primary key';
COMMENT ON COLUMN person_schema.persons.first_name IS 'First name';
COMMENT ON COLUMN person_schema.persons.last_name IS 'Last name';
COMMENT ON COLUMN person_schema.persons.middle_name IS 'Middle name';
COMMENT ON COLUMN person_schema.persons.notes IS 'Additional information about the person';
COMMENT ON COLUMN person_schema.persons.active IS 'Whether the person is active';

--rollback DROP TABLE IF EXISTS person_schema.persons CASCADE;


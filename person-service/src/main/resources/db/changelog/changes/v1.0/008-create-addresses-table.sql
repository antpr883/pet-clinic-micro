--liquibase formatted sql

-- =========================================
-- CREATE ADDRESSES TABLE
-- =========================================
--
-- Table for storing person addresses.
-- One person can have many addresses of different types:
-- HOME, WORK, TEMPORARY, MAILING, OTHER

--changeset clinic_user:008-create-addresses-table context:dev,prod,test
CREATE TABLE person_schema.addresses (
    -- Primary Key
    id BIGSERIAL PRIMARY KEY,

    -- Foreign Key to Persons
    person_id BIGINT NOT NULL,

    -- Address Type (ENUM stored as VARCHAR)
    address_type VARCHAR(30) NOT NULL,

    -- Address Information
    country VARCHAR(100),
    city VARCHAR(100),
    street VARCHAR(255),
    building VARCHAR(50),
    apartment VARCHAR(50),
    postal_code VARCHAR(20),
    description VARCHAR(500),

    -- Active Status
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit Fields
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key Constraint
    CONSTRAINT fk_address_person 
        FOREIGN KEY (person_id) 
        REFERENCES person_schema.persons(id) 
        ON DELETE CASCADE
);

--rollback DROP TABLE person_schema.addresses CASCADE;

--changeset clinic_user:008-create-addresses-comments context:dev,prod,test
COMMENT ON TABLE person_schema.addresses IS 'Person addresses';
COMMENT ON COLUMN person_schema.addresses.person_id IS 'Link to person';
COMMENT ON COLUMN person_schema.addresses.address_type IS 'Address type: HOME, WORK, TEMPORARY, MAILING, OTHER';
COMMENT ON COLUMN person_schema.addresses.description IS 'Address description (e.g., "office #2", "dacha")';

--rollback DROP TABLE IF EXISTS person_schema.addresses CASCADE;


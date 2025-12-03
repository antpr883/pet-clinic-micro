--liquibase formatted sql

-- =========================================
-- CREATE CONTACTS TABLE
-- =========================================
--
-- Table for storing person contact information.
-- One person can have many contacts of different types:
-- EMAIL, PHONE, MOBILE, TELEGRAM, WHATSAPP, LINKEDIN, OTHER

--changeset clinic_user:002-create-contacts-table context:dev,prod,test
--validCheckSum: 9:a54600efec02b09ec28d808863b7d45d,9:0d81330598a06db9ac76cb6a5d7eeb4c
CREATE TABLE person_schema.contacts (
    -- Primary Key
    id BIGSERIAL PRIMARY KEY,

    -- Foreign Key to Persons
    person_id BIGINT NOT NULL,

    -- Contact Type (ENUM stored as VARCHAR)
    contact_type VARCHAR(30) NOT NULL,

    -- Contact Value (TEXT) - stores contact value (email, phone, etc.)
    value TEXT NOT NULL,

    -- Label (VARCHAR) - contact description/name (e.g., "work", "personal")
    label VARCHAR(100),

    -- Primary Contact Flag
    is_primary BOOLEAN NOT NULL DEFAULT false,

    -- Active Status
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit Fields
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key Constraint
    CONSTRAINT fk_contact_person 
        FOREIGN KEY (person_id) 
        REFERENCES person_schema.persons(id) 
        ON DELETE CASCADE
);

--rollback DROP TABLE person_schema.contacts CASCADE;

--changeset clinic_user:002-create-contacts-comment context:dev,prod,test
--validCheckSum: 9:d64c518cbd73249d21c75411f7675489,9:cd4dfd5ca2d89c78d0060627977b102f
COMMENT ON TABLE person_schema.contacts IS 'Person contact information';
COMMENT ON COLUMN person_schema.contacts.person_id IS 'Link to person';
COMMENT ON COLUMN person_schema.contacts.contact_type IS 'Contact type: EMAIL, PHONE, MOBILE, TELEGRAM, WHATSAPP, LINKEDIN, OTHER';
COMMENT ON COLUMN person_schema.contacts.value IS 'Contact value (email address, phone number, etc.)';
COMMENT ON COLUMN person_schema.contacts.label IS 'Contact description/name (e.g., "work", "personal")';
COMMENT ON COLUMN person_schema.contacts.is_primary IS 'Whether this is the primary contact of this type';

--rollback DROP TABLE IF EXISTS person_schema.contacts CASCADE;


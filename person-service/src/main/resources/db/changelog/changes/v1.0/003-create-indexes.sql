--liquibase formatted sql

-- =========================================
-- CREATE INDEXES
-- =========================================

-- ========== PERSONS INDEXES ==========

--changeset clinic_user:003-create-persons-indexes context:dev,prod,test
-- Index on first_name for search
CREATE INDEX idx_person_first_name ON person_schema.persons(first_name);

-- Index on last_name for search
CREATE INDEX idx_person_last_name ON person_schema.persons(last_name);

-- Index on active status for filtering
CREATE INDEX idx_person_active ON person_schema.persons(active);

-- Composite index for full name search
CREATE INDEX idx_person_full_name ON person_schema.persons(first_name, last_name);

--rollback DROP INDEX IF EXISTS person_schema.idx_person_first_name;
--rollback DROP INDEX IF EXISTS person_schema.idx_person_last_name;
--rollback DROP INDEX IF EXISTS person_schema.idx_person_active;
--rollback DROP INDEX IF EXISTS person_schema.idx_person_full_name;


-- ========== CONTACTS INDEXES ==========

--changeset clinic_user:003-create-contacts-indexes context:dev,prod,test
-- Index on person_id for JOIN operations
CREATE INDEX idx_contact_person_id ON person_schema.contacts(person_id);

-- Index on contact_type for filtering
CREATE INDEX idx_contact_type ON person_schema.contacts(contact_type);

-- Index on value for search (instead of separate email/phone, we use value)
CREATE INDEX idx_contact_value ON person_schema.contacts(value) WHERE value IS NOT NULL;

-- Composite index for finding person's contacts by type
CREATE INDEX idx_contact_person_type ON person_schema.contacts(person_id, contact_type);

-- Index for finding primary contacts
CREATE INDEX idx_contact_is_primary ON person_schema.contacts(is_primary);

--rollback DROP INDEX IF EXISTS person_schema.idx_contact_person_id;
--rollback DROP INDEX IF EXISTS person_schema.idx_contact_type;
--rollback DROP INDEX IF EXISTS person_schema.idx_contact_value;
--rollback DROP INDEX IF EXISTS person_schema.idx_contact_person_type;
--rollback DROP INDEX IF EXISTS person_schema.idx_contact_is_primary;


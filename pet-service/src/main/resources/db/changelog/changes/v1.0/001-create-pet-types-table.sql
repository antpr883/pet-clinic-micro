--liquibase formatted sql

-- =========================================
-- CREATE PET_TYPES TABLE
-- =========================================
-- 
-- Table for storing pet types (cat, dog, exotic, etc.).
-- Reference table for pet type classification.

--changeset clinic_user:001-create-pet-types-table context:dev,prod,test
CREATE TABLE pet_schema.pet_types (
    -- Primary Key
    id BIGSERIAL PRIMARY KEY,

    -- Type Information
    type_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    exotic BOOLEAN NOT NULL DEFAULT false,

    -- Active Status
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit Fields
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--rollback DROP TABLE pet_schema.pet_types CASCADE;

--changeset clinic_user:001-create-pet-types-comment context:dev,prod,test
COMMENT ON TABLE pet_schema.pet_types IS 'Pet types table (cat, dog, exotic, etc.)';
COMMENT ON COLUMN pet_schema.pet_types.id IS 'Primary key';
COMMENT ON COLUMN pet_schema.pet_types.type_name IS 'Type name (DOG, CAT, PARROT, IGUANA)';
COMMENT ON COLUMN pet_schema.pet_types.description IS 'Pet type description';
COMMENT ON COLUMN pet_schema.pet_types.exotic IS 'Whether the pet is exotic';
COMMENT ON COLUMN pet_schema.pet_types.active IS 'Whether the type is active';

--rollback DROP TABLE IF EXISTS pet_schema.pet_types CASCADE;


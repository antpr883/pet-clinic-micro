--liquibase formatted sql

-- =========================================
-- CREATE PET_INFO TABLE
-- =========================================
-- 
-- Table for storing detailed pet information in JSONB format.
-- Allows storing arbitrary characteristics (weight, breed, favorite food, etc.).

--changeset clinic_user:003-create-pet-info-table context:dev,prod,test
CREATE TABLE pet_schema.pet_info (
    -- Primary Key
    id BIGSERIAL PRIMARY KEY,

    -- Pet Reference
    pet_id BIGINT NOT NULL UNIQUE,

    -- JSONB Details
    details JSONB NOT NULL,

    -- Active Status
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit Fields
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_pet_info_pet 
        FOREIGN KEY (pet_id) 
        REFERENCES pet_schema.pets(id) 
        ON DELETE CASCADE
);

--rollback DROP TABLE pet_schema.pet_info CASCADE;

--changeset clinic_user:003-create-pet-info-comment context:dev,prod,test
COMMENT ON TABLE pet_schema.pet_info IS 'Detailed pet information table (JSONB)';
COMMENT ON COLUMN pet_schema.pet_info.id IS 'Primary key';
COMMENT ON COLUMN pet_schema.pet_info.pet_id IS 'Pet ID';
COMMENT ON COLUMN pet_schema.pet_info.details IS 'Detailed information in JSONB format (weight, breed, favorite food, etc.)';
COMMENT ON COLUMN pet_schema.pet_info.active IS 'Whether the information is active';

--rollback DROP TABLE IF EXISTS pet_schema.pet_info CASCADE;


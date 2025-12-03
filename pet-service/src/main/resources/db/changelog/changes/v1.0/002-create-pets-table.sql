--liquibase formatted sql

-- =========================================
-- CREATE PETS TABLE
-- =========================================
-- 
-- Table for storing pets.
-- Contains basic information: name, birth date, owner, type, photo.
-- Detailed information is stored in a separate pet_info table (One-to-One relationship).

--changeset clinic_user:002-create-pets-table context:dev,prod,test
CREATE TABLE pet_schema.pets (
    -- Primary Key
    id BIGSERIAL PRIMARY KEY,

    -- Pet Information
    name VARCHAR(50) NOT NULL,
    birth_date DATE,
    owner_id BIGINT NOT NULL,
    type_id BIGINT NOT NULL,
    photo_url VARCHAR(255),

    -- Active Status
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit Fields
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_pet_type 
        FOREIGN KEY (type_id) 
        REFERENCES pet_schema.pet_types(id) 
        ON DELETE RESTRICT
);

--rollback DROP TABLE pet_schema.pets CASCADE;

--changeset clinic_user:002-create-pets-comment context:dev,prod,test
COMMENT ON TABLE pet_schema.pets IS 'Pets table';
COMMENT ON COLUMN pet_schema.pets.id IS 'Primary key';
COMMENT ON COLUMN pet_schema.pets.name IS 'Pet name';
COMMENT ON COLUMN pet_schema.pets.birth_date IS 'Pet birth date';
COMMENT ON COLUMN pet_schema.pets.owner_id IS 'Owner ID from Person Service (no FK - microservice architecture)';
COMMENT ON COLUMN pet_schema.pets.type_id IS 'Pet type ID';
COMMENT ON COLUMN pet_schema.pets.photo_url IS 'Pet photo URL';
COMMENT ON COLUMN pet_schema.pets.active IS 'Whether the pet is active';

--rollback DROP TABLE IF EXISTS pet_schema.pets CASCADE;


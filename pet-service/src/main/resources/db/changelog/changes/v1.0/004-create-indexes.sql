--liquibase formatted sql

-- =========================================
-- CREATE INDEXES
-- =========================================
-- 
-- Creates indexes for query optimization.

--changeset clinic_user:004-create-indexes context:dev,prod,test

-- Pet Types Indexes
CREATE INDEX IF NOT EXISTS idx_pet_type_name ON pet_schema.pet_types(type_name);
CREATE INDEX IF NOT EXISTS idx_pet_type_active ON pet_schema.pet_types(active);

-- Pets Indexes
CREATE INDEX IF NOT EXISTS idx_pet_name ON pet_schema.pets(name);
CREATE INDEX IF NOT EXISTS idx_pet_owner_id ON pet_schema.pets(owner_id);
CREATE INDEX IF NOT EXISTS idx_pet_type_id ON pet_schema.pets(type_id);
CREATE INDEX IF NOT EXISTS idx_pet_active ON pet_schema.pets(active);
CREATE INDEX IF NOT EXISTS idx_pet_birth_date ON pet_schema.pets(birth_date);

-- Pet Info Indexes
CREATE INDEX IF NOT EXISTS idx_pet_info_pet_id ON pet_schema.pet_info(pet_id);
CREATE INDEX IF NOT EXISTS idx_pet_info_active ON pet_schema.pet_info(active);

-- GIN Index for JSONB (for searching in JSON fields)
CREATE INDEX IF NOT EXISTS idx_pet_info_json_gin ON pet_schema.pet_info USING gin(details);

--rollback DROP INDEX IF EXISTS pet_schema.idx_pet_type_name;
--rollback DROP INDEX IF EXISTS pet_schema.idx_pet_type_active;
--rollback DROP INDEX IF EXISTS pet_schema.idx_pet_name;
--rollback DROP INDEX IF EXISTS pet_schema.idx_pet_owner_id;
--rollback DROP INDEX IF EXISTS pet_schema.idx_pet_type_id;
--rollback DROP INDEX IF EXISTS pet_schema.idx_pet_active;
--rollback DROP INDEX IF EXISTS pet_schema.idx_pet_birth_date;
--rollback DROP INDEX IF EXISTS pet_schema.idx_pet_info_pet_id;
--rollback DROP INDEX IF EXISTS pet_schema.idx_pet_info_active;
--rollback DROP INDEX IF EXISTS pet_schema.idx_pet_info_json_gin;


--liquibase formatted sql

--changeset author:auth-service:003-create-permissions-table labels:v1.0 context:all
--comment: Create permissions table

CREATE TABLE IF NOT EXISTS auth_schema.permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT true,
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_schema.permissions IS 'Permissions table - granular permissions (PERSON_READ, PET_WRITE, etc.)';
COMMENT ON COLUMN auth_schema.permissions.name IS 'Permission name (e.g., PERSON_READ, CLINIC_CASE_WRITE)';
COMMENT ON COLUMN auth_schema.permissions.active IS 'Soft delete flag';


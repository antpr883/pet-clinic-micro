--liquibase formatted sql

--changeset author:auth-service:002-create-roles-table labels:v1.0 context:all
--comment: Create roles table

CREATE TABLE IF NOT EXISTS auth_schema.roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT true,
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_schema.roles IS 'Roles table - ADMIN, VET, RECEPTIONIST, OWNER, ORCHESTRATOR';
COMMENT ON COLUMN auth_schema.roles.name IS 'Role name (e.g., ADMIN, VET)';
COMMENT ON COLUMN auth_schema.roles.active IS 'Soft delete flag';


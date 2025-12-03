--liquibase formatted sql

--changeset author:auth-service:001-create-users-table labels:v1.0 context:all
--comment: Create users table

CREATE TABLE IF NOT EXISTS auth_schema.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    password_salt VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_schema.users IS 'Users table - stores user credentials and basic info';
COMMENT ON COLUMN auth_schema.users.username IS 'Unique username for login';
COMMENT ON COLUMN auth_schema.users.password_hash IS 'SHA-256 hashed password with salt';
COMMENT ON COLUMN auth_schema.users.password_salt IS 'Salt used for password hashing';
COMMENT ON COLUMN auth_schema.users.active IS 'Soft delete flag';


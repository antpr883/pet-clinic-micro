--liquibase formatted sql

-- =========================================
-- CREATE AUTH_SCHEMA
-- =========================================
-- 
-- Creates schema for storing all authentication and authorization tables.
-- Executed first before all other migrations.

--changeset clinic_user:000-create-schema context:dev,prod,test
CREATE SCHEMA IF NOT EXISTS auth_schema;

COMMENT ON SCHEMA auth_schema IS 'Schema for Auth Service - users, roles, permissions, refresh tokens';

-- Grant privileges to clinic_user
GRANT ALL PRIVILEGES ON SCHEMA auth_schema TO clinic_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA auth_schema TO clinic_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA auth_schema TO clinic_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA auth_schema GRANT ALL ON TABLES TO clinic_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA auth_schema GRANT ALL ON SEQUENCES TO clinic_user;

--rollback DROP SCHEMA IF EXISTS auth_schema CASCADE;


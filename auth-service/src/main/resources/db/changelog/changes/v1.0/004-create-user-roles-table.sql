--liquibase formatted sql

--changeset author:auth-service:004-create-user-roles-table labels:v1.0 context:all
--comment: Create user_roles junction table

CREATE TABLE IF NOT EXISTS auth_schema.user_roles (
    user_id BIGINT NOT NULL REFERENCES auth_schema.users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES auth_schema.roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE auth_schema.user_roles IS 'Many-to-many relationship between users and roles';


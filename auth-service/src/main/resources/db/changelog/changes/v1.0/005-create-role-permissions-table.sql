--liquibase formatted sql

--changeset author:auth-service:005-create-role-permissions-table labels:v1.0 context:all
--comment: Create role_permissions junction table

CREATE TABLE IF NOT EXISTS auth_schema.role_permissions (
    role_id BIGINT NOT NULL REFERENCES auth_schema.roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES auth_schema.permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

COMMENT ON TABLE auth_schema.role_permissions IS 'Many-to-many relationship between roles and permissions';


--liquibase formatted sql

--changeset author:auth-service:007-create-indexes labels:v1.0 context:all
--comment: Create indexes for performance

CREATE INDEX IF NOT EXISTS idx_users_username ON auth_schema.users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON auth_schema.users(email);
CREATE INDEX IF NOT EXISTS idx_users_active ON auth_schema.users(active);
CREATE INDEX IF NOT EXISTS idx_roles_name ON auth_schema.roles(name);
CREATE INDEX IF NOT EXISTS idx_permissions_name ON auth_schema.permissions(name);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token_id ON auth_schema.refresh_tokens(token_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON auth_schema.refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at ON auth_schema.refresh_tokens(expires_at);


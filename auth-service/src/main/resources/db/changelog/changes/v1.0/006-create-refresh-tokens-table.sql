--liquibase formatted sql

--changeset author:auth-service:006-create-refresh-tokens-table labels:v1.0 context:all
--comment: Create refresh_tokens table

CREATE TABLE IF NOT EXISTS auth_schema.refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES auth_schema.users(id) ON DELETE CASCADE,
    token_id VARCHAR(100) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    client_id VARCHAR(100),
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_schema.refresh_tokens IS 'Refresh tokens for JWT token rotation';
COMMENT ON COLUMN auth_schema.refresh_tokens.token_id IS 'JWT ID (jti) from refresh token';
COMMENT ON COLUMN auth_schema.refresh_tokens.revoked IS 'Flag to revoke token on logout';


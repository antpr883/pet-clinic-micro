--liquibase formatted sql

--changeset author:auth-service:009-seed-admin-user labels:v1.0 context:all
--comment: Seed default admin user (password: admin123)

-- Password: admin123
-- Hash: SHA-256 with 1024 iterations
-- Salt: f478357b4ab526b26a0fc910e2ab5e6a03469547576547f772aa30253a3e7cd1
-- Hash: bb4209d06328aa3001604db4dc4d50a7f82f9997f89ab1d70ac0786d62ff0293
-- 
-- ⚠️ ВАЖЛИВО: Це реальні дані для розробки!
-- Для генерації нового hash використайте:
-- PasswordHashGenerator.main() або PasswordService
-- 
-- Генерація: mvn test-compile exec:java -Dexec.mainClass="com.micro.auth.util.PasswordHashGenerator" -Dexec.classpathScope=test

INSERT INTO auth_schema.users (username, password_hash, password_salt, email, active, created_by, modified_by)
VALUES (
    'admin',
    'bb4209d06328aa3001604db4dc4d50a7f82f9997f89ab1d70ac0786d62ff0293', -- Hash for password: admin123
    'f478357b4ab526b26a0fc910e2ab5e6a03469547576547f772aa30253a3e7cd1', -- Salt
    'admin@petclinic.local',
    true,
    'system',
    'system'
)
ON CONFLICT (username) DO UPDATE SET
    password_hash = EXCLUDED.password_hash,
    password_salt = EXCLUDED.password_salt,
    active = true;

-- Assign ADMIN role to admin user
INSERT INTO auth_schema.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM auth_schema.users u, auth_schema.roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

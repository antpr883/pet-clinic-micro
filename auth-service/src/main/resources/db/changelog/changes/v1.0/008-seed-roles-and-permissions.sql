--liquibase formatted sql

--changeset author:auth-service:008-seed-roles-and-permissions labels:v1.0 context:all
--comment: Seed initial roles and permissions

-- Insert Permissions
INSERT INTO auth_schema.permissions (name, active, created_by, modified_by) VALUES
    ('PERSON_READ', true, 'system', 'system'),
    ('PERSON_WRITE', true, 'system', 'system'),
    ('PET_READ', true, 'system', 'system'),
    ('PET_WRITE', true, 'system', 'system'),
    ('CLINIC_CASE_READ', true, 'system', 'system'),
    ('CLINIC_CASE_WRITE', true, 'system', 'system'),
    ('CLINIC_DIAGNOSTICS_WRITE', true, 'system', 'system'),
    ('CLINIC_HOSPITALIZATION_WRITE', true, 'system', 'system')
ON CONFLICT (name) DO NOTHING;

-- Insert Roles
INSERT INTO auth_schema.roles (name, active, created_by, modified_by) VALUES
    ('ADMIN', true, 'system', 'system'),
    ('VET', true, 'system', 'system'),
    ('RECEPTIONIST', true, 'system', 'system'),
    ('OWNER', true, 'system', 'system'),
    ('ORCHESTRATOR', true, 'system', 'system')
ON CONFLICT (name) DO NOTHING;

-- Assign Permissions to Roles
-- ADMIN gets all permissions
INSERT INTO auth_schema.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM auth_schema.roles r, auth_schema.permissions p
WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- VET permissions
INSERT INTO auth_schema.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM auth_schema.roles r, auth_schema.permissions p
WHERE r.name = 'VET' AND p.name IN (
    'PET_READ', 'PERSON_READ', 'CLINIC_CASE_WRITE', 
    'CLINIC_DIAGNOSTICS_WRITE', 'CLINIC_HOSPITALIZATION_WRITE'
)
ON CONFLICT DO NOTHING;

-- RECEPTIONIST permissions
INSERT INTO auth_schema.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM auth_schema.roles r, auth_schema.permissions p
WHERE r.name = 'RECEPTIONIST' AND p.name IN (
    'PERSON_READ', 'PERSON_WRITE', 'PET_READ', 'PET_WRITE', 'CLINIC_CASE_READ'
)
ON CONFLICT DO NOTHING;

-- OWNER permissions
INSERT INTO auth_schema.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM auth_schema.roles r, auth_schema.permissions p
WHERE r.name = 'OWNER' AND p.name IN (
    'PET_READ', 'CLINIC_CASE_READ'
)
ON CONFLICT DO NOTHING;

-- ORCHESTRATOR permissions (for service-to-service calls)
INSERT INTO auth_schema.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM auth_schema.roles r, auth_schema.permissions p
WHERE r.name = 'ORCHESTRATOR' AND p.name IN (
    'PERSON_READ', 'PET_READ', 'CLINIC_CASE_READ', 'CLINIC_CASE_WRITE'
)
ON CONFLICT DO NOTHING;


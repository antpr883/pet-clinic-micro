--liquibase formatted sql

-- =========================================
-- CREATE CLINIC_SCHEMA
-- =========================================
-- 
-- Creates schema for storing all clinic case, visit, diagnosis, procedure, 
-- hospitalization and daily check tables.
-- Executed first before all other migrations.

--changeset clinic_user:000-create-schema context:dev,prod,test
CREATE SCHEMA IF NOT EXISTS clinic_schema;

--rollback DROP SCHEMA IF EXISTS clinic_schema CASCADE;



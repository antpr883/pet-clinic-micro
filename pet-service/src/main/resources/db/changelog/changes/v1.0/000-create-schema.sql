--liquibase formatted sql

-- =========================================
-- CREATE PET_SCHEMA
-- =========================================
-- 
-- Creates schema for storing all pet tables, pet types and detailed information.
-- Executed first before all other migrations.

--changeset clinic_user:000-create-schema context:dev,prod,test
CREATE SCHEMA IF NOT EXISTS pet_schema;

--rollback DROP SCHEMA IF EXISTS pet_schema CASCADE;


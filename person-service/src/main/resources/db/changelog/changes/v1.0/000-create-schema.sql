--liquibase formatted sql

-- =========================================
-- CREATE PERSON_SCHEMA
-- =========================================
-- 
-- Creates schema for storing all person, contact and address tables.
-- Executed first before all other migrations.

--changeset clinic_user:000-create-schema context:dev,prod,test,test
CREATE SCHEMA IF NOT EXISTS person_schema;

--rollback DROP SCHEMA IF EXISTS person_schema CASCADE;


--liquibase formatted sql

--changeset author:person-service:4 labels:seed-data context:dev
--validCheckSum: 9:004b721902ea68d4f55d0428e7c33be0,9:87934ae2a8546eb82c1850b581c92605
--comment: Seed database with sample persons and contacts for development

-- Insert sample persons
INSERT INTO person_schema.persons (id, first_name, last_name, middle_name, notes, person_role, active, created_at, updated_at, created_by, modified_by)
VALUES 
(1, 'Ivan', 'Petrenko', 'Oleksandrovych', 'Owner of dog Barsik', 'OWNER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, 'Maria', 'Kovalenko', 'Volodymyrivna', 'Owner of cat Murzik', 'OWNER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, 'Oleksandr', 'Shevchenko', 'Ihorovych', 'Veterinarian', 'VETERINARIAN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(4, 'Olena', 'Melnyk', 'Petrivna', 'Receptionist', 'RECEPTIONIST', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(5, 'Dmitro', 'Bondarenko', 'Serhiyovych', 'Owner of rabbit Pukh', 'OWNER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(6, 'Natalia', 'Tkachenko', 'Andriivna', 'Owner of hamster Niki', 'OWNER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Insert sample contacts for persons
-- New structure: contact_type, value, label (without separate email, phone, address fields)
INSERT INTO person_schema.contacts (id, person_id, contact_type, value, label, is_primary, active, created_at, updated_at, created_by, modified_by)
VALUES 
-- Contacts for Person 1 (Ivan Petrenko)
(1, 1, 'EMAIL', 'ivan.petrenko@example.com', 'Personal email', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, 1, 'MOBILE', '+380501234567', 'Mobile phone', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, 1, 'MOBILE', '+380671234567', 'Work phone', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 2 (Maria Kovalenko)
(4, 2, 'EMAIL', 'maria.kovalenko@example.com', 'Personal email', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(5, 2, 'MOBILE', '+380502345678', 'Mobile phone', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 3 (Oleksandr Shevchenko - Veterinarian)
(6, 3, 'EMAIL', 'oleksandr.shevchenko@vetclinic.com', 'Work email', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(7, 3, 'MOBILE', '+380503456789', 'Mobile phone', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 4 (Olena Melnyk - Receptionist)
(8, 4, 'EMAIL', 'olena.melnyk@vetclinic.com', 'Work email', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(9, 4, 'MOBILE', '+380504567890', 'Mobile phone', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 5 (Dmitro Bondarenko)
(10, 5, 'EMAIL', 'dmitro.bondarenko@example.com', 'Personal email', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(11, 5, 'MOBILE', '+380505678901', 'Mobile phone', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 6 (Natalia Tkachenko - inactive)
(12, 6, 'EMAIL', 'natalia.tkachenko@example.com', 'Personal email', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(13, 6, 'MOBILE', '+380506789012', 'Mobile phone', false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Reset sequences to avoid conflicts
SELECT setval('person_schema.persons_id_seq', (SELECT MAX(id) FROM person_schema.persons));
SELECT setval('person_schema.contacts_id_seq', (SELECT MAX(id) FROM person_schema.contacts));


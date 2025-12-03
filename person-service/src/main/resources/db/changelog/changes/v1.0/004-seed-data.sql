--liquibase formatted sql

--changeset author:person-service:4 labels:seed-data context:dev
--comment: Seed database with sample persons and contacts for development

-- Insert sample persons
INSERT INTO person_schema.persons (id, first_name, last_name, middle_name, notes, person_role, active, created_at, updated_at, created_by, modified_by)
VALUES 
(1, 'Іван', 'Петренко', 'Олександрович', 'Власник собаки Барсик', 'OWNER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, 'Марія', 'Коваленко', 'Володимирівна', 'Власниця кота Мурзик', 'OWNER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, 'Олександр', 'Шевченко', 'Ігорович', 'Ветеринар', 'VETERINARIAN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(4, 'Олена', 'Мельник', 'Петрівна', 'Рецепціоніст', 'RECEPTIONIST', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(5, 'Дмитро', 'Бондаренко', 'Сергійович', 'Власник кролика Пух', 'OWNER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(6, 'Наталія', 'Ткаченко', 'Андріївна', 'Власниця хомяка Нікі', 'OWNER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Insert sample contacts for persons
INSERT INTO person_schema.contacts (id, person_id, contact_type, email, phone, address, is_primary, active, created_at, updated_at, created_by, modified_by)
VALUES 
-- Contacts for Person 1 (Іван Петренко)
(1, 1, 'PERSONAL', 'ivan.petrenko@example.com', NULL, NULL, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, 1, 'MOBILE', NULL, '+380501234567', NULL, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, 1, 'MOBILE', NULL, '+380671234567', NULL, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 2 (Марія Коваленко)
(4, 2, 'PERSONAL', 'maria.kovalenko@example.com', NULL, NULL, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(5, 2, 'MOBILE', NULL, '+380502345678', NULL, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 3 (Олександр Шевченко - Ветеринар)
(6, 3, 'WORK', 'oleksandr.shevchenko@vetclinic.com', NULL, NULL, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(7, 3, 'MOBILE', NULL, '+380503456789', NULL, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(8, 3, 'HOME', NULL, NULL, 'вул. Ветеринарна, 15, м. Київ', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 4 (Олена Мельник - Рецепціоніст)
(9, 4, 'WORK', 'olena.melnyk@vetclinic.com', NULL, NULL, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(10, 4, 'MOBILE', NULL, '+380504567890', NULL, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 5 (Дмитро Бондаренко)
(11, 5, 'PERSONAL', 'dmitro.bondarenko@example.com', NULL, NULL, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(12, 5, 'MOBILE', NULL, '+380505678901', NULL, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(13, 5, 'HOME', NULL, NULL, 'вул. Кроликова, 7, м. Львів', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 6 (Наталія Ткаченко - inactive)
(14, 6, 'PERSONAL', 'natalia.tkachenko@example.com', NULL, NULL, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(15, 6, 'MOBILE', NULL, '+380506789012', NULL, false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Reset sequences to avoid conflicts
SELECT setval('person_schema.persons_id_seq', (SELECT MAX(id) FROM person_schema.persons));
SELECT setval('person_schema.contacts_id_seq', (SELECT MAX(id) FROM person_schema.contacts));


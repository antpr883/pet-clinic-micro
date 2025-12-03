-- Seed data for tests
-- This file is loaded after schema creation and before tests run

-- Insert sample persons (6 persons: 5 active, 1 inactive)
INSERT INTO person_schema.persons (id, first_name, last_name, middle_name, notes, person_role, active, created_at, updated_at, created_by, modified_by)
VALUES 
(1, 'Іван', 'Петренко', 'Олександрович', 'Власник собаки Барсик', 'OWNER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, 'Марія', 'Коваленко', 'Володимирівна', 'Власниця кота Мурзик', 'OWNER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, 'Олександр', 'Шевченко', 'Ігорович', 'Ветеринар', 'VETERINARIAN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(4, 'Олена', 'Мельник', 'Петрівна', 'Рецепціоніст', 'RECEPTIONIST', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(5, 'Дмитро', 'Бондаренко', 'Сергійович', 'Власник кролика Пух', 'OWNER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(6, 'Наталія', 'Ткаченко', 'Андріївна', 'Власниця хомяка Нікі', 'OWNER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (id) DO NOTHING;

-- Insert sample contacts for persons
-- Using new structure: contact_type, value, label
INSERT INTO person_schema.contacts (id, person_id, contact_type, value, label, is_primary, active, created_at, updated_at, created_by, modified_by)
VALUES 
-- Contacts for Person 1 (Іван Петренко)
(1, 1, 'EMAIL', 'ivan.petrenko@example.com', 'Personal email', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, 1, 'MOBILE', '+380501234567', 'Mobile phone', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, 1, 'MOBILE', '+380671234567', 'Work phone', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 2 (Марія Коваленко)
(4, 2, 'EMAIL', 'maria.kovalenko@example.com', 'Personal email', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(5, 2, 'MOBILE', '+380502345678', 'Mobile phone', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 3 (Олександр Шевченко - Ветеринар)
(6, 3, 'EMAIL', 'oleksandr.shevchenko@vetclinic.com', 'Work email', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(7, 3, 'MOBILE', '+380503456789', 'Mobile phone', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 4 (Олена Мельник - Рецепціоніст)
(8, 4, 'EMAIL', 'olena.melnyk@vetclinic.com', 'Work email', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(9, 4, 'MOBILE', '+380504567890', 'Mobile phone', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 5 (Дмитро Бондаренко)
(10, 5, 'EMAIL', 'dmitro.bondarenko@example.com', 'Personal email', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(11, 5, 'MOBILE', '+380505678901', 'Mobile phone', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Contacts for Person 6 (Наталія Ткаченко - inactive)
(12, 6, 'EMAIL', 'natalia.tkachenko@example.com', 'Personal email', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(13, 6, 'MOBILE', '+380506789012', 'Mobile phone', false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (id) DO NOTHING;

-- Reset sequences to avoid conflicts
SELECT setval('person_schema.persons_id_seq', (SELECT COALESCE(MAX(id), 1) FROM person_schema.persons));
SELECT setval('person_schema.contacts_id_seq', (SELECT COALESCE(MAX(id), 1) FROM person_schema.contacts));


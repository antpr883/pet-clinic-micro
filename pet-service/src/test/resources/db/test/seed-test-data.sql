-- Seed data for tests
-- This file is loaded after schema creation and before tests run

-- Insert sample pet types (6 types: 6 active)
INSERT INTO pet_schema.pet_types (id, type_name, description, exotic, active, created_at, updated_at, created_by, modified_by)
VALUES 
(1, 'DOG', 'Dog', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, 'CAT', 'Cat', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, 'RABBIT', 'Rabbit', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(4, 'HAMSTER', 'Hamster', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(5, 'PARROT', 'Parrot', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(6, 'IGUANA', 'Iguana', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (id) DO NOTHING;

-- Insert sample pets
-- ownerId = 1: Barsik (active), Rex (active) = 2 pets
-- ownerId = 2: Murzik (active), Niki (inactive) = 1 active pet
-- ownerId = 5: Pukh (active) = 1 pet
-- Total: 4 active pets, 1 inactive
INSERT INTO pet_schema.pets (id, name, birth_date, owner_id, type_id, photo_url, active, created_at, updated_at, created_by, modified_by)
VALUES 
(1, 'Barsik', '2020-03-15', 1, 1, 'https://example.com/photos/barsik.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, 'Murzik', '2019-07-20', 2, 2, 'https://example.com/photos/murzik.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, 'Pukh', '2021-05-10', 5, 3, 'https://example.com/photos/pukh.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(4, 'Rex', '2018-11-25', 1, 1, 'https://example.com/photos/rex.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(5, 'Niki', '2022-01-08', 2, 4, 'https://example.com/photos/niki.jpg', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (id) DO NOTHING;

-- Reset sequences to avoid conflicts
SELECT setval('pet_schema.pet_types_id_seq', (SELECT COALESCE(MAX(id), 1) FROM pet_schema.pet_types));
SELECT setval('pet_schema.pets_id_seq', (SELECT COALESCE(MAX(id), 1) FROM pet_schema.pets));


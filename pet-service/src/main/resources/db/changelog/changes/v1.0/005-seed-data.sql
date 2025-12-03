--liquibase formatted sql

--changeset author:pet-service:5 labels:seed-data context:dev
--comment: Seed database with sample pet types, pets and pet info for development
--note: ownerId values (1, 2, 5) correspond to real Person IDs from Person Service

-- Insert sample pet types
INSERT INTO pet_schema.pet_types (id, type_name, description, exotic, active, created_at, updated_at, created_by, modified_by)
VALUES 
(1, 'DOG', 'Dog', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, 'CAT', 'Cat', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, 'RABBIT', 'Rabbit', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(4, 'HAMSTER', 'Hamster', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(5, 'PARROT', 'Parrot', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(6, 'IGUANA', 'Iguana', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Insert sample pets
-- ownerId = 1 (Ivan Petrenko) - owner of dog Barsik
-- ownerId = 2 (Maria Kovalenko) - owner of cat Murzik
-- ownerId = 5 (Dmitro Bondarenko) - owner of rabbit Pukh
INSERT INTO pet_schema.pets (id, name, birth_date, owner_id, type_id, photo_url, active, created_at, updated_at, created_by, modified_by)
VALUES 
(1, 'Barsik', '2020-03-15', 1, 1, 'https://example.com/photos/barsik.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, 'Murzik', '2019-07-20', 2, 2, 'https://example.com/photos/murzik.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, 'Pukh', '2021-05-10', 5, 3, 'https://example.com/photos/pukh.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(4, 'Rex', '2018-11-25', 1, 1, 'https://example.com/photos/rex.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(5, 'Niki', '2022-01-08', 2, 4, 'https://example.com/photos/niki.jpg', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Insert sample pet info (JSONB details)
INSERT INTO pet_schema.pet_info (id, pet_id, details, active, created_at, updated_at, created_by, modified_by)
VALUES 
-- Pet info for Barsik (id=1, owner=1, type=DOG)
(1, 1, '{"weight": 25.5, "breed": "Labrador Retriever", "color": "Golden", "favoriteFood": "chicken", "vaccinations": [{"name": "Rabies", "date": "2024-01-15", "nextDue": "2025-01-15"}, {"name": "DHPP", "date": "2024-03-01", "nextDue": "2025-03-01"}], "medicalHistory": {"lastCheckup": "2024-11-01", "chronicConditions": []}, "notes": "Very active, requires walks 3 times a day"}', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Pet info for Murzik (id=2, owner=2, type=CAT)
(2, 2, '{"weight": 4.2, "breed": "British Shorthair", "color": "Gray", "favoriteFood": "fish", "vaccinations": [{"name": "Rabies", "date": "2024-02-10", "nextDue": "2025-02-10"}, {"name": "FVRCP", "date": "2024-02-10", "nextDue": "2025-02-10"}], "medicalHistory": {"lastCheckup": "2024-10-15", "chronicConditions": []}, "notes": "Calm character, loves to sleep"}', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Pet info for Pukh (id=3, owner=5, type=RABBIT)
(3, 3, '{"weight": 1.8, "breed": "Holland Lop", "color": "White", "favoriteFood": "carrots", "vaccinations": [], "medicalHistory": {"lastCheckup": "2024-09-20", "chronicConditions": []}, "notes": "Very friendly, loves to play"}', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Pet info for Rex (id=4, owner=1, type=DOG)
(4, 4, '{"weight": 30.0, "breed": "German Shepherd", "color": "Black and Tan", "favoriteFood": "beef", "vaccinations": [{"name": "Rabies", "date": "2024-03-20", "nextDue": "2025-03-20"}], "medicalHistory": {"lastCheckup": "2024-11-05", "chronicConditions": ["hip dysplasia"]}, "notes": "Veterinary care for joints"}', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Pet info for Niki (id=5, owner=2, type=HAMSTER, inactive)
(5, 5, '{"weight": 0.15, "breed": "Syrian Hamster", "color": "Golden", "favoriteFood": "seeds", "vaccinations": [], "medicalHistory": {"lastCheckup": "2023-12-10", "chronicConditions": []}, "notes": "Inactive - deleted"}', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Reset sequences to avoid conflicts
SELECT setval('pet_schema.pet_types_id_seq', (SELECT MAX(id) FROM pet_schema.pet_types));
SELECT setval('pet_schema.pets_id_seq', (SELECT MAX(id) FROM pet_schema.pets));
SELECT setval('pet_schema.pet_info_id_seq', (SELECT MAX(id) FROM pet_schema.pet_info));


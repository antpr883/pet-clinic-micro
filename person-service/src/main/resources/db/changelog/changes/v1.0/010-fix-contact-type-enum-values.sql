--liquibase formatted sql

--changeset clinic_user:010-fix-contact-type-enum-values context:dev,prod,test
--comment: Виправлення невірних значень ContactType enum (PERSONAL/WORK/HOME -> правильні значення)
--preconditions onFail:CONTINUE onError:CONTINUE
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = 'person_schema' AND table_name = 'contacts' AND column_name = 'contact_type'

-- Виправляємо PERSONAL -> EMAIL (для email адрес)
UPDATE person_schema.contacts 
SET contact_type = 'EMAIL'
WHERE contact_type = 'PERSONAL' AND (value LIKE '%@%' OR value IS NULL);

-- Виправляємо WORK -> EMAIL (для email адрес)
UPDATE person_schema.contacts 
SET contact_type = 'EMAIL'
WHERE contact_type = 'WORK' AND (value LIKE '%@%' OR value IS NULL);

-- Виправляємо HOME -> OTHER (для адрес, які будуть мігровані в addresses)
UPDATE person_schema.contacts 
SET contact_type = 'OTHER'
WHERE contact_type = 'HOME';

-- Виправляємо PERSONAL -> MOBILE (для телефонів, якщо value містить телефон)
UPDATE person_schema.contacts 
SET contact_type = 'MOBILE'
WHERE contact_type = 'PERSONAL' AND value ~ '^\+?[0-9]{10,15}$';

-- Виправляємо WORK -> MOBILE (для телефонів, якщо value містить телефон)
UPDATE person_schema.contacts 
SET contact_type = 'MOBILE'
WHERE contact_type = 'WORK' AND value ~ '^\+?[0-9]{10,15}$';

--rollback 
-- UPDATE person_schema.contacts SET contact_type = 'PERSONAL' WHERE contact_type = 'EMAIL' AND label LIKE '%Особистий%';
-- UPDATE person_schema.contacts SET contact_type = 'WORK' WHERE contact_type = 'EMAIL' AND label LIKE '%Робочий%';
-- UPDATE person_schema.contacts SET contact_type = 'HOME' WHERE contact_type = 'OTHER' AND label LIKE '%адреса%';


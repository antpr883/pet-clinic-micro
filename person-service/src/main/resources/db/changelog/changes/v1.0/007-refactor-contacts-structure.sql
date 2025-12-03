--liquibase formatted sql

--changeset clinic_user:007-refactor-contacts-add-value-label context:dev,prod,test
--comment: Рефакторинг таблиці contacts - міграція для існуючих баз (якщо таблиця має старі поля)
--preconditions onFail:CONTINUE onError:CONTINUE
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = 'person_schema' AND table_name = 'contacts' AND column_name = 'value'

-- Додаємо нові поля (якщо їх ще немає)
-- PostgreSQL підтримує IF NOT EXISTS, але для сумісності використовуємо preconditions
ALTER TABLE person_schema.contacts ADD COLUMN value TEXT;
ALTER TABLE person_schema.contacts ADD COLUMN label VARCHAR(100);

-- Мігруємо дані з окремих полів в value (якщо старі поля існують)
-- Використовуємо preconditions для перевірки наявності старих полів
--preconditions onFail:CONTINUE onError:CONTINUE
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = 'person_schema' AND table_name = 'contacts' AND column_name = 'email'
UPDATE person_schema.contacts
SET 
    value = CASE
        WHEN email IS NOT NULL THEN email
        WHEN phone IS NOT NULL THEN phone
        WHEN address IS NOT NULL THEN address
        ELSE NULL
    END,
    contact_type = CASE
        WHEN email IS NOT NULL THEN 'EMAIL'
        WHEN phone IS NOT NULL AND contact_type = 'MOBILE' THEN 'MOBILE'
        WHEN phone IS NOT NULL THEN 'PHONE'
        WHEN address IS NOT NULL THEN 'OTHER'
        ELSE contact_type
    END,
    label = CASE
        WHEN email IS NOT NULL AND contact_type = 'WORK' THEN 'Work email'
        WHEN email IS NOT NULL AND contact_type = 'PERSONAL' THEN 'Personal email'
        WHEN email IS NOT NULL THEN 'Email'
        WHEN phone IS NOT NULL AND contact_type = 'MOBILE' THEN 'Mobile phone'
        WHEN phone IS NOT NULL AND contact_type = 'WORK' THEN 'Work phone'
        WHEN phone IS NOT NULL AND contact_type = 'PERSONAL' THEN 'Personal phone'
        WHEN phone IS NOT NULL THEN 'Phone'
        WHEN address IS NOT NULL THEN 'Home address'
        ELSE 'Contact'
    END
WHERE value IS NULL;

-- Delete old fields (if they exist)
ALTER TABLE person_schema.contacts DROP COLUMN IF EXISTS email;
ALTER TABLE person_schema.contacts DROP COLUMN IF EXISTS phone;
ALTER TABLE person_schema.contacts DROP COLUMN IF EXISTS address;
ALTER TABLE person_schema.contacts DROP COLUMN IF EXISTS description;

-- Update indexes
DROP INDEX IF EXISTS person_schema.idx_contact_email;
DROP INDEX IF EXISTS person_schema.idx_contact_phone;

-- Create new indexes (if they don't exist yet)
CREATE INDEX IF NOT EXISTS idx_contact_value ON person_schema.contacts (value) WHERE value IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_contact_label ON person_schema.contacts (label) WHERE label IS NOT NULL;

--rollback 
-- ALTER TABLE person_schema.contacts ADD COLUMN email VARCHAR(100);
-- ALTER TABLE person_schema.contacts ADD COLUMN phone VARCHAR(20);
-- ALTER TABLE person_schema.contacts ADD COLUMN address VARCHAR(255);
-- ALTER TABLE person_schema.contacts ADD COLUMN description VARCHAR(500);
-- UPDATE person_schema.contacts SET email = value WHERE contact_type IN ('EMAIL', 'WORK', 'PERSONAL') AND value LIKE '%@%';
-- UPDATE person_schema.contacts SET phone = value WHERE contact_type IN ('PHONE', 'MOBILE');
-- UPDATE person_schema.contacts SET address = value WHERE contact_type = 'HOME';
-- ALTER TABLE person_schema.contacts DROP COLUMN IF EXISTS value;
-- ALTER TABLE person_schema.contacts DROP COLUMN IF EXISTS label;


-- Скрипт ініціалізації тестової схеми PostgreSQL
-- Виконується автоматично при старті Testcontainers контейнера

-- Створюємо схему для тестів (якщо не існує)
CREATE SCHEMA IF NOT EXISTS clinic_schema;

-- Надаємо права користувачу test
GRANT ALL PRIVILEGES ON SCHEMA clinic_schema TO test;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA clinic_schema TO test;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA clinic_schema TO test;

-- Встановлюємо default privileges для майбутніх таблиць
ALTER DEFAULT PRIVILEGES IN SCHEMA clinic_schema GRANT ALL ON TABLES TO test;
ALTER DEFAULT PRIVILEGES IN SCHEMA clinic_schema GRANT ALL ON SEQUENCES TO test;



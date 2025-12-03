# Clinic Service

## Опис проєкту

**Clinic Service** - це мікросервіс для управління клінічними кейсами (випадками лікування) в системі Pet Clinic. Сервіс відповідає за повний життєвий цикл лікування тварини: від реєстрації до виписки, включаючи візити, діагностику, процедури та госпіталізацію.

## Призначення

Clinic Service є **центральним бізнес-компонентом** системи Pet Clinic, оскільки:

- **Управляє клінічними кейсами** - повний життєвий цикл лікування тварини
- **Ведеться workflow лікування** - через Spring State Machine (реєстрація → огляд → діагностика → лікування → виписка)
- **Зберігає історію візитів** - всі візити до клініки
- **Управляє діагностикою** - діагнози та їх статуси
- **Управляє процедурами** - медичні процедури та їх виконання
- **Управляє госпіталізацією** - госпіталізація тварин та щоденні огляди
- **Інтегрується з Person та Pet Services** - отримує інформацію про тварин та ветеринарів

## Частина загальної системи

Clinic Service є **частиною мікросервісної архітектури Pet Clinic**, яка складається з:

```
Pet Clinic Microservices System
├── Person Service                    ← Управління персонами
├── Pet Service                       ← Управління тваринами
└── Clinic Service (цей сервіс)       ← Управління клінічними кейсами
```

**Зв'язки з іншими сервісами:**

- **Person Service** - Clinic Service використовує Person Service для отримання інформації про ветеринарів та власників
- **Pet Service** - Clinic Service використовує Pet Service для отримання інформації про тварин при створенні клінічних кейсів

## Мікросервісна архітектура

Clinic Service є **автономним мікросервісом** з:

- **Власною базою даних** - `pet_clinic` зі схемою `clinic_schema`
- **Власним API** - REST endpoints на `/api/v1/clinic`
- **Незалежним деплоєм** - може бути розгорнутий окремо від інших сервісів
- **Власною бізнес-логікою** - управління клінічними кейсами, workflow через State Machine

**Принципи мікросервісної архітектури:**

- **Database per Service** - кожен сервіс має свою БД
- **API Gateway Pattern** - сервіси спілкуються через REST API
- **Service Independence** - сервіси незалежні один від одного

## Функціональність

### Основні можливості

1. **Управління клінічними кейсами:**
   - CRUD операції для клінічних кейсів
   - Workflow через Spring State Machine
   - Статуси кейсів (REGISTERED, INITIAL_CHECKUP_COMPLETED, DIAGNOSIS_COMPLETED, etc.)
   - Пріоритети (NORMAL, URGENT, EMERGENCY)

2. **Управління візитами:**
   - Реєстрація візитів до клініки
   - Типи візитів (INITIAL, FOLLOW_UP, EMERGENCY, etc.)
   - Історія візитів

3. **Управління діагностикою:**
   - Створення діагнозів
   - Статуси діагнозів (PRELIMINARY, CONFIRMED, RULED_OUT)
   - Історія діагностики

4. **Управління процедурами:**
   - Створення медичних процедур
   - Статуси процедур (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED)
   - Відстеження виконання

5. **Управління госпіталізацією:**
   - Реєстрація госпіталізації
   - Статуси госпіталізації (ADMITTED, DISCHARGED, etc.)
   - Щоденні огляди (Daily Checks) з деталями в JSONB

6. **Spring State Machine:**
   - Управління станами клінічного кейсу
   - Валідація переходів між станами
   - Guards для бізнес-правил

## Стадія готовності

### ✅ Реалізовано

- [x] **Архітектура:**
  - Layered Architecture (Controller → Service → Repository)
  - CustomJpaRepository з Entity Graph підтримкою
  - AbstractBaseService для уніфікації CRUD

- [x] **Функціональність:**
  - CRUD операції для ClinicCase, ClinicVisit, Diagnosis, Procedure, Hospitalization, DailyCheck
  - Spring State Machine для workflow
  - Soft delete
  - JPA Auditing (createdBy, modifiedBy, createdAt, updatedAt)
  - Optimistic Locking (@Version)

- [x] **Технології:**
  - Spring Boot 3.x
  - Spring Data JPA з Cosium Entity Graph
  - Spring State Machine для workflow
  - PostgreSQL зі схемою `clinic_schema`
  - Liquibase міграції
  - MapStruct для DTO маппінгу
  - Lombok для зменшення boilerplate
  - Testcontainers для інтеграційного тестування

- [x] **База даних:**
  - Таблиці: clinic_cases, clinic_visits, diagnoses, procedures, hospitalizations, daily_checks
  - Індекси для оптимізації
  - Foreign Keys з CASCADE
  - Soft Delete через поле `active`
  - Optimistic Locking через поле `version`

### 🚧 В процесі / Планується

- [ ] **API Endpoints** - створення REST контролерів та endpoints
- [ ] **API Gateway інтеграція** - інтеграція з API Gateway для загальної системи
- [ ] **Service Discovery** - реєстрація в Service Discovery (Eureka/Consul)
- [ ] **Circuit Breaker** - захист від падіння Person/Pet Services
- [ ] **Distributed Tracing** - інтеграція з tracing системою
- [ ] **Metrics & Monitoring** - додаткові метрики для моніторингу
- [ ] **RSQL пошук** - додавання RSQL конфігурації та SearchService (зараз є тільки в pom.xml)

## Інтеграція в загальну систему

### Поточна інтеграція

**1. Person Service використовується Clinic Service:**

```java
// Clinic Service викликає Person Service API
GET /api/v1/persons/{vetId}/preview
GET /api/v1/persons/{ownerId}/preview
// Для перевірки існування ветеринара та власника
```

**2. Pet Service використовується Clinic Service:**

```java
// Clinic Service викликає Pet Service API
GET /api/v1/pets/{petId}/preview
// Для перевірки існування тварини при створенні клінічного кейсу
```

### Майбутня інтеграція

- **API Gateway** - всі запити будуть проходити через API Gateway
- **Service Mesh** - для управління міжсервісною комунікацією
- **Event-Driven Architecture** - події для синхронізації даних між сервісами

## Технічний стек

- **Framework:** Spring Boot 3.x
- **Database:** PostgreSQL 16
- **ORM:** Hibernate (Spring Data JPA)
- **Entity Graph:** Cosium 3.2.3
- **State Machine:** Spring State Machine
- **DTO Mapping:** MapStruct
- **Code Generation:** Lombok
- **Migrations:** Liquibase
- **Testing:** Testcontainers, JUnit 5
- **API Documentation:** Springdoc OpenAPI (Swagger)

## База даних

- **Database:** `pet_clinic`
- **Schema:** `clinic_schema`
- **User:** `clinic_user`
- **Tables:**
  - `clinic_cases` - основна таблиця клінічних кейсів
  - `clinic_visits` - візити до клініки
  - `diagnoses` - діагнози
  - `procedures` - медичні процедури
  - `hospitalizations` - госпіталізації
  - `daily_checks` - щоденні огляди під час госпіталізації

## API Endpoints

**Base URL:** `/api/v1/clinic`

**Основні endpoints (планується):**
- `GET /api/v1/clinic/cases/{id}` - отримати клінічний кейс
- `POST /api/v1/clinic/cases` - створити клінічний кейс
- `PUT /api/v1/clinic/cases/{id}` - оновити клінічний кейс
- `POST /api/v1/clinic/cases/{id}/transitions` - змінити статус (State Machine)
- `GET /api/v1/clinic/cases/search?rsqlQuery=...` - RSQL пошук

**Мікросервісна інтеграція:**
- Перевірка існування petId через Pet Service
- Перевірка існування vetId, ownerId через Person Service

## Документація

Повна технічна документація доступна в папці:
- `project-technical-documentation/` - детальна технічна документація (якщо створена)

## Історія проєкту

### Версія 1.0 (Поточна)

**Що реалізовано:**

Clinic Service був створений як **третій мікросервіс** в системі Pet Clinic для управління клінічними кейсами. Сервіс реалізує повний функціонал для ведення клінічних кейсів, включаючи:

- **Клінічні кейси** - повний життєвий цикл лікування тварини
- **Spring State Machine** - управління workflow лікування (реєстрація → огляд → діагностика → лікування → виписка)
- **Візити** - реєстрація та історія візитів до клініки
- **Діагностика** - створення та управління діагнозами
- **Процедури** - медичні процедури та відстеження їх виконання
- **Госпіталізація** - реєстрація госпіталізації та щоденні огляди
- **Optimistic Locking** - захист від concurrent modifications

**Для чого створений:**

Clinic Service є **центральним бізнес-компонентом** системи Pet Clinic, оскільки:
- Ведеться повний життєвий цикл лікування тварини
- Управляє workflow лікування через State Machine
- Зберігає всю історію лікування (візити, діагнози, процедури, госпіталізація)
- Інтегрується з Person Service та Pet Service для отримання інформації

**Частина чого:**

Clinic Service є частиною **мікросервісної системи Pet Clinic**, яка призначена для:
- Управління ветеринарною клінікою
- Обліку тварин та їх власників
- Ведення клінічних кейсів та історії лікування

**Стадія готовності:**

🚧 **В процесі розробки:**
- ✅ Всі entities та DTOs створені
- ✅ Всі services та repositories реалізовані
- ✅ Spring State Machine налаштований
- ✅ Liquibase міграції створені
- ⏳ REST контролери та API endpoints - **в процесі**
- ⏳ RSQL пошук - **потрібно додати** (є тільки в pom.xml)

**Інтеграція:**

- ✅ Інтегрований з Person Service (vetId, ownerId посилання)
- ✅ Інтегрований з Pet Service (petId посилання)
- 🚧 Планується інтеграція з API Gateway
- 🚧 Планується Service Discovery

## Запуск

```bash
# 1. Запустити PostgreSQL
docker-compose up -d postgres

# 2. Запустити сервіс
cd clinic-service
mvn spring-boot:run

# Або через Maven
mvn clean install
java -jar target/clinic-service.jar
```

## Конфігурація

- **Port:** 8084 (за замовчуванням)
- **Database:** `pet_clinic`
- **Schema:** `clinic_schema`
- **User:** `clinic_user`
- **Profile:** `dev` (за замовчуванням)

Конфігурація в `application-dev.yml` та `dev.env`.

## Контакти та підтримка

Для питань та підтримки звертайтеся до команди розробки Pet Clinic.

---

**Версія:** 1.0  
**Статус:** 🚧 В процесі розробки (основна функціональність готова, потрібні контролери)  
**Останнє оновлення:** 2025-11-20


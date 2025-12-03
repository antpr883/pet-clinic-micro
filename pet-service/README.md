# Pet Service

## Опис проєкту

**Pet Service** - це мікросервіс для управління тваринами в системі Pet Clinic. Сервіс відповідає за зберігання та управління інформацією про домашніх тварин, їх типи та детальну інформацію.

## Призначення

Pet Service є **ключовим компонентом** системи Pet Clinic, оскільки:

- **Зберігає інформацію про тварин** - ім'я, дата народження, тип тварини
- **Управляє типами тварин** - собаки, коти, кролі, хом'яки, etc.
- **Зберігає детальну інформацію** - вага, розмір, особливості (JSONB)
- **Пов'язує тварин з власниками** - через ownerId (посилання на Person Service)
- **Надає API для Clinic Service** - для отримання інформації про тварин при створенні клінічних кейсів

## Частина загальної системи

Pet Service є **частиною мікросервісної архітектури Pet Clinic**, яка складається з:

```
Pet Clinic Microservices System
├── Person Service                    ← Управління персонами
├── Pet Service (цей сервіс)         ← Управління тваринами
└── Clinic Service                    ← Управління клінічними кейсами
```

**Зв'язки з іншими сервісами:**

- **Person Service** - Pet Service використовує Person Service для отримання інформації про власників (ownerId → Person)
- **Clinic Service** використовує Pet Service для отримання інформації про тварин при створенні клінічних кейсів

## Мікросервісна архітектура

Pet Service є **автономним мікросервісом** з:

- **Власною базою даних** - `pet_clinic` зі схемою `pet_schema`
- **Власним API** - REST endpoints на `/api/v1/pets`
- **Незалежним деплоєм** - може бути розгорнутий окремо від інших сервісів
- **Власною бізнес-логікою** - управління тваринами, типами тварин, детальною інформацією

**Принципи мікросервісної архітектури:**

- **Database per Service** - кожен сервіс має свою БД
- **API Gateway Pattern** - сервіси спілкуються через REST API
- **Service Independence** - сервіси незалежні один від одного

## Функціональність

### Основні можливості

1. **Управління тваринами:**
   - CRUD операції для тварин
   - Зв'язок з власником через ownerId (посилання на Person Service)
   - Soft delete (логічне видалення)

2. **Управління типами тварин:**
   - CRUD операції для типів тварин (Dog, Cat, Rabbit, etc.)
   - Каталог типів тварин

3. **Детальна інформація:**
   - Зберігання детальної інформації в JSONB (вага, розмір, особливості)
   - Гнучка структура даних

4. **Пошук та фільтрація:**
   - RSQL для динамічних запитів
   - Пошук по імені, типу, власнику
   - Пагінація результатів

5. **Мікросервісна інтеграція:**
   - API для перевірки існування тварин
   - API для отримання preview інформації
   - Bulk операції для оптимізації

## Стадія готовності

### ✅ Реалізовано

- [x] **Архітектура:**
  - Layered Architecture (Controller → Service → Repository)
  - CustomJpaRepository з Entity Graph підтримкою
  - AbstractBaseService для уніфікації CRUD

- [x] **Функціональність:**
  - CRUD операції для Pet, PetType, PetInfo
  - RSQL пошук з Entity Graph оптимізацією
  - Soft delete
  - JPA Auditing (createdBy, modifiedBy, createdAt, updatedAt)

- [x] **Технології:**
  - Spring Boot 3.x
  - Spring Data JPA з Cosium Entity Graph
  - PostgreSQL зі схемою `pet_schema`
  - Liquibase міграції
  - MapStruct для DTO маппінгу
  - Lombok для зменшення boilerplate
  - Testcontainers для інтеграційного тестування

- [x] **API:**
  - REST endpoints для Pet, PetType, PetInfo
  - RSQL пошук
  - Пагінація
  - Мікросервісна інтеграція (checkExistence, findPreviewsByIds)

- [x] **База даних:**
  - Таблиці: pets, pet_types, pet_info
  - Індекси для оптимізації
  - Foreign Keys з CASCADE
  - Soft Delete через поле `active`

### 🚧 В процесі / Планується

- [ ] **API Gateway інтеграція** - інтеграція з API Gateway для загальної системи
- [ ] **Service Discovery** - реєстрація в Service Discovery (Eureka/Consul)
- [ ] **Circuit Breaker** - захист від падіння Person Service
- [ ] **Distributed Tracing** - інтеграція з tracing системою
- [ ] **Metrics & Monitoring** - додаткові метрики для моніторингу
- [ ] **Фото тварин** - інтеграція з S3 для зберігання фото

## Інтеграція в загальну систему

### Поточна інтеграція

**1. Person Service використовується Pet Service:**

```java
// Pet Service викликає Person Service API
GET /api/v1/persons/{ownerId}/preview
// Для перевірки існування власника при створенні тварини
```

**2. Clinic Service використовує Pet Service:**

```java
// Clinic Service викликає Pet Service API
GET /api/v1/pets/{petId}/preview
// Для отримання інформації про тварину при створенні клінічного кейсу
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
- **Query Language:** RSQL Parser
- **DTO Mapping:** MapStruct
- **Code Generation:** Lombok
- **Migrations:** Liquibase
- **Testing:** Testcontainers, JUnit 5
- **API Documentation:** Springdoc OpenAPI (Swagger)

## База даних

- **Database:** `pet_clinic`
- **Schema:** `pet_schema`
- **User:** `pet_user`
- **Tables:**
  - `pets` - основна таблиця тварин
  - `pet_types` - типи тварин (Dog, Cat, Rabbit, etc.)
  - `pet_info` - детальна інформація (JSONB)

## API Endpoints

**Base URL:** `/api/v1/pets`

**Основні endpoints:**
- `GET /api/v1/pets/{id}` - отримати тварину
- `POST /api/v1/pets` - створити тварину
- `PUT /api/v1/pets/{id}` - оновити тварину
- `DELETE /api/v1/pets/{id}` - видалити тварину (soft delete)
- `GET /api/v1/pets/search?rsqlQuery=...` - RSQL пошук

**Мікросервісна інтеграція:**
- `POST /api/v1/pets/check-existence` - перевірити існування тварин
- `POST /api/v1/pets/previews` - отримати preview для списку ID

## Документація

Повна технічна документація доступна в папці:
- `project-technical-documentation/` - детальна технічна документація (якщо створена)

## Історія проєкту

### Версія 1.0 (Поточна)

**Що реалізовано:**

Pet Service був створений як **другий мікросервіс** в системі Pet Clinic для управління тваринами. Сервіс реалізує повний CRUD функціонал для тварин, типів тварин та детальної інформації, з підтримкою:

- **Тварин** - ім'я, дата народження, тип, власник
- **Типів тварин** - каталог типів (Dog, Cat, Rabbit, Hamster, etc.)
- **Детальної інформації** - вага, розмір, особливості (JSONB)
- **RSQL пошуку** - динамічні запити через REST API
- **Entity Graph оптимізації** - уникнення N+1 проблеми
- **Мікросервісної інтеграції** - API для інших сервісів

**Для чого створений:**

Pet Service є **центральним компонентом** системи Pet Clinic, оскільки:
- Зберігає інформацію про всіх тварин в клініці
- Пов'язує тварин з власниками (через ownerId)
- Надає API для Clinic Service для створення клінічних кейсів
- Дозволяє вести облік тварин та їх історію

**Частина чого:**

Pet Service є частиною **мікросервісної системи Pet Clinic**, яка призначена для:
- Управління ветеринарною клінікою
- Обліку тварин та їх власників
- Ведення клінічних кейсів та історії лікування

**Стадія готовності:**

✅ **Готовий до використання:**
- Всі основні функції реалізовані
- API працює та протестовано
- Інтеграція з Person Service та Clinic Service налаштована
- Технічна документація може бути створена

**Інтеграція:**

- ✅ Інтегрований з Person Service (ownerId посилання)
- ✅ Інтегрований з Clinic Service (petId посилання)
- 🚧 Планується інтеграція з API Gateway
- 🚧 Планується Service Discovery

## Запуск

```bash
# 1. Запустити PostgreSQL
docker-compose up -d postgres

# 2. Запустити сервіс
cd pet-service
mvn spring-boot:run

# Або через Maven
mvn clean install
java -jar target/pet-service.jar
```

## Конфігурація

- **Port:** 8082 (за замовчуванням)
- **Database:** `pet_clinic`
- **Schema:** `pet_schema`
- **Profile:** `dev` (за замовчуванням)

Конфігурація в `application-dev.yml` та `dev.env`.

## Контакти та підтримка

Для питань та підтримки звертайтеся до команди розробки Pet Clinic.

---

**Версія:** 1.0  
**Статус:** ✅ Готовий до використання  
**Останнє оновлення:** 2025-11-20


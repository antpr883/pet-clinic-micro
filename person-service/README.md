# Person Service

## Опис проєкту

**Person Service** - це мікросервіс для управління персонами (людьми) в системі Pet Clinic. Сервіс відповідає за зберігання та управління інформацією про власників тварин, ветеринарів та реєстраторів клініки.

## Призначення

Person Service є **фундаментальним компонентом** системи Pet Clinic, оскільки:

- **Зберігає інформацію про людей** - власників тварин, ветеринарів, реєстраторів
- **Управляє контактною інформацією** - email, телефони, адреси
- **Надає API для інших мікросервісів** - Pet Service та Clinic Service використовують Person Service для отримання інформації про власників та ветеринарів
- **Підтримує ролі** - OWNER (власник), VETERINARIAN (ветеринар), RECEPTIONIST (реєстратор)

## Частина загальної системи

Person Service є **частиною мікросервісної архітектури Pet Clinic**, яка складається з:

```
Pet Clinic Microservices System
├── Person Service (цей сервіс)      ← Управління персонами
├── Pet Service                       ← Управління тваринами
└── Clinic Service                    ← Управління клінічними кейсами
```

**Зв'язки з іншими сервісами:**

- **Pet Service** використовує Person Service для отримання інформації про власників (ownerId → Person)
- **Clinic Service** використовує Person Service для отримання інформації про ветеринарів та власників

## Мікросервісна архітектура

Person Service є **автономним мікросервісом** з:

- **Власною базою даних** - `pet_clinic_person` зі схемою `person_schema`
- **Власним API** - REST endpoints на `/api/v1/persons`
- **Незалежним деплоєм** - може бути розгорнутий окремо від інших сервісів
- **Власною бізнес-логікою** - управління персонами, контактами, адресами

**Принципи мікросервісної архітектури:**

- **Database per Service** - кожен сервіс має свою БД
- **API Gateway Pattern** - сервіси спілкуються через REST API
- **Service Independence** - сервіси незалежні один від одного

## Функціональність

### Основні можливості

1. **Управління персонами:**
   - CRUD операції для персон
   - Підтримка ролей (OWNER, VETERINARIAN, RECEPTIONIST)
   - Soft delete (логічне видалення)

2. **Управління контактами:**
   - Email, телефони, інші типи контактів
   - Підтримка primary контактів
   - Різні типи контактів (PERSONAL, WORK, etc.)

3. **Управління адресами:**
   - Домашні та робочі адреси
   - Повна адресна інформація (місто, вулиця, будинок, квартира)

4. **Пошук та фільтрація:**
   - RSQL для динамічних запитів
   - Пошук по імені, прізвищу, ролі
   - Пагінація результатів

5. **Мікросервісна інтеграція:**
   - API для перевірки існування персон
   - API для отримання preview інформації
   - Bulk операції для оптимізації

## Стадія готовності

### ✅ Реалізовано

- [x] **Архітектура:**
  - Layered Architecture (Controller → Service → Repository)
  - CustomJpaRepository з Entity Graph підтримкою
  - AbstractBaseService для уніфікації CRUD

- [x] **Функціональність:**
  - CRUD операції для Person, Contact, Address
  - RSQL пошук з Entity Graph оптимізацією
  - Soft delete
  - JPA Auditing (createdBy, modifiedBy, createdAt, updatedAt)

- [x] **Технології:**
  - Spring Boot 3.x
  - Spring Data JPA з Cosium Entity Graph
  - PostgreSQL зі схемою `person_schema`
  - Liquibase міграції
  - MapStruct для DTO маппінгу
  - Lombok для зменшення boilerplate
  - Testcontainers для інтеграційного тестування

- [x] **API:**
  - REST endpoints для Person, Contact, Address
  - RSQL пошук
  - Пагінація
  - Мікросервісна інтеграція (checkPersonsExist, getPreviewsByIds)

- [x] **База даних:**
  - Таблиці: persons, contacts, addresses
  - Індекси для оптимізації
  - Foreign Keys з CASCADE
  - Soft Delete через поле `active`

### 🚧 В процесі / Планується

- [ ] **API Gateway інтеграція** - інтеграція з API Gateway для загальної системи
- [ ] **Service Discovery** - реєстрація в Service Discovery (Eureka/Consul)
- [ ] **Circuit Breaker** - захист від падіння інших сервісів
- [ ] **Distributed Tracing** - інтеграція з tracing системою
- [ ] **Metrics & Monitoring** - додаткові метрики для моніторингу

## Інтеграція в загальну систему

### Поточна інтеграція

**1. Pet Service використовує Person Service:**

```java
// Pet Service викликає Person Service API
GET /api/v1/persons/{id}/preview
// Для отримання інформації про власника тварини
```

**2. Clinic Service використовує Person Service:**

```java
// Clinic Service викликає Person Service API
GET /api/v1/persons/{id}/preview
// Для отримання інформації про ветеринара або власника
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

- **Database:** `pet_clinic_person`
- **Schema:** `person_schema`
- **User:** `clinic_user`
- **Tables:**
  - `persons` - основна таблиця персон
  - `contacts` - контактна інформація
  - `addresses` - адреси

## API Endpoints

**Base URL:** `/api/v1/persons`

**Основні endpoints:**
- `GET /api/v1/persons/{id}` - отримати персону
- `POST /api/v1/persons` - створити персону
- `PUT /api/v1/persons/{id}` - оновити персону
- `DELETE /api/v1/persons/{id}` - видалити персону (soft delete)
- `GET /api/v1/persons/search?rsqlQuery=...` - RSQL пошук

**Мікросервісна інтеграція:**
- `POST /api/v1/persons/check-existence` - перевірити існування персон
- `POST /api/v1/persons/previews` - отримати preview для списку ID

## Документація

Повна технічна документація доступна в папці:
- `project-technical-documentation/` - детальна технічна документація

## Історія проєкту

### Версія 1.0 (Поточна)

**Що реалізовано:**

Person Service був створений як **перший мікросервіс** в системі Pet Clinic для управління персонами. Сервіс реалізує повний CRUD функціонал для персон, контактів та адрес, з підтримкою:

- **Ролей** - OWNER (власник тварини), VETERINARIAN (ветеринар), RECEPTIONIST (реєстратор)
- **Контактної інформації** - email, телефони, інші типи контактів
- **Адрес** - домашні та робочі адреси
- **RSQL пошуку** - динамічні запити через REST API
- **Entity Graph оптимізації** - уникнення N+1 проблеми
- **Мікросервісної інтеграції** - API для інших сервісів

**Для чого створений:**

Person Service є **фундаментом** системи Pet Clinic, оскільки:
- Всі інші сервіси (Pet, Clinic) посилаються на Person Service
- Зберігає інформацію про всіх учасників системи (власників, ветеринарів, реєстраторів)
- Надає API для отримання інформації про персон

**Частина чого:**

Person Service є частиною **мікросервісної системи Pet Clinic**, яка призначена для:
- Управління ветеринарною клінікою
- Обліку тварин та їх власників
- Ведення клінічних кейсів та історії лікування

**Стадія готовності:**

✅ **Готовий до використання:**
- Всі основні функції реалізовані
- API працює та протестовано
- Інтеграція з Pet Service та Clinic Service налаштована
- Технічна документація створена

**Інтеграція:**

- ✅ Інтегрований з Pet Service (ownerId посилання)
- ✅ Інтегрований з Clinic Service (vetId, ownerId посилання)
- 🚧 Планується інтеграція з API Gateway
- 🚧 Планується Service Discovery

## Запуск

```bash
# 1. Запустити PostgreSQL
docker-compose up -d postgres

# 2. Запустити сервіс
cd person-service
mvn spring-boot:run

# Або через Maven
mvn clean install
java -jar target/person-service.jar
```

## Конфігурація

- **Port:** 8083 (за замовчуванням)
- **Database:** `pet_clinic_person`
- **Schema:** `person_schema`
- **Profile:** `dev` (за замовчуванням)

Конфігурація в `application-dev.yml` та `dev.env`.

## Контакти та підтримка

Для питань та підтримки звертайтеся до команди розробки Pet Clinic.

---

**Версія:** 1.0  
**Статус:** ✅ Готовий до використання  
**Останнє оновлення:** 2025-11-20


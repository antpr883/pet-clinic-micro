# Auth Security Client

Spring Boot Starter для JWT аутентифікації в мікросервісах (Pet Service, Person Service, Clinic Service).

## 📋 Опис

`auth-security-client` - це Spring Boot Starter, який надає автоматичну конфігурацію для валідації JWT токенів та захисту endpoints в мікросервісах.

### Основні можливості:

- ✅ **Автоматична валідація JWT токенів** - через `JwtAuthenticationFilter`
- ✅ **Auto-configuration** - автоматична налаштування Spring Security
- ✅ **Type-safe permissions** - централізована система permissions
- ✅ **Проста інтеграція** - додайте dependency та налаштуйте конфігурацію

## 🚀 Швидкий старт

### 1. Додайте dependency в `pom.xml`:

```xml
<dependency>
    <groupId>com.micro.security</groupId>
    <artifactId>auth-security-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Налаштуйте конфігурацію в `application.yml`:

```yaml
auth:
  client:
    security:
      jwt:
        signing:
          secret: ${JWT_SECRET:your-secret-key}
        issuer: ${JWT_ISSUER:pet-clinic-auth}
```

### 3. Використовуйте `@PreAuthorize` для захисту endpoints:

```java
@RestController
@RequestMapping("/api/v1/pets")
public class PetController {
    
    @GetMapping
    @PreAuthorize("hasAuthority('PET_READ')")
    public ResponseEntity<List<PetDto>> getAllPets() {
        // ...
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('PET_WRITE')")
    public ResponseEntity<PetDto> createPet(@RequestBody PetCreateDto dto) {
        // ...
    }
}
```

## 📚 Документація

- [ARCHITECTURE.md](./ARCHITECTURE.md) - детальна архітектура та інтеграція
- [PERMISSIONS.md](./PERMISSIONS.md) - документація по Permissions

## 🔧 Налаштування

### Обов'язкові параметри:

- `auth.client.security.jwt.signing.secret` - секретний ключ для валідації JWT (має співпадати з auth-service)

### Опціональні параметри:

- `auth.client.security.jwt.issuer` - issuer токена (за замовчуванням: `pet-clinic-auth`)
- `auth.client.security.jwt.clock-skew-seconds` - допустимий часовий зсув (за замовчуванням: 60 секунд)

## 🏗️ Архітектура

```
auth-security-client/
├── config/
│   ├── AuthClientSecurityAutoConfiguration.java  # Auto-configuration
│   └── AuthClientSecurityProperties.java         # Configuration properties
├── filter/
│   └── JwtAuthenticationFilter.java              # JWT validation filter
├── jwt/
│   ├── JwtTokenValidator.java                    # JWT token validation
│   ├── JwtUserDetails.java                       # User details from JWT
│   └── JwtValidationException.java               # Custom exception
└── permissions/
    └── Permissions.java                          # Centralized permissions
```

## 📦 Встановлення в локальний Maven репозиторій

Якщо ви використовуєте локальну версію (не з Maven Central):

```bash
cd auth-security-client
mvn clean install
```

Це встановить JAR в локальний Maven репозиторій (`~/.m2/repository/com/micro/security/auth-security-client/0.0.1-SNAPSHOT/`).

## 🔐 Permissions

Всі permissions визначені в класі `Permissions.java`. Використовуйте константи замість рядків:

```java
@PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
```

Детальніше: [PERMISSIONS.md](./PERMISSIONS.md)

## 🧪 Тестування

Для тестування endpoints з JWT токенами:

1. Отримайте токен через `POST /api/v1/auth/login` в auth-service
2. Використовуйте токен в header: `Authorization: Bearer <token>`
3. Викликайте захищені endpoints

## 📝 Примітки

- Цей starter використовується в: `person-service`, `pet-service`, `clinic-service`
- JWT токени генеруються в `auth-service`
- Всі сервіси мають використовувати однаковий `secret` для валідації токенів

## 🔗 Пов'язані проекти

- [auth-service](../auth-service/) - генерація JWT токенів
- [person-service](../person-service/) - використовує auth-security-client
- [pet-service](../pet-service/) - використовує auth-security-client
- [clinic-service](../clinic-service/) - використовує auth-security-client

---

**Автор:** Pet Clinic Microservices Team  
**Версія:** 0.0.1-SNAPSHOT


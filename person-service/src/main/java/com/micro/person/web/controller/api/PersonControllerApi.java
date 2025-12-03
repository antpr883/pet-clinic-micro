package com.micro.person.web.controller.api;

import com.micro.person.data.constants.EntityConstants;
import com.micro.person.data.constants.PersonEntityGraphConstants;
import com.micro.person.data.dto.person.PersonCreateDto;
import com.micro.person.data.dto.person.PersonFullDto;
import com.micro.person.data.dto.person.PersonFullExtendedDto;
import com.micro.person.data.dto.person.PersonFullPreviewDto;
import com.micro.person.data.dto.person.PersonPreviewDto;
import com.micro.person.data.dto.person.PersonUpdateDto;
import com.micro.person.web.response.AppResponse;
import com.micro.person.web.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Оптимізований API інтерфейс для управління персонами.
 * 
 * Версія: 1.0 (Optimized)
 * 
 * Філософія: "Один сервіс — мінімум API — максимум функціональності."
 * 
 * Основні принципи:
 * - RSQL як єдиний механізм пошуку
 * - Мінімальний набір endpoints без дублікатів
 * - RESTful структура з підресурсами
 * - Готовність до мікросервісних інтеграцій
 * 
 * @see com.micro.person.web.controller.PersonController
 */
@Tag(
    name = "Person Management",
    description = "Оптимізований API для управління персонами: CRUD операції, RSQL пошук, мікросервісна інтеграція"
)
public interface PersonControllerApi {

    // ========== CRUD Operations ==========

    @Operation(
        summary = "Отримати персону по ID",
        description = """
                Повертає повну інформацію про персону з контактами та адресами.
                
                Використовує PersonFullDto для повного представлення даних.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Персона знайдена",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Персона не знайдена",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    ResponseEntity<AppResponse<PersonFullDto>> getById(
        @Parameter(description = "ID персони", required = true, example = "1")
        @PathVariable Long id
    );

    @Operation(
        summary = "Отримати короткий вигляд персони (Preview)",
        description = """
                Повертає мінімальну інформацію про персону (id, firstName, lastName, email, phone).
                
                Використовується для списків та швидкого доступу до базової інформації.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Preview персони отримано",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Персона не знайдена",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @GetMapping("/{id}/preview")
    ResponseEntity<AppResponse<PersonPreviewDto>> getPreviewById(
        @Parameter(description = "ID персони", required = true, example = "1")
        @PathVariable Long id
    );

    @Operation(
        summary = "Отримати повну розширену інформацію про персону",
        description = """
                Повертає повну інформацію про персону з усіма зв'язками (Person + Contacts + Addresses).
                
                Використовує PersonFullExtendedDto для повного представлення даних.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Розширена інформація про персону отримана",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Персона не знайдена",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @GetMapping("/{id}/full-extended")
    ResponseEntity<AppResponse<PersonFullExtendedDto>> getFullExtendedById(
        @Parameter(description = "ID персони", required = true, example = "1")
        @PathVariable Long id
    );

    @Operation(
        summary = "Отримати FullPreview персони по ID",
        description = """
                Повертає агрегований вигляд персони з primary contacts та address.
                
                Використовується у клінічних звітах, історії пацієнтів, інтеграціях із clinic-service.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "FullPreview персони отримано",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Персона не знайдена",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @GetMapping("/{id}/full-preview")
    ResponseEntity<AppResponse<PersonFullPreviewDto>> getFullPreviewById(
        @Parameter(description = "ID персони", required = true, example = "1")
        @PathVariable Long id
    );

    @Operation(
        summary = "Створити нову персону",
        description = """
                Створює нову персону з валідацією даних.
                
                Можна створити персону разом з контактами та адресами:
                - contacts - опціонально, список контактів для створення
                - addresses - опціонально, список адрес для створення
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Персона успішно створена",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Невірні дані в запиті",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ValidationErrorResponse.class))
        )
    })
    @PostMapping
    ResponseEntity<AppResponse<PersonFullDto>> create(
        @Parameter(description = "Дані персони для створення", required = true)
        @Valid @RequestBody PersonCreateDto personDto
    );

    @Operation(
        summary = "Оновити персону",
        description = """
                Оновлює існуючу персону по ID.
                
                Всі поля опціональні (часткове оновлення):
                - Якщо поле передано - воно буде оновлено
                - Якщо поле не передано (null) - воно залишиться без змін
                
                Контакти та адреси:
                - Якщо передано contacts/addresses - всі існуючі будуть замінені на нові (replace strategy)
                - Якщо contacts/addresses не передано (null) - вони залишаться без змін
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Персона успішно оновлена",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Невірні дані в запиті",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ValidationErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Персона не знайдена",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    ResponseEntity<AppResponse<PersonFullDto>> update(
        @Parameter(description = "ID персони", required = true, example = "1")
        @PathVariable Long id,
        
        @Parameter(description = "Оновлені дані персони (всі поля опціональні, включаючи contacts та addresses)", required = true)
        @Valid @RequestBody PersonUpdateDto personDto
    );

    @Operation(
        summary = "Видалити персону",
        description = """
                Видаляє персону (soft delete за замовчуванням).
                
                Параметри:
                - hard=true - повне видалення з бази даних (hard delete)
                - hard=false або відсутній - м'яке видалення (soft delete, за замовчуванням)
                
                Увага: hard delete - операція незворотна!
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Персона успішно видалена"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Персона не знайдена",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
        @Parameter(description = "ID персони", required = true, example = "1")
        @PathVariable Long id,
        
        @Parameter(description = "Hard delete (повне видалення з БД)", required = false, example = "false")
        @RequestParam(defaultValue = "false") boolean hard
    );

    // ========== Full API (повна інформація) ==========
    
    @Operation(
        summary = "Отримати всі персони (Full)",
        description = """
                Повертає всі активні персони з контактами та адресами.
                
                Використовується у внутрішніх сценаріях (детальна картка, адміністрування, звіти).
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Список всіх персон (Full)",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        )
    })
    @GetMapping
    ResponseEntity<AppResponse<List<PersonFullDto>>> getAll();
    
    @Operation(
        summary = "Отримати всі персони (Full) з пагінацією",
        description = """
                Повертає всі активні персони з контактами та адресами з пагінацією.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Список всіх персон (Full) з пагінацією",
            content = @Content(schema = @Schema(implementation = PaginationResponse.class))
        )
    })
    @GetMapping("/paginated")
    ResponseEntity<PaginationResponse<PersonFullDto>> getAllWithPagination(
        @Parameter(description = "Параметри пагінації та сортування")
        @PageableDefault(size = 20, sort = "id") Pageable pageable
    );

    // ========== Preview API (скорочений вигляд) ==========
    
    @Operation(
        summary = "Отримати всі персони (Preview)",
        description = """
                Повертає всі активні персони у скороченому форматі.
                
                Для UI-таблиць, інтеграцій, списків власників, швидких переглядів.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Список всіх персон (Preview)",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        )
    })
    @GetMapping("/previews")
    ResponseEntity<AppResponse<List<PersonPreviewDto>>> getAllPreviews();
    
    @Operation(
        summary = "Отримати всі персони (Preview) з пагінацією",
        description = """
                Повертає всі активні персони у скороченому форматі з пагінацією.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Список всіх персон (Preview) з пагінацією",
            content = @Content(schema = @Schema(implementation = PaginationResponse.class))
        )
    })
    @GetMapping("/previews/paginated")
    ResponseEntity<PaginationResponse<PersonPreviewDto>> getAllPreviewsWithPagination(
        @Parameter(description = "Параметри пагінації та сортування")
        @PageableDefault(size = 20, sort = "id") Pageable pageable
    );
    
    @Operation(
        summary = "Отримати Preview персон по списку ID",
        description = """
                Отримує мінімальну інформацію про персон по списку ID (для мікросервісів).
                
                Альтернативний endpoint: POST /persons/previews
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Список Preview персон",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        )
    })
    @PostMapping("/previews/by-ids")
    ResponseEntity<AppResponse<List<PersonPreviewDto>>> getPreviewsByIds(
        @Parameter(description = "Список ID персон", required = true)
        @RequestBody List<Long> ids
    );

    // ========== Full Preview API (агрегований вигляд) ==========
    
    @Operation(
        summary = "Отримати всі персони (FullPreview)",
        description = """
                Повертає всі активні персони з агрегованими контактами та адресами.
                
                Комбінований набір — Person + Primary Contacts + Primary Address.
                Використовується у клінічних звітах, історії пацієнтів, інтеграціях із clinic-service.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Список всіх персон (FullPreview)",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        )
    })
    @GetMapping("/full-previews")
    ResponseEntity<AppResponse<List<PersonFullPreviewDto>>> getAllFullPreviews();
    
    @Operation(
        summary = "Отримати всі персони (FullPreview) з пагінацією",
        description = """
                Повертає всі активні персони з агрегованими контактами та адресами з пагінацією.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Список всіх персон (FullPreview) з пагінацією",
            content = @Content(schema = @Schema(implementation = PaginationResponse.class))
        )
    })
    @GetMapping("/full-previews/paginated")
    ResponseEntity<PaginationResponse<PersonFullPreviewDto>> getAllFullPreviewsWithPagination(
        @Parameter(description = "Параметри пагінації та сортування")
        @PageableDefault(size = 20, sort = "id") Pageable pageable
    );

    // ========== RSQL Search (Єдиний механізм пошуку) ==========

    @Operation(
        summary = "RSQL пошук персон з динамічним завантаженням зв'язків",
        description = "Універсальний пошук з використанням RSQL запитів та Entity Graph.\n\n" +
                "Приклади запитів:\n" +
                "- `firstName==Іван` - точний збіг\n" +
                "- `firstName=ilike='%іван%'` - case-insensitive LIKE\n" +
                "- `lastName==Петренко;" + EntityConstants.ACTIVE_RSQL_QUERY + "` - AND\n" +
                "- `firstName==Іван,lastName==Петро` - OR\n" +
                "- `contacts.value=like='%@gmail.com%'` - вкладена властивість\n" +
                "- `addresses.city==Київ;personRole==OWNER` - комбінований пошук\n" +
                "- `createdAt>2025-01-01` - порівняння дат\n" +
                "- `" + EntityConstants.ACTIVE_RSQL_QUERY + "` - тільки активні персони\n\n" +
                "Приклади graphAttributes:\n" +
                "- `contacts` - завантажити контакти\n" +
                "- `addresses` - завантажити адреси\n" +
                "- `contacts,addresses` - завантажити обидва\n\n" +
                "Замінює всі custom search endpoints (/by-name, /by-first-name, /active, тощо)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Результати пошуку",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Невірний RSQL запит",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @GetMapping("/search")
    ResponseEntity<AppResponse<List<PersonFullDto>>> search(
        @Parameter(description = "RSQL запит", required = true, example = "firstName==Іван;" + EntityConstants.ACTIVE_RSQL_QUERY)
        @RequestParam String rsqlQuery,
        
        @Parameter(description = "Атрибути для Entity Graph (опціонально)", example = "contacts,addresses")
        @RequestParam(required = false) String[] graphAttributes
    );

    @Operation(
        summary = "RSQL пошук персон з пагінацією та динамічним завантаженням зв'язків",
        description = """
                RSQL пошук з підтримкою пагінації, сортування та Entity Graph.
                
                Замінює всі paginated endpoints (/paginated, /active/paginated, тощо).
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Результати пошуку з пагінацією",
            content = @Content(schema = @Schema(implementation = PaginationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Невірний RSQL запит",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @GetMapping("/search/paginated")
    ResponseEntity<PaginationResponse<PersonFullDto>> searchWithPagination(
        @Parameter(description = "RSQL запит", required = true, example = EntityConstants.ACTIVE_RSQL_QUERY)
        @RequestParam String rsqlQuery,
        
        @Parameter(description = "Параметри пагінації та сортування")
        @PageableDefault(size = 20, sort = "id") Pageable pageable,
        
        @Parameter(description = "Атрибути для Entity Graph (опціонально, ігнорується - завжди завантажуємо " + 
                PersonEntityGraphConstants.CONTACTS + " та " + PersonEntityGraphConstants.ADDRESSES + ")", 
                example = PersonEntityGraphConstants.CONTACTS)
        @RequestParam(required = false) String[] graphAttributes
    );
    
    @Operation(
        summary = "RSQL пошук Preview персон",
        description = """
                RSQL пошук з поверненням PersonPreviewDto (скорочений формат).
                
                Призначений для швидкого пошуку з мінімальними даними.
                """
    )
    @GetMapping("/search/preview")
    ResponseEntity<AppResponse<List<PersonPreviewDto>>> searchPreview(
        @Parameter(description = "RSQL запит", required = true, example = EntityConstants.ACTIVE_RSQL_QUERY)
        @RequestParam String rsqlQuery
    );
    
    @Operation(
        summary = "RSQL пошук Preview персон з пагінацією",
        description = """
                RSQL пошук з поверненням PersonPreviewDto та пагінацією.
                """
    )
    @GetMapping("/search/preview/paginated")
    ResponseEntity<PaginationResponse<PersonPreviewDto>> searchPreviewWithPagination(
        @Parameter(description = "RSQL запит", required = true, example = EntityConstants.ACTIVE_RSQL_QUERY)
        @RequestParam String rsqlQuery,
        
        @Parameter(description = "Параметри пагінації та сортування")
        @PageableDefault(size = 20, sort = "id") Pageable pageable
    );
    
    @Operation(
        summary = "RSQL пошук FullPreview персон",
        description = """
                RSQL пошук з поверненням PersonFullPreviewDto (агрегований вигляд з primary contacts та address).
                
                Комбінований набір — Person + Primary Contacts + Primary Address.
                Використовується у клінічних звітах, історії пацієнтів, інтеграціях із clinic-service.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Результати пошуку FullPreview",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Невірний RSQL запит",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @GetMapping("/search/full-preview")
    ResponseEntity<AppResponse<List<PersonFullPreviewDto>>> searchFullPreview(
        @Parameter(description = "RSQL запит", required = true, example = EntityConstants.ACTIVE_RSQL_QUERY)
        @RequestParam String rsqlQuery
    );
    
    @Operation(
        summary = "RSQL пошук FullPreview персон з пагінацією",
        description = """
                RSQL пошук з поверненням PersonFullPreviewDto та пагінацією.
                
                Комбінований набір — Person + Primary Contacts + Primary Address.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Результати пошуку FullPreview з пагінацією",
            content = @Content(schema = @Schema(implementation = PaginationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Невірний RSQL запит",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @GetMapping("/search/full-preview/paginated")
    ResponseEntity<PaginationResponse<PersonFullPreviewDto>> searchFullPreviewWithPagination(
        @Parameter(description = "RSQL запит", required = true, example = EntityConstants.ACTIVE_RSQL_QUERY)
        @RequestParam String rsqlQuery,
        
        @Parameter(description = "Параметри пагінації та сортування")
        @PageableDefault(size = 20, sort = "id") Pageable pageable
    );
    
    // ========== Microservice Integration Methods ==========

    @Operation(
        summary = "Перевірити існування персон (для мікросервісів)",
        description = """
                Bulk перевірка існування персон по списку ID.
                
                Використовується для валідації в інших мікросервісах (Orchestrator, Pet, Clinic).
                
                Повертає Map<Long, Boolean> де:
                - key = ID персони
                - value = true якщо персона існує і активна
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Результати перевірки",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        )
    })
    @PostMapping("/check-existence")
    ResponseEntity<AppResponse<Map<Long, Boolean>>> checkPersonsExist(
        @Parameter(description = "Список ID персон для перевірки", required = true)
        @RequestBody List<Long> ids
    );
    
    // ========== Statistics ==========
    
    @Operation(
        summary = "Підрахунок активних персон",
        description = """
                Повертає кількість активних (не видалених) персон.
                
                Використовується для статистики та моніторингу.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Кількість активних персон",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        )
    })
    @GetMapping("/count/active")
    ResponseEntity<AppResponse<Long>> countActive();
}

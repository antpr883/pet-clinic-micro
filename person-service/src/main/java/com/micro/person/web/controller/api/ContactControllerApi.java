package com.micro.person.web.controller.api;

import com.micro.person.data.constants.EntityConstants;

import com.micro.person.data.dto.contact.ContactCreateDto;
import com.micro.person.data.dto.contact.ContactDto;
import com.micro.person.data.dto.contact.ContactPreviewDto;
import com.micro.person.data.dto.contact.ContactUpdateDto;
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

/**
 * Оптимізований API інтерфейс для управління контактами.
 * 
 * Версія: 1.0 (Optimized)
 * 
 * Філософія: "Один сервіс — мінімум API — максимум функціональності."
 * 
 * Основні принципи:
 * - RSQL як єдиний механізм пошуку
 * - Мінімальний набір endpoints без дублікатів
 * - RESTful структура з підресурсами
 * 
 * @see com.micro.person.web.controller.ContactController
 */
@Tag(
    name = "Contact Management",
    description = "Оптимізований API для управління контактами: CRUD операції, RSQL пошук"
)
public interface ContactControllerApi {

    // ========== CRUD Operations ==========

    @Operation(
        summary = "Отримати контакт по ID",
        description = "Повертає повну інформацію про контакт."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Контакт знайдено",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Контакт не знайдено",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    ResponseEntity<AppResponse<ContactDto>> getById(
        @Parameter(description = "ID контакту", required = true, example = "1")
        @PathVariable Long id
    );

    @Operation(
        summary = "Створити новий контакт",
        description = "Створює новий контакт з валідацією даних."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Контакт успішно створено",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Невірні дані в запиті",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ValidationErrorResponse.class))
        )
    })
    @PostMapping
    ResponseEntity<AppResponse<ContactDto>> create(
        @Parameter(description = "Дані контакту для створення", required = true)
        @Valid @RequestBody ContactCreateDto contactDto
    );

    @Operation(
        summary = "Оновити контакт",
        description = "Оновлює існуючий контакт по ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Контакт успішно оновлено",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Невірні дані в запиті",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ValidationErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Контакт не знайдено",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    ResponseEntity<AppResponse<ContactDto>> update(
        @Parameter(description = "ID контакту", required = true, example = "1")
        @PathVariable Long id,
        
        @Parameter(description = "Оновлені дані контакту", required = true)
        @Valid @RequestBody ContactUpdateDto contactDto
    );

    @Operation(
        summary = "Видалити контакт",
        description = """
                Видаляє контакт (soft delete за замовчуванням).
                
                Параметри:
                - hard=true - повне видалення з бази даних (hard delete)
                - hard=false або відсутній - м'яке видалення (soft delete, за замовчуванням)
                
                Увага: hard delete - операція незворотна!
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Контакт успішно видалено"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Контакт не знайдено",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
        @Parameter(description = "ID контакту", required = true, example = "1")
        @PathVariable Long id,
        
        @Parameter(description = "Hard delete (повне видалення з БД)", required = false, example = "false")
        @RequestParam(defaultValue = "false") boolean hard
    );

    // ========== RSQL Search (Єдиний механізм пошуку) ==========

    @Operation(
        summary = "RSQL пошук контактів",
        description = "Універсальний пошук контактів з використанням RSQL запитів.\n\n" +
                "Приклади запитів:\n" +
                "- `contactType==EMAIL` - по типу\n" +
                "- `value=ilike='%@gmail.com%'` - case-insensitive LIKE\n" +
                "- `person.id==1` - по персоні\n" +
                "- `isPrimary==true;" + EntityConstants.ACTIVE_RSQL_QUERY + "` - AND\n" +
                "- `value==+380123456789` - точний збіг\n" +
                "- `" + EntityConstants.ACTIVE_RSQL_QUERY + "` - тільки активні контакти\n\n" +
                "Замінює всі custom search endpoints (/by-email, /by-phone, /active, тощо)."
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
    ResponseEntity<AppResponse<List<ContactDto>>> search(
        @Parameter(description = "RSQL запит", required = true, example = "contactType==EMAIL;" + EntityConstants.ACTIVE_RSQL_QUERY)
        @RequestParam String rsqlQuery
    );

    @Operation(
        summary = "RSQL пошук контактів з пагінацією",
        description = """
                RSQL пошук контактів з підтримкою пагінації та сортування.
                
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
    ResponseEntity<PaginationResponse<ContactDto>> searchWithPagination(
        @Parameter(description = "RSQL запит", required = true, example = EntityConstants.ACTIVE_RSQL_QUERY)
        @RequestParam String rsqlQuery,
        
        @Parameter(description = "Параметри пагінації та сортування")
        @PageableDefault(size = 20, sort = "id") Pageable pageable
    );

    // ========== Person-specific Methods (підресурси) ==========

    @Operation(
        summary = "Отримати контакти персони",
        description = """
                Повертає всі контакти вказаної персони.
                
                Використовується як підресурс персони.
                Альтернатива через RSQL: /contacts/search?rsqlQuery=person.id=={personId}
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Список контактів персони",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        )
    })
    @GetMapping("/persons/{personId}")
    ResponseEntity<AppResponse<List<ContactDto>>> findByPersonId(
        @Parameter(description = "ID персони", required = true, example = "1")
        @PathVariable Long personId
    );

    @Operation(
        summary = "Отримати основний контакт персони",
        description = """
                Повертає основний (primary) контакт персони, якщо він існує.
                
                Альтернатива через RSQL: /contacts/search?rsqlQuery=person.id=={personId};isPrimary==true
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Основний контакт знайдено або не знайдено",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        )
    })
    @GetMapping("/persons/{personId}/primary")
    ResponseEntity<AppResponse<ContactPreviewDto>> findPrimaryByPersonId(
        @Parameter(description = "ID персони", required = true, example = "1")
        @PathVariable Long personId
    );
}

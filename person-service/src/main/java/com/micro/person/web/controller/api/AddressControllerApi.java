package com.micro.person.web.controller.api;

import com.micro.person.data.constants.EntityConstants;

import com.micro.person.data.dto.address.AddressCreateDto;
import com.micro.person.data.dto.address.AddressDto;
import com.micro.person.data.dto.address.AddressPreviewDto;
import com.micro.person.data.dto.address.AddressUpdateDto;
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
 * Оптимізований API інтерфейс для управління адресами.
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
 * @see com.micro.person.web.controller.AddressController
 */
@Tag(
    name = "Address Management",
    description = "API для управління адресами персон: CRUD операції, пошук, пагінація"
)
public interface AddressControllerApi {

    // ========== CRUD Operations ==========

    @Operation(
        summary = "Отримати адресу по ID",
        description = "Повертає повну інформацію про адресу."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Адреса знайдена",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Адреса не знайдена",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    ResponseEntity<AppResponse<AddressDto>> getById(
        @Parameter(description = "ID адреси", required = true, example = "1")
        @PathVariable Long id
    );

    @Operation(
        summary = "Створити нову адресу",
        description = "Створює нову адресу з валідацією даних."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Адреса успішно створена",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Невірні дані в запиті",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ValidationErrorResponse.class))
        )
    })
    @PostMapping
    ResponseEntity<AppResponse<AddressDto>> create(
        @Parameter(description = "Дані адреси для створення", required = true)
        @Valid @RequestBody AddressCreateDto addressDto
    );

    @Operation(
        summary = "Оновити адресу",
        description = "Оновлює існуючу адресу по ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Адреса успішно оновлена",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Невірні дані в запиті",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ValidationErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Адреса не знайдена",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    ResponseEntity<AppResponse<AddressDto>> update(
        @Parameter(description = "ID адреси", required = true, example = "1")
        @PathVariable Long id,
        
        @Parameter(description = "Оновлені дані адреси", required = true)
        @Valid @RequestBody AddressUpdateDto addressDto
    );

    @Operation(
        summary = "Видалити адресу (soft або hard delete)",
        description = """
                Видаляє адресу з бази даних.
                За замовчуванням виконується м'яке видалення (soft delete).
                Для повного видалення (hard delete) встановіть параметр `hard=true`.
                Увага: hard delete незворотний!
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Адреса успішно видалена"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Адреса не знайдена",
            content = @Content(schema = @Schema(implementation = com.micro.person.web.response.ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
        @Parameter(description = "ID адреси", required = true, example = "1")
        @PathVariable Long id,
        
        @Parameter(description = "Виконати повне видалення (true) або м'яке (false, за замовчуванням)", required = false, example = "false")
        @RequestParam(defaultValue = "false") boolean hard
    );

    // ========== RSQL Search (Єдиний механізм пошуку) ==========

    @Operation(
        summary = "RSQL пошук адрес",
        description = "Універсальний пошук адрес з використанням RSQL запитів.\n\n" +
                "Приклади запитів:\n" +
                "- `addressType==HOME` - по типу\n" +
                "- `city=ilike='%київ%'` - case-insensitive LIKE\n" +
                "- `person.id==1` - по персоні\n" +
                "- `country==Україна;" + EntityConstants.ACTIVE_RSQL_QUERY + "` - AND\n" +
                "- `city==Київ` - точний збіг\n\n" +
                "Замінює всі custom search endpoints (/by-city, /by-country, /active, тощо)."
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
    ResponseEntity<AppResponse<List<AddressDto>>> search(
        @Parameter(description = "RSQL запит", required = true, example = "addressType==HOME")
        @RequestParam String rsqlQuery
    );

    @Operation(
        summary = "RSQL пошук адрес з пагінацією",
        description = """
                RSQL пошук адрес з підтримкою пагінації та сортування.
                
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
    ResponseEntity<PaginationResponse<AddressDto>> searchWithPagination(
        @Parameter(description = "RSQL запит", required = true, example = EntityConstants.ACTIVE_RSQL_QUERY)
        @RequestParam String rsqlQuery,
        
        @Parameter(description = "Параметри пагінації та сортування")
        @PageableDefault(size = 20, sort = "id") Pageable pageable
    );

    // ========== Person-specific Methods (підресурси) ==========

    @Operation(
        summary = "Отримати адреси персони",
        description = """
                Повертає всі адреси вказаної персони.
                
                Використовується як підресурс персони.
                Альтернатива через RSQL: /addresses/search?rsqlQuery=person.id=={personId}
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Список адрес персони",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        )
    })
    @GetMapping("/persons/{personId}")
    ResponseEntity<AppResponse<List<AddressDto>>> findByPersonId(
        @Parameter(description = "ID персони", required = true, example = "1")
        @PathVariable Long personId
    );

    @Operation(
        summary = "Отримати основну адресу персони",
        description = """
                Повертає основну (HOME) адресу персони, якщо вона існує.
                
                Альтернатива через RSQL: /addresses/search?rsqlQuery=person.id=={personId};addressType==HOME
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Основна адреса знайдена або не знайдена",
            content = @Content(schema = @Schema(implementation = AppResponse.class))
        )
    })
    @GetMapping("/persons/{personId}/primary")
    ResponseEntity<AppResponse<AddressPreviewDto>> findPrimaryByPersonId(
        @Parameter(description = "ID персони", required = true, example = "1")
        @PathVariable Long personId
    );
}


package com.micro.person.web.controller;

import com.micro.person.data.dto.contact.ContactCreateDto;
import com.micro.person.data.dto.contact.ContactDto;
import com.micro.person.data.dto.contact.ContactPreviewDto;
import com.micro.person.data.dto.contact.ContactUpdateDto;
import com.micro.person.service.contact.ContactService;
import com.micro.person.web.controller.api.ContactControllerApi;
import com.micro.person.web.response.AppResponse;
import com.micro.person.web.response.PaginationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Оптимізований REST контроллер для управління контактами.
 * 
 * Версія: 1.0 (Optimized)
 * 
 * Реалізує ContactControllerApi з мінімальним набором endpoints:
 * - CRUD операції
 * - RSQL як єдиний механізм пошуку
 * - Підресурси персони (/persons/{personId}/contacts)
 * 
 * Видалено дублікати:
 * - /active, /active/paginated → RSQL active==true
 * - /paginated → RSQL з пагінацією
 * - /search/by-email, /by-phone → RSQL запити
 * - /by-emails, /by-phones, /check-exist → RSQL bulk запити
 * - /persons/{id}/by-type → RSQL person.id==1;contactType==EMAIL
 * - /soft → об'єднано з DELETE через параметр hard
 */
@Slf4j
@RestController
@RequestMapping("${end.point.contacts}")
@RequiredArgsConstructor
@Tag(name = "Contact Management", description = "Оптимізований API для управління контактами")
public class ContactController implements ContactControllerApi {

    private final ContactService contactService;

    @Override
    public ResponseEntity<AppResponse<ContactDto>> getById(@PathVariable Long id) {
        log.debug("Getting contact by id: {}", id);
        ContactDto dto = contactService.findById(id);
        return ResponseEntity.ok(AppResponse.successful(dto));
    }

    @Override
    public ResponseEntity<AppResponse<ContactDto>> create(@Valid @RequestBody ContactCreateDto contactDto) {
        log.debug("Creating contact: {}", contactDto);
        ContactDto created = contactService.create(contactDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppResponse.successful(created));
    }

    @Override
    public ResponseEntity<AppResponse<ContactDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody ContactUpdateDto contactDto) {
        log.debug("Updating contact id: {} with data: {}", id, contactDto);
        ContactDto updated = contactService.update(id, contactDto);
        return ResponseEntity.ok(AppResponse.successful(updated));
    }

    @Override
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean hard) {
        log.debug("Deleting contact id: {} (hard={})", id, hard);
        if (hard) {
            contactService.delete(id);
        } else {
            contactService.softDelete(id);
        }
        return ResponseEntity.noContent().build();
    }

    // ========== RSQL Search (Єдиний механізм пошуку) ==========

    @Override
    public ResponseEntity<AppResponse<List<ContactDto>>> search(@RequestParam String rsqlQuery) {
        log.debug("RSQL search with query: {}", rsqlQuery);
        List<ContactDto> dtos = contactService.search(rsqlQuery);
        return ResponseEntity.ok(AppResponse.successful(dtos));
    }

    @Override
    public ResponseEntity<PaginationResponse<ContactDto>> searchWithPagination(
            @RequestParam String rsqlQuery,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("RSQL search with pagination: query={}, page={}, size={}", 
                rsqlQuery, pageable.getPageNumber(), pageable.getPageSize());
        Page<ContactDto> page = contactService.searchWithPagination(rsqlQuery, pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }

    // ========== Person-specific Methods (підресурси) ==========

    @Override
    public ResponseEntity<AppResponse<List<ContactDto>>> findByPersonId(@PathVariable Long personId) {
        log.debug("Finding contacts by personId: {}", personId);
        List<ContactDto> dtos = contactService.findByPersonId(personId);
        return ResponseEntity.ok(AppResponse.successful(dtos));
    }

    @Override
    public ResponseEntity<AppResponse<ContactPreviewDto>> findPrimaryByPersonId(@PathVariable Long personId) {
        log.debug("Finding primary contact by personId: {}", personId);
        Optional<ContactPreviewDto> contact = contactService.findPrimaryPreviewByPersonId(personId);
        return contact.map(dto -> ResponseEntity.ok(AppResponse.successful(dto)))
                .orElse(ResponseEntity.ok(AppResponse.successful(null)));
    }
}

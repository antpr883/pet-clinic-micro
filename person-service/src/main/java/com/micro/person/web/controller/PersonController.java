package com.micro.person.web.controller;

import com.micro.person.data.dto.person.PersonCreateDto;
import com.micro.person.data.dto.person.PersonFullDto;
import com.micro.person.data.dto.person.PersonFullExtendedDto;
import com.micro.person.data.dto.person.PersonFullPreviewDto;
import com.micro.person.data.dto.person.PersonPreviewDto;
import com.micro.person.data.dto.person.PersonUpdateDto;
import com.micro.person.service.person.PersonService;
import com.micro.person.web.controller.api.PersonControllerApi;
import com.micro.person.web.response.AppResponse;
import com.micro.person.web.response.PaginationResponse;
import com.micro.security.client.permissions.Permissions;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Optimized REST controller for managing persons.
 * 
 * Version: 1.0 (Optimized)
 * 
 * Implements PersonControllerApi with minimal set of endpoints:
 * - CRUD operations
 * - RSQL as the only search mechanism
 * - Microservice integration
 * 
 * Removed duplicates:
 * - /active, /active/paginated → RSQL active==true
 * - /paginated → RSQL with pagination
 * - /search/by-* → RSQL queries
 * - /soft → merged with DELETE via hard parameter
 */
@Slf4j
@RestController
@RequestMapping("${end.point.persons}")
@RequiredArgsConstructor
@Tag(name = "Person Management", description = "Optimized API for managing persons")
public class PersonController implements PersonControllerApi {

    private final PersonService personService;

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<PersonFullDto>> getById(@PathVariable Long id) {
        log.debug("Getting full person by id: {}", id);
        PersonFullDto dto = personService.findFullById(id);
        return ResponseEntity.ok(AppResponse.successful(dto));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<PersonPreviewDto>> getPreviewById(@PathVariable Long id) {
        log.debug("Getting preview person by id: {}", id);
        PersonPreviewDto dto = personService.findPreviewById(id);
        return ResponseEntity.ok(AppResponse.successful(dto));
    }
    
    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<PersonFullExtendedDto>> getFullExtendedById(@PathVariable Long id) {
        log.debug("Getting full extended person by id: {}", id);
        PersonFullExtendedDto dto = personService.findFullExtendedById(id);
        return ResponseEntity.ok(AppResponse.successful(dto));
    }
    
    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<PersonFullPreviewDto>> getFullPreviewById(@PathVariable Long id) {
        log.debug("Getting full preview person by id: {}", id);
        PersonFullPreviewDto dto = personService.findFullPreviewById(id);
        return ResponseEntity.ok(AppResponse.successful(dto));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_WRITE + "')")
    public ResponseEntity<AppResponse<PersonFullDto>> create(@Valid @RequestBody PersonCreateDto personDto) {
        log.debug("Creating person: {}", personDto);
        PersonFullDto created = personService.create(personDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppResponse.successful(created));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_WRITE + "')")
    public ResponseEntity<AppResponse<PersonFullDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody PersonUpdateDto personDto) {
        log.debug("Updating person id: {} with data: {}", id, personDto);
        PersonFullDto updated = personService.update(id, personDto);
        return ResponseEntity.ok(AppResponse.successful(updated));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_WRITE + "')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean hard) {
        log.debug("Deleting person id: {} (hard={})", id, hard);
        if (hard) {
            personService.delete(id);
        } else {
            personService.softDelete(id);
        }
        return ResponseEntity.noContent().build();
    }

    // ========== Full API (full information) ==========
    
    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<List<PersonFullDto>>> getAll() {
        log.debug("Getting all full persons");
        List<PersonFullDto> dtos = personService.findAllFull();
        return ResponseEntity.ok(AppResponse.successful(dtos));
    }
    
    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<PaginationResponse<PersonFullDto>> getAllWithPagination(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("Getting all full persons with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        Page<PersonFullDto> page = personService.findAllFullWithPagination(pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }

    // ========== Preview API (short view) ==========
    
    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<List<PersonPreviewDto>>> getAllPreviews() {
        log.debug("Getting all preview persons");
        List<PersonPreviewDto> dtos = personService.findAllPreviews();
        return ResponseEntity.ok(AppResponse.successful(dtos));
    }
    
    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<PaginationResponse<PersonPreviewDto>> getAllPreviewsWithPagination(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("Getting all preview persons with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        Page<PersonPreviewDto> page = personService.findAllPreviewsWithPagination(pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }

    // ========== Full Preview API (aggregated view) ==========
    
    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<List<PersonFullPreviewDto>>> getAllFullPreviews() {
        log.debug("Getting all full preview persons");
        List<PersonFullPreviewDto> dtos = personService.findAllFullPreviews();
        return ResponseEntity.ok(AppResponse.successful(dtos));
    }
    
    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<PaginationResponse<PersonFullPreviewDto>> getAllFullPreviewsWithPagination(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("Getting all full preview persons with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        Page<PersonFullPreviewDto> page = personService.findAllFullPreviewsWithPagination(pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }

    // ========== RSQL Search (Single search mechanism) ==========

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<List<PersonFullDto>>> search(
            @RequestParam String rsqlQuery,
            @RequestParam(required = false) String[] graphAttributes) {
        log.debug("RSQL search with query: {} (always with contacts and addresses)", rsqlQuery);
        // graphAttributes is ignored - always load contacts and addresses
        List<PersonFullDto> dtos = personService.search(rsqlQuery);
        return ResponseEntity.ok(AppResponse.successful(dtos));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<PaginationResponse<PersonFullDto>> searchWithPagination(
            @RequestParam String rsqlQuery,
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) String[] graphAttributes) {
        log.debug("RSQL search with pagination: query={}, page={}, size={} (always with contacts and addresses)", 
                rsqlQuery, pageable.getPageNumber(), pageable.getPageSize());
        // graphAttributes is ignored - always load contacts and addresses
        Page<PersonFullDto> page = personService.searchWithPagination(rsqlQuery, pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }
    
    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<List<PersonPreviewDto>>> searchPreview(@RequestParam String rsqlQuery) {
        log.debug("RSQL search preview with query: {}", rsqlQuery);
        List<PersonPreviewDto> dtos = personService.searchPreview(rsqlQuery);
        return ResponseEntity.ok(AppResponse.successful(dtos));
    }
    
    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<PaginationResponse<PersonPreviewDto>> searchPreviewWithPagination(
            @RequestParam String rsqlQuery,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("RSQL search preview with pagination: query={}, page={}, size={}", 
                rsqlQuery, pageable.getPageNumber(), pageable.getPageSize());
        Page<PersonPreviewDto> page = personService.searchPreviewWithPagination(rsqlQuery, pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }
    
    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<List<PersonFullPreviewDto>>> searchFullPreview(@RequestParam String rsqlQuery) {
        log.debug("RSQL search full preview with query: {}", rsqlQuery);
        List<PersonFullPreviewDto> dtos = personService.searchFullPreview(rsqlQuery);
        return ResponseEntity.ok(AppResponse.successful(dtos));
    }
    
    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<PaginationResponse<PersonFullPreviewDto>> searchFullPreviewWithPagination(
            @RequestParam String rsqlQuery,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("RSQL search full preview with pagination: query={}, page={}, size={}", 
                rsqlQuery, pageable.getPageNumber(), pageable.getPageSize());
        Page<PersonFullPreviewDto> page = personService.searchFullPreviewWithPagination(rsqlQuery, pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }

    // ========== Microservice Integration Methods ==========
    // Ці методи використовуються для service-to-service викликів
    // Можуть використовувати ORCHESTRATOR роль або спеціальні permissions

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<Map<Long, Boolean>>> checkPersonsExist(
            @RequestBody List<Long> ids) {
        log.debug("Checking existence of persons: {}", ids);
        Map<Long, Boolean> result = personService.checkPersonsExist(ids);
        return ResponseEntity.ok(AppResponse.successful(result));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<List<PersonPreviewDto>>> getPreviewsByIds(
            @RequestBody List<Long> ids) {
        log.debug("Getting previews for persons: {}", ids);
        List<PersonPreviewDto> previews = personService.getPreviewsByIds(ids);
        return ResponseEntity.ok(AppResponse.successful(previews));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PERSON_READ + "')")
    public ResponseEntity<AppResponse<Long>> countActive() {
        log.debug("Counting active persons");
        long count = personService.countActive();
        return ResponseEntity.ok(AppResponse.successful(count));
    }
}

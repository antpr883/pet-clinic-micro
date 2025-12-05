package com.micro.pet.web.controller;

import com.micro.pet.data.constants.EntityConstants;
import com.micro.pet.data.dto.pet.*;
import com.micro.pet.service.pet.PetService;
import com.micro.pet.web.controller.api.PetControllerApi;
import com.micro.pet.web.response.AppResponse;
import com.micro.pet.web.response.PaginationResponse;
import com.micro.security.client.permissions.Permissions;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing pets.
 */
@Slf4j
@RestController
@RequestMapping("${end.point.pets}")
@RequiredArgsConstructor
@Tag(name = "Pet Management", description = "API for managing pets")
public class PetController implements PetControllerApi {

    private final PetService petService;

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<AppResponse<PetFullDto>> getById(@PathVariable Long id) {
        log.debug("Getting full pet by id: {}", id);
        PetFullDto dto = petService.findFullById(id);
        return ResponseEntity.ok(AppResponse.successful(dto));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<AppResponse<PetPreviewDto>> getPreviewById(@PathVariable Long id) {
        log.debug("Getting preview pet by id: {}", id);
        PetPreviewDto dto = petService.findPreviewById(id);
        return ResponseEntity.ok(AppResponse.successful(dto));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_WRITE + "')")
    public ResponseEntity<AppResponse<PetFullDto>> create(@Valid @RequestBody PetCreateDto petDto) {
        log.debug("Creating pet: {}", petDto);
        PetFullDto created = petService.create(petDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppResponse.successful(created));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_WRITE + "')")
    public ResponseEntity<AppResponse<PetFullDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody PetUpdateDto petDto) {
        log.debug("Updating pet id: {} with data: {}", id, petDto);
        PetFullDto updated = petService.update(id, petDto);
        return ResponseEntity.ok(AppResponse.successful(updated));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_WRITE + "')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean hard) {
        log.debug("Deleting pet id: {}, hard: {}", id, hard);
        if (hard) {
            petService.delete(id);
        } else {
            petService.softDelete(id);
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<AppResponse<List<PetFullDto>>> getAll() {
        log.debug("Getting all full pets");
        List<PetFullDto> pets = petService.search(EntityConstants.ACTIVE_RSQL_QUERY);
        return ResponseEntity.ok(AppResponse.successful(pets));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<PaginationResponse<PetFullDto>> getAllWithPagination(Pageable pageable) {
        log.debug("Getting all full pets with pagination: {}", pageable);
        Page<PetFullDto> page = petService.searchWithPagination(EntityConstants.ACTIVE_RSQL_QUERY, pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<AppResponse<List<PetPreviewDto>>> getAllPreviews() {
        log.debug("Getting all preview pets");
        List<PetPreviewDto> pets = petService.searchPreview(EntityConstants.ACTIVE_RSQL_QUERY);
        return ResponseEntity.ok(AppResponse.successful(pets));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<PaginationResponse<PetPreviewDto>> getAllPreviewsWithPagination(Pageable pageable) {
        log.debug("Getting all preview pets with pagination: {}", pageable);
        Page<PetPreviewDto> page = petService.searchPreviewWithPagination(EntityConstants.ACTIVE_RSQL_QUERY, pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<AppResponse<List<PetFullDto>>> search(@RequestParam String rsqlQuery) {
        log.debug("Searching pets with RSQL query: {}", rsqlQuery);
        List<PetFullDto> pets = petService.search(rsqlQuery);
        return ResponseEntity.ok(AppResponse.successful(pets));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<PaginationResponse<PetFullDto>> searchWithPagination(
            @RequestParam String rsqlQuery,
            Pageable pageable) {
        log.debug("Searching pets with RSQL query: {}, pagination: {}", rsqlQuery, pageable);
        Page<PetFullDto> page = petService.searchWithPagination(rsqlQuery, pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<AppResponse<List<PetPreviewDto>>> searchPreview(@RequestParam String rsqlQuery) {
        log.debug("Searching preview pets with RSQL query: {}", rsqlQuery);
        List<PetPreviewDto> pets = petService.searchPreview(rsqlQuery);
        return ResponseEntity.ok(AppResponse.successful(pets));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<PaginationResponse<PetPreviewDto>> searchPreviewWithPagination(
            @RequestParam String rsqlQuery,
            Pageable pageable) {
        log.debug("Searching preview pets with RSQL query: {}, pagination: {}", rsqlQuery, pageable);
        Page<PetPreviewDto> page = petService.searchPreviewWithPagination(rsqlQuery, pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<AppResponse<List<PetPreviewDto>>> getByOwnerId(@PathVariable Long ownerId) {
        log.debug("Getting pets by ownerId: {}", ownerId);
        List<PetPreviewDto> pets = petService.findByOwnerId(ownerId);
        return ResponseEntity.ok(AppResponse.successful(pets));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<AppResponse<Map<Long, Boolean>>> checkExistence(@RequestBody List<Long> ids) {
        log.debug("Checking existence of {} pets", ids.size());
        Map<Long, Boolean> result = petService.checkExistence(ids);
        return ResponseEntity.ok(AppResponse.successful(result));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<AppResponse<List<PetPreviewDto>>> getPreviewsByIds(@RequestBody List<Long> ids) {
        log.debug("Getting previews for {} pets", ids.size());
        List<PetPreviewDto> pets = petService.findPreviewsByIds(ids);
        return ResponseEntity.ok(AppResponse.successful(pets));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<AppResponse<Long>> countActive() {
        log.debug("Counting active pets");
        long count = petService.countActive();
        return ResponseEntity.ok(AppResponse.successful(count));
    }

    @Override
    @PreAuthorize("hasAuthority('" + Permissions.PET_READ + "')")
    public ResponseEntity<AppResponse<Map<String, Long>>> countByType() {
        log.debug("Counting pets by type");
        Map<String, Long> result = petService.countByType();
        return ResponseEntity.ok(AppResponse.successful(result));
    }
}


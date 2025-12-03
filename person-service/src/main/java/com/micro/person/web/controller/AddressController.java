package com.micro.person.web.controller;

import com.micro.person.data.dto.address.AddressCreateDto;
import com.micro.person.data.dto.address.AddressDto;
import com.micro.person.data.dto.address.AddressPreviewDto;
import com.micro.person.data.dto.address.AddressUpdateDto;
import com.micro.person.service.address.AddressService;
import com.micro.person.web.controller.api.AddressControllerApi;
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
 * Оптимізований REST контроллер для управління адресами.
 * 
 * Версія: 1.0 (Optimized)
 * 
 * Реалізує AddressControllerApi з мінімальним набором endpoints:
 * - CRUD операції
 * - RSQL як єдиний механізм пошуку
 * - Підресурси персони (/persons/{personId}/addresses)
 */
@Slf4j
@RestController
@RequestMapping("${end.point.addresses}")
@RequiredArgsConstructor
@Tag(name = "Address Management", description = "Оптимізований API для управління адресами")
public class AddressController implements AddressControllerApi {

    private final AddressService addressService;

    @Override
    public ResponseEntity<AppResponse<AddressDto>> getById(@PathVariable Long id) {
        log.debug("Getting address by id: {}", id);
        AddressDto dto = addressService.findById(id);
        return ResponseEntity.ok(AppResponse.successful(dto));
    }

    @Override
    public ResponseEntity<AppResponse<AddressDto>> create(@Valid @RequestBody AddressCreateDto addressDto) {
        log.debug("Creating address: {}", addressDto);
        AddressDto created = addressService.create(addressDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppResponse.successful(created));
    }

    @Override
    public ResponseEntity<AppResponse<AddressDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody AddressUpdateDto addressDto) {
        log.debug("Updating address id: {} with data: {}", id, addressDto);
        AddressDto updated = addressService.update(id, addressDto);
        return ResponseEntity.ok(AppResponse.successful(updated));
    }

    @Override
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean hard) {
        log.debug("Deleting address id: {} (hard={})", id, hard);
        if (hard) {
            addressService.delete(id);
        } else {
            addressService.softDelete(id);
        }
        return ResponseEntity.noContent().build();
    }

    // ========== RSQL Search (Єдиний механізм пошуку) ==========

    @Override
    public ResponseEntity<AppResponse<List<AddressDto>>> search(@RequestParam String rsqlQuery) {
        log.debug("RSQL search with query: {}", rsqlQuery);
        List<AddressDto> dtos = addressService.search(rsqlQuery);
        return ResponseEntity.ok(AppResponse.successful(dtos));
    }

    @Override
    public ResponseEntity<PaginationResponse<AddressDto>> searchWithPagination(
            @RequestParam String rsqlQuery,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("RSQL search with pagination: query={}, page={}, size={}", 
                rsqlQuery, pageable.getPageNumber(), pageable.getPageSize());
        Page<AddressDto> page = addressService.searchWithPagination(rsqlQuery, pageable);
        return ResponseEntity.ok(PaginationResponse.of(page));
    }

    // ========== Person-specific Methods (підресурси) ==========

    @Override
    public ResponseEntity<AppResponse<List<AddressDto>>> findByPersonId(@PathVariable Long personId) {
        log.debug("Finding addresses by personId: {}", personId);
        List<AddressDto> dtos = addressService.findByPersonId(personId);
        return ResponseEntity.ok(AppResponse.successful(dtos));
    }

    @Override
    public ResponseEntity<AppResponse<AddressPreviewDto>> findPrimaryByPersonId(@PathVariable Long personId) {
        log.debug("Finding primary address by personId: {}", personId);
        Optional<AddressPreviewDto> address = addressService.findPrimaryPreviewByPersonId(personId);
        return address.map(dto -> ResponseEntity.ok(AppResponse.successful(dto)))
                .orElse(ResponseEntity.ok(AppResponse.successful(null)));
    }
}


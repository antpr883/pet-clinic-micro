package com.micro.pet.web.controller;

import com.micro.pet.data.dto.pettype.*;
import com.micro.pet.service.pettype.PetTypeService;
import com.micro.pet.web.response.AppResponse;
import com.micro.pet.web.response.PaginationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing pet types.
 */
@Slf4j
@RestController
@RequestMapping("${end.point.petTypes}")
@RequiredArgsConstructor
@Tag(name = "Pet Type Management", description = "API for managing pet types")
public class PetTypeController {

    private final PetTypeService petTypeService;

    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<PetTypeDto>> getById(@PathVariable Long id) {
        log.debug("Getting pet type by id: {}", id);
        PetTypeDto dto = petTypeService.findById(id);
        return ResponseEntity.ok(AppResponse.successful(dto));
    }

    @GetMapping
    public ResponseEntity<AppResponse<List<PetTypeDto>>> getAll() {
        log.debug("Getting all pet types");
        List<PetTypeDto> types = petTypeService.findAllActive();
        return ResponseEntity.ok(AppResponse.successful(types));
    }

    @GetMapping("/paginated")
    public ResponseEntity<PaginationResponse<PetTypeDto>> getAllWithPagination(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("Getting all pet types with pagination: {}", pageable);
        return ResponseEntity.ok(PaginationResponse.of(petTypeService.findAllActive(pageable)));
    }

    @PostMapping
    public ResponseEntity<AppResponse<PetTypeDto>> create(@Valid @RequestBody PetTypeCreateDto createDto) {
        log.debug("Creating pet type: {}", createDto);
        PetTypeDto created = petTypeService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppResponse.successful(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppResponse<PetTypeDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody PetTypeUpdateDto updateDto) {
        log.debug("Updating pet type id: {} with data: {}", id, updateDto);
        PetTypeDto updated = petTypeService.update(id, updateDto);
        return ResponseEntity.ok(AppResponse.successful(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean hard) {
        log.debug("Deleting pet type id: {}, hard: {}", id, hard);
        if (hard) {
            petTypeService.delete(id);
        } else {
            petTypeService.softDelete(id);
        }
        return ResponseEntity.noContent().build();
    }
}


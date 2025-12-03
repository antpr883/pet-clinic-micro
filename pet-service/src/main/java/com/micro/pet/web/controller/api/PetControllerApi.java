package com.micro.pet.web.controller.api;

import com.micro.pet.data.dto.pet.*;
import com.micro.pet.web.response.AppResponse;
import com.micro.pet.web.response.PaginationResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API interface for managing pets.
 */
@Tag(
    name = "Pet Management",
    description = "API for managing pets: CRUD operations, RSQL search, microservice integration"
)
public interface PetControllerApi {

    // ========== CRUD Operations ==========

    @Operation(summary = "Get pet by ID", description = "Returns full pet information with type and details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pet found"),
        @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<AppResponse<PetFullDto>> getById(@PathVariable Long id);

    @Operation(summary = "Get pet preview by ID")
    @GetMapping("/{id}/preview")
    ResponseEntity<AppResponse<PetPreviewDto>> getPreviewById(@PathVariable Long id);

    @Operation(summary = "Create new pet")
    @PostMapping
    ResponseEntity<AppResponse<PetFullDto>> create(@Valid @RequestBody PetCreateDto petDto);

    @Operation(summary = "Update pet")
    @PutMapping("/{id}")
    ResponseEntity<AppResponse<PetFullDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody PetUpdateDto petDto);

    @Operation(summary = "Delete pet")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean hard);

    // ========== List All Methods ==========
    
    @Operation(summary = "Get all pets (Full)")
    @GetMapping
    ResponseEntity<AppResponse<List<PetFullDto>>> getAll();
    
    @Operation(summary = "Get all pets (Full) with pagination")
    @GetMapping("/paginated")
    ResponseEntity<PaginationResponse<PetFullDto>> getAllWithPagination(
            @PageableDefault(size = 20, sort = "id") Pageable pageable);

    // ========== Preview API ==========
    
    @Operation(summary = "Get all pets (Preview)")
    @GetMapping("/previews")
    ResponseEntity<AppResponse<List<PetPreviewDto>>> getAllPreviews();
    
    @Operation(summary = "Get all pets (Preview) with pagination")
    @GetMapping("/previews/paginated")
    ResponseEntity<PaginationResponse<PetPreviewDto>> getAllPreviewsWithPagination(
            @PageableDefault(size = 20, sort = "id") Pageable pageable);

    // ========== RSQL Search ==========

    @Operation(summary = "RSQL search for pets")
    @GetMapping("/search")
    ResponseEntity<AppResponse<List<PetFullDto>>> search(
            @RequestParam String rsqlQuery);

    @Operation(summary = "RSQL search for pets with pagination")
    @GetMapping("/search/paginated")
    ResponseEntity<PaginationResponse<PetFullDto>> searchWithPagination(
            @RequestParam String rsqlQuery,
            @PageableDefault(size = 20, sort = "id") Pageable pageable);
    
    @Operation(summary = "RSQL search for Preview pets")
    @GetMapping("/search/preview")
    ResponseEntity<AppResponse<List<PetPreviewDto>>> searchPreview(
            @RequestParam String rsqlQuery);
    
    @Operation(summary = "RSQL search for Preview pets with pagination")
    @GetMapping("/search/preview/paginated")
    ResponseEntity<PaginationResponse<PetPreviewDto>> searchPreviewWithPagination(
            @RequestParam String rsqlQuery,
            @PageableDefault(size = 20, sort = "id") Pageable pageable);

    // ========== Owner Methods ==========
    
    @Operation(summary = "Get all pets by owner")
    @GetMapping("/owner/{ownerId}")
    ResponseEntity<AppResponse<List<PetPreviewDto>>> getByOwnerId(@PathVariable Long ownerId);

    // ========== Microservice Integration ==========

    @Operation(summary = "Check pet existence (for microservices)")
    @PostMapping("/check-existence")
    ResponseEntity<AppResponse<Map<Long, Boolean>>> checkExistence(@RequestBody List<Long> ids);
    
    @Operation(summary = "Get Preview pets by list of IDs")
    @PostMapping("/previews")
    ResponseEntity<AppResponse<List<PetPreviewDto>>> getPreviewsByIds(@RequestBody List<Long> ids);

    // ========== Statistics ==========
    
    @Operation(summary = "Count active pets")
    @GetMapping("/count/active")
    ResponseEntity<AppResponse<Long>> countActive();
    
    @Operation(summary = "Count pets by type")
    @GetMapping("/count/by-type")
    ResponseEntity<AppResponse<Map<String, Long>>> countByType();
}


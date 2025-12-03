package com.micro.pet.web.controller;

import com.micro.pet.data.dto.petinfo.PetInfoDto;
import com.micro.pet.data.dto.petinfo.PetInfoUpdateDto;
import com.micro.pet.service.petinfo.PetInfoService;
import com.micro.pet.web.response.AppResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing detailed pet information (JSONB).
 */
@Slf4j
@RestController
@RequestMapping("${end.point.pets}/{petId}/info")
@RequiredArgsConstructor
@Tag(name = "Pet Info Management", description = "API for managing detailed pet information (JSONB)")
public class PetInfoController {

    private final PetInfoService petInfoService;

    @GetMapping
    public ResponseEntity<AppResponse<PetInfoDto>> getByPetId(@PathVariable Long petId) {
        log.debug("Getting pet info by petId: {}", petId);
        PetInfoDto dto = petInfoService.findByPetId(petId);
        return ResponseEntity.ok(AppResponse.successful(dto));
    }

    @PutMapping
    public ResponseEntity<AppResponse<PetInfoDto>> update(
            @PathVariable Long petId,
            @Valid @RequestBody PetInfoUpdateDto updateDto) {
        log.debug("Updating pet info for petId: {} with data: {}", petId, updateDto);
        PetInfoDto updated = petInfoService.update(petId, updateDto);
        return ResponseEntity.ok(AppResponse.successful(updated));
    }
}


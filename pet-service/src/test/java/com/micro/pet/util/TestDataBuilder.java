package com.micro.pet.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.pet.data.dto.pet.*;
import com.micro.pet.data.dto.pettype.*;
import com.micro.pet.data.entities.PetEntity;
import com.micro.pet.data.entities.PetInfoEntity;
import com.micro.pet.data.entities.PetTypeEntity;

import java.time.LocalDate;

/**
 * Builder для створення тестових Entity та DTO.
 */
public class TestDataBuilder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ========== PetTypeEntity Builders ==========

    public static PetTypeEntity.PetTypeEntityBuilder petTypeEntity() {
        return PetTypeEntity.builder()
                .typeName("DOG")
                .description("Собака")
                .exotic(false)
                .active(true);
    }

    public static PetTypeEntity.PetTypeEntityBuilder petTypeEntityCat() {
        return PetTypeEntity.builder()
                .typeName("CAT")
                .description("Кіт")
                .exotic(false)
                .active(true);
    }

    public static PetTypeEntity.PetTypeEntityBuilder petTypeEntityExotic() {
        return PetTypeEntity.builder()
                .typeName("IGUANA")
                .description("Ігуана")
                .exotic(true)
                .active(true);
    }

    // ========== PetEntity Builders ==========

    public static PetEntity.PetEntityBuilder petEntity() {
        return PetEntity.builder()
                .name("Барсик")
                .birthDate(LocalDate.of(2020, 3, 15))
                .ownerId(1L)
                .photoUrl("https://example.com/photo.jpg")
                .active(true);
    }

    public static PetEntity.PetEntityBuilder petEntityMinimal() {
        return PetEntity.builder()
                .name("Рекс")
                .ownerId(1L)
                .active(true);
    }

    public static PetEntity.PetEntityBuilder petEntityInactive() {
        return PetEntity.builder()
                .name("Мурка")
                .birthDate(LocalDate.of(2019, 5, 20))
                .ownerId(1L)
                .active(false);
    }

    // ========== PetInfoEntity Builders ==========

    public static PetInfoEntity.PetInfoEntityBuilder petInfoEntity() {
        try {
            JsonNode details = objectMapper.readTree("""
                {
                    "weight": 5.5,
                    "breed": "Перська",
                    "favoriteFood": "Корм для котів"
                }
                """);
            return PetInfoEntity.builder()
                    .details(details)
                    .active(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ========== PetTypeDto Builders ==========

    public static PetTypeDto.PetTypeDtoBuilder petTypeDto() {
        return PetTypeDto.builder()
                .typeName("DOG")
                .description("Собака")
                .exotic(false);
    }

    public static PetTypeCreateDto.PetTypeCreateDtoBuilder petTypeCreateDto() {
        return PetTypeCreateDto.builder()
                .typeName("DOG")
                .description("Собака")
                .exotic(false);
    }

    // ========== PetDto Builders ==========

    public static PetCreateDto.PetCreateDtoBuilder petCreateDto() {
        try {
            JsonNode details = objectMapper.readTree("""
                {
                    "weight": 5.5,
                    "breed": "Перська"
                }
                """);
            return PetCreateDto.builder()
                    .name("Барсик")
                    .birthDate(LocalDate.of(2020, 3, 15))
                    .ownerId(1L)
                    .typeId(1L)
                    .photoUrl("https://example.com/photo.jpg")
                    .details(details);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PetCreateDto.PetCreateDtoBuilder petCreateDtoMinimal() {
        return PetCreateDto.builder()
                .name("Рекс")
                .ownerId(1L)
                .typeId(1L);
    }
}


package com.micro.pet.service.pettype;

import com.micro.pet.data.dto.mapper.PetTypeMapper;
import com.micro.pet.data.dto.pettype.*;
import com.micro.pet.data.entities.PetTypeEntity;
import com.micro.pet.exception.ResourceNotFoundException;
import com.micro.pet.repository.PetTypeRepository;
import com.micro.pet.service.graph.GraphBuilderMappingService;
import com.micro.pet.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit тести для PetTypeServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PetTypeServiceImpl Unit Tests")
class PetTypeServiceImplTest {

    @Mock
    private PetTypeRepository petTypeRepository;

    @Mock
    private PetTypeMapper petTypeMapper;

    @Mock
    private GraphBuilderMappingService graphBuilderMappingService;

    @InjectMocks
    private PetTypeServiceImpl petTypeService;

    private PetTypeEntity testTypeEntity;
    private PetTypeDto testTypeDto;
    private PetTypeCreateDto testTypeCreateDto;
    private PetTypeUpdateDto testTypeUpdateDto;

    @BeforeEach
    void setUp() {
        testTypeEntity = TestDataBuilder.petTypeEntity().build();
        testTypeEntity.setId(1L);
        testTypeDto = TestDataBuilder.petTypeDto().build();
        testTypeDto.setId(1L);
        testTypeCreateDto = TestDataBuilder.petTypeCreateDto().build();
        testTypeUpdateDto = PetTypeUpdateDto.builder()
                .typeName("CAT")
                .description("Кіт")
                .build();
    }

    @Test
    @DisplayName("create - should create and return PetTypeDto")
    void create_shouldCreateAndReturnPetTypeDto() {
        // Given
        when(petTypeMapper.fromCreateDto(testTypeCreateDto)).thenReturn(testTypeEntity);
        when(petTypeRepository.save(any(PetTypeEntity.class))).thenReturn(testTypeEntity);
        when(petTypeMapper.toDto(testTypeEntity)).thenReturn(testTypeDto);

        // When
        PetTypeDto result = petTypeService.create(testTypeCreateDto);

        // Then
        assertThat(result).isNotNull();
        verify(petTypeRepository).save(any(PetTypeEntity.class));
    }

    @Test
    @DisplayName("update - should update and return PetTypeDto")
    void update_shouldUpdateAndReturnPetTypeDto() {
        // Given
        Long id = 1L;
        when(petTypeRepository.findById(id)).thenReturn(Optional.of(testTypeEntity));
        when(petTypeRepository.save(any(PetTypeEntity.class))).thenReturn(testTypeEntity);
        when(petTypeMapper.toDto(testTypeEntity)).thenReturn(testTypeDto);

        // When
        PetTypeDto result = petTypeService.update(id, testTypeUpdateDto);

        // Then
        assertThat(result).isNotNull();
        verify(petTypeMapper).updateFromUpdateDto(testTypeUpdateDto, testTypeEntity);
        verify(petTypeRepository).save(testTypeEntity);
    }

    @Test
    @DisplayName("update - should throw ResourceNotFoundException when type not exists")
    void update_shouldThrowException_whenTypeNotExists() {
        // Given
        Long id = 999L;
        when(petTypeRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petTypeService.update(id, testTypeUpdateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}


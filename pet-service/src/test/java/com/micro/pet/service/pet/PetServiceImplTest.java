package com.micro.pet.service.pet;

import com.micro.pet.data.constants.EntityConstants;

import com.micro.pet.data.dto.mapper.PetMapper;
import com.micro.pet.data.dto.pet.*;
import com.micro.pet.data.entities.PetEntity;
import com.micro.pet.data.entities.PetTypeEntity;
import com.micro.pet.exception.ResourceNotFoundException;
import com.micro.pet.repository.PetRepository;
import com.micro.pet.repository.PetTypeRepository;
import com.micro.pet.repository.rsql.PetSearchService;
import com.micro.pet.service.graph.GraphBuilderMappingService;
import com.micro.pet.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit тести для PetServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PetServiceImpl Unit Tests")
class PetServiceImplTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetMapper petMapper;

    @Mock
    private GraphBuilderMappingService graphBuilderMappingService;

    @Mock
    private PetTypeRepository petTypeRepository;

    @Mock
    private PetSearchService petSearchService;

    @InjectMocks
    private PetServiceImpl petService;

    private PetEntity testPetEntity;
    private PetTypeEntity testTypeEntity;
    private PetFullDto testPetFullDto;
    private PetPreviewDto testPetPreviewDto;
    private PetCreateDto testPetCreateDto;

    @BeforeEach
    void setUp() {
        testTypeEntity = TestDataBuilder.petTypeEntity().build();
        testTypeEntity.setId(1L);
        testPetEntity = TestDataBuilder.petEntity().build();
        testPetEntity.setId(1L);
        testPetEntity.setType(testTypeEntity);
        
        testPetFullDto = PetFullDto.builder()
                .id(1L)
                .name("Барсик")
                .ownerId(1L)
                .build();
        
        testPetPreviewDto = PetPreviewDto.builder()
                .id(1L)
                .name("Барсик")
                .typeName("DOG")
                .build();
        
        testPetCreateDto = TestDataBuilder.petCreateDto().build();
    }

    @Test
    @DisplayName("findFullById - should return PetFullDto when pet exists")
    void findFullById_shouldReturnPetFullDto_whenPetExists() {
        // Given
        Long id = 1L;
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPetEntity));
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.of(testPetEntity));
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.findFullById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(petMapper).toDto(testPetEntity);
    }

    @Test
    @DisplayName("findFullById - should throw ResourceNotFoundException when pet not exists")
    void findFullById_shouldThrowException_whenPetNotExists() {
        // Given
        Long id = 999L;
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.empty());
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.findFullById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("create - should create and return PetFullDto")
    void create_shouldCreateAndReturnPetFullDto() {
        // Given
        when(petTypeRepository.findById(1L)).thenReturn(Optional.of(testTypeEntity));
        when(petMapper.fromCreateDto(testPetCreateDto)).thenReturn(testPetEntity);
        when(petRepository.save(any(PetEntity.class))).thenReturn(testPetEntity);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.create(testPetCreateDto);

        // Then
        assertThat(result).isNotNull();
        verify(petRepository).save(any(PetEntity.class));
        // After optimization: entity is not reloaded after save
        verify(petRepository, never()).findById(anyLong());
        verify(petRepository, never()).findById(anyLong(), any());
    }

    @Test
    @DisplayName("create - should throw ResourceNotFoundException when type not exists")
    void create_shouldThrowException_whenTypeNotExists() {
        // Given
        when(petTypeRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.create(testPetCreateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findByOwnerId - should return list of PetPreviewDto")
    void findByOwnerId_shouldReturnListOfPetPreviewDto() {
        // Given
        Long ownerId = 1L;
        List<PetEntity> entities = Arrays.asList(testPetEntity);
        when(petRepository.findByOwnerIdAndActiveTrue(ownerId)).thenReturn(entities);
        when(petMapper.toPreviewDto(testPetEntity)).thenReturn(testPetPreviewDto);

        // When
        List<PetPreviewDto> result = petService.findByOwnerId(ownerId);

        // Then
        assertThat(result).hasSize(1);
        verify(petMapper).toPreviewDto(testPetEntity);
    }

    @Test
    @DisplayName("search - should return list of PetFullDto")
    void search_shouldReturnListOfPetFullDto() {
        // Given
        String rsqlQuery = EntityConstants.ACTIVE_RSQL_QUERY;
        List<PetEntity> entities = Arrays.asList(testPetEntity);
        when(petSearchService.search(eq(rsqlQuery), any(String[].class))).thenReturn(entities);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        List<PetFullDto> result = petService.search(rsqlQuery);

        // Then
        assertThat(result).hasSize(1);
        verify(petSearchService).search(eq(rsqlQuery), any(String[].class));
    }

    @Test
    @DisplayName("countActive - should return count of active pets")
    void countActive_shouldReturnCountOfActivePets() {
        // Given
        when(petRepository.countActive()).thenReturn(5L);

        // When
        long result = petService.countActive();

        // Then
        assertThat(result).isEqualTo(5L);
    }

    @Test
    @DisplayName("checkExistence - should return map with existence status using optimized query")
    void checkExistence_shouldReturnMapWithExistenceStatus() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(petRepository.existsAndActive(1L)).thenReturn(true);
        when(petRepository.existsAndActive(2L)).thenReturn(false);
        when(petRepository.existsAndActive(3L)).thenReturn(true);

        // When
        var result = petService.checkExistence(ids);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(1L)).isTrue();
        assertThat(result.get(2L)).isFalse();
        assertThat(result.get(3L)).isTrue();
        // Verify optimized single query is used instead of two separate queries
        verify(petRepository, times(3)).existsAndActive(anyLong());
        verify(petRepository, never()).existsById(anyLong());
        verify(petRepository, never()).isActive(anyLong());
    }

    @Test
    @DisplayName("findPreviewById - should return PetPreviewDto when pet exists")
    void findPreviewById_shouldReturnPetPreviewDto_whenPetExists() {
        // Given
        Long id = 1L;
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPetEntity));
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.of(testPetEntity));
        when(petMapper.toPreviewDto(testPetEntity)).thenReturn(testPetPreviewDto);

        // When
        PetPreviewDto result = petService.findPreviewById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(petMapper).toPreviewDto(testPetEntity);
    }

    @Test
    @DisplayName("findPreviewById - should throw ResourceNotFoundException when pet not exists")
    void findPreviewById_shouldThrowException_whenPetNotExists() {
        // Given
        Long id = 999L;
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.empty());
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.findPreviewById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("create - should create pet with PetInfo when details provided")
    void create_shouldCreatePetWithPetInfo_whenDetailsProvided() {
        // Given
        PetCreateDto createDtoWithDetails = TestDataBuilder.petCreateDto().build(); // has details
        PetEntity entityWithInfo = TestDataBuilder.petEntity().build();
        entityWithInfo.setId(1L);
        entityWithInfo.setType(testTypeEntity);
        
        when(petTypeRepository.findById(1L)).thenReturn(Optional.of(testTypeEntity));
        when(petMapper.fromCreateDto(createDtoWithDetails)).thenReturn(entityWithInfo);
        when(petRepository.save(any(PetEntity.class))).thenReturn(entityWithInfo);
        when(petMapper.toDto(entityWithInfo)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.create(createDtoWithDetails);

        // Then
        assertThat(result).isNotNull();
        verify(petRepository).save(any(PetEntity.class));
        // Verify that PetInfoEntity is created when details are provided
        verify(petRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("create - should create pet without PetInfo when details not provided")
    void create_shouldCreatePetWithoutPetInfo_whenDetailsNotProvided() {
        // Given
        PetCreateDto createDtoWithoutDetails = TestDataBuilder.petCreateDtoMinimal().build();
        createDtoWithoutDetails.setDetails(null);
        PetEntity entityWithoutInfo = TestDataBuilder.petEntity().build();
        entityWithoutInfo.setId(1L);
        entityWithoutInfo.setType(testTypeEntity);
        entityWithoutInfo.setInfo(null);
        
        when(petTypeRepository.findById(1L)).thenReturn(Optional.of(testTypeEntity));
        when(petMapper.fromCreateDto(createDtoWithoutDetails)).thenReturn(entityWithoutInfo);
        when(petRepository.save(any(PetEntity.class))).thenReturn(entityWithoutInfo);
        when(petMapper.toDto(entityWithoutInfo)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.create(createDtoWithoutDetails);

        // Then
        assertThat(result).isNotNull();
        verify(petRepository).save(any(PetEntity.class));
    }

    @Test
    @DisplayName("update - should update pet and return PetFullDto")
    void update_shouldUpdatePetAndReturnPetFullDto() {
        // Given
        Long id = 1L;
        PetUpdateDto updateDto = PetUpdateDto.builder()
                .name("Оновлений Барсик")
                .build();
        
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPetEntity));
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.of(testPetEntity));
        when(petRepository.save(any(PetEntity.class))).thenReturn(testPetEntity);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.update(id, updateDto);

        // Then
        assertThat(result).isNotNull();
        verify(petMapper).updateFromUpdateDto(updateDto, testPetEntity);
        verify(petRepository).save(testPetEntity);
        // Verify typeId is null, so type should not be updated
        verify(petTypeRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("update - should update pet without typeId when typeId is null")
    void update_shouldUpdatePetWithoutTypeId_whenTypeIdIsNull() {
        // Given
        Long id = 1L;
        PetUpdateDto updateDto = PetUpdateDto.builder()
                .name("Оновлений Барсик")
                .typeId(null) // Explicitly null
                .build();
        
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPetEntity));
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.of(testPetEntity));
        when(petRepository.save(any(PetEntity.class))).thenReturn(testPetEntity);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.update(id, updateDto);

        // Then
        assertThat(result).isNotNull();
        // Verify typeId is null, so type should not be updated
        verify(petTypeRepository, never()).findById(anyLong());
        verify(petRepository).save(testPetEntity);
    }

    @Test
    @DisplayName("update - should update pet without details when details is null")
    void update_shouldUpdatePetWithoutDetails_whenDetailsIsNull() {
        // Given
        Long id = 1L;
        PetUpdateDto updateDto = PetUpdateDto.builder()
                .name("Оновлений Барсик")
                .details(null) // Explicitly null
                .build();
        
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPetEntity));
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.of(testPetEntity));
        when(petRepository.save(any(PetEntity.class))).thenReturn(testPetEntity);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.update(id, updateDto);

        // Then
        assertThat(result).isNotNull();
        // Verify details is null, so PetInfo should not be updated
        verify(petRepository).save(testPetEntity);
        // Verify that existing info is not modified
        assertThat(testPetEntity.getInfo()).isEqualTo(testPetEntity.getInfo());
    }

    @Test
    @DisplayName("update - should update pet with typeId and without details")
    void update_shouldUpdatePetWithTypeIdAndWithoutDetails() {
        // Given
        Long id = 1L;
        Long newTypeId = 2L;
        PetTypeEntity newType = TestDataBuilder.petTypeEntityCat().build();
        newType.setId(newTypeId);
        
        PetUpdateDto updateDto = PetUpdateDto.builder()
                .name("Оновлений Барсик")
                .typeId(newTypeId)
                .details(null) // No details update
                .build();
        
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPetEntity));
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.of(testPetEntity));
        when(petTypeRepository.findById(newTypeId)).thenReturn(Optional.of(newType));
        when(petRepository.save(any(PetEntity.class))).thenReturn(testPetEntity);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.update(id, updateDto);

        // Then
        assertThat(result).isNotNull();
        verify(petTypeRepository).findById(newTypeId);
        verify(petRepository).save(testPetEntity);
    }

    @Test
    @DisplayName("update - should update pet type when typeId provided")
    void update_shouldUpdatePetType_whenTypeIdProvided() {
        // Given
        Long id = 1L;
        Long newTypeId = 2L;
        PetTypeEntity newType = TestDataBuilder.petTypeEntityCat().build();
        newType.setId(newTypeId);
        
        PetUpdateDto updateDto = PetUpdateDto.builder()
                .typeId(newTypeId)
                .build();
        
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPetEntity));
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.of(testPetEntity));
        when(petTypeRepository.findById(newTypeId)).thenReturn(Optional.of(newType));
        when(petRepository.save(any(PetEntity.class))).thenReturn(testPetEntity);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.update(id, updateDto);

        // Then
        assertThat(result).isNotNull();
        verify(petTypeRepository).findById(newTypeId);
        verify(petRepository).save(testPetEntity);
    }

    @Test
    @DisplayName("update - should throw ResourceNotFoundException when pet not exists")
    void update_shouldThrowException_whenPetNotExists() {
        // Given
        Long id = 999L;
        PetUpdateDto updateDto = PetUpdateDto.builder().name("Test").build();
        
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.empty());
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.update(id, updateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update - should throw ResourceNotFoundException when type not exists")
    void update_shouldThrowException_whenTypeNotExists() {
        // Given
        Long id = 1L;
        Long invalidTypeId = 999L;
        PetUpdateDto updateDto = PetUpdateDto.builder()
                .typeId(invalidTypeId)
                .build();
        
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPetEntity));
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.of(testPetEntity));
        when(petTypeRepository.findById(invalidTypeId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.update(id, updateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update - should create PetInfo when details provided and info is null")
    void update_shouldCreatePetInfo_whenDetailsProvidedAndInfoIsNull() {
        // Given
        Long id = 1L;
        testPetEntity.setInfo(null); // No existing info
        
        com.fasterxml.jackson.databind.JsonNode details = null;
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            details = mapper.readTree("{\"weight\": 6.5}");
        } catch (Exception e) {
            // ignore
        }
        
        PetUpdateDto updateDto = PetUpdateDto.builder()
                .details(details)
                .build();
        
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPetEntity));
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.of(testPetEntity));
        when(petRepository.save(any(PetEntity.class))).thenReturn(testPetEntity);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.update(id, updateDto);

        // Then
        assertThat(result).isNotNull();
        verify(petRepository).save(any(PetEntity.class));
    }

    @Test
    @DisplayName("update - should update existing PetInfo when details provided")
    void update_shouldUpdateExistingPetInfo_whenDetailsProvided() {
        // Given
        Long id = 1L;
        com.micro.pet.data.entities.PetInfoEntity existingInfo = TestDataBuilder.petInfoEntity().build();
        testPetEntity.setInfo(existingInfo);
        
        com.fasterxml.jackson.databind.JsonNode details = null;
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            details = mapper.readTree("{\"weight\": 7.0}");
        } catch (Exception e) {
            // ignore
        }
        
        PetUpdateDto updateDto = PetUpdateDto.builder()
                .details(details)
                .build();
        
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPetEntity));
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.of(testPetEntity));
        when(petRepository.save(any(PetEntity.class))).thenReturn(testPetEntity);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.update(id, updateDto);

        // Then
        assertThat(result).isNotNull();
        verify(petRepository).save(any(PetEntity.class));
    }

    @Test
    @DisplayName("findByOwnerId - should return empty list when no pets found")
    void findByOwnerId_shouldReturnEmptyList_whenNoPetsFound() {
        // Given
        Long ownerId = 999L;
        when(petRepository.findByOwnerIdAndActiveTrue(ownerId)).thenReturn(Arrays.asList());

        // When
        List<PetPreviewDto> result = petService.findByOwnerId(ownerId);

        // Then
        assertThat(result).isEmpty();
        verify(petRepository).findByOwnerIdAndActiveTrue(ownerId);
    }

    @Test
    @DisplayName("search - should return empty list when no pets found")
    void search_shouldReturnEmptyList_whenNoPetsFound() {
        // Given
        String rsqlQuery = "name==NonExistent";
        when(petSearchService.search(eq(rsqlQuery), any(String[].class))).thenReturn(Arrays.asList());

        // When
        List<PetFullDto> result = petService.search(rsqlQuery);

        // Then
        assertThat(result).isEmpty();
        verify(petSearchService).search(eq(rsqlQuery), any(String[].class));
    }

    @Test
    @DisplayName("searchWithPagination - should return Page of PetFullDto")
    void searchWithPagination_shouldReturnPageOfPetFullDto() {
        // Given
        String rsqlQuery = EntityConstants.ACTIVE_RSQL_QUERY;
        Pageable pageable = PageRequest.of(0, 10);
        Page<PetEntity> page = new PageImpl<>(Arrays.asList(testPetEntity), pageable, 1);
        
        when(petSearchService.searchWithPagination(eq(rsqlQuery), eq(pageable), any(String[].class)))
                .thenReturn(page);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        Page<PetFullDto> result = petService.searchWithPagination(rsqlQuery, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(petSearchService).searchWithPagination(eq(rsqlQuery), eq(pageable), any(String[].class));
    }

    @Test
    @DisplayName("searchPreview - should return list of PetPreviewDto")
    void searchPreview_shouldReturnListOfPetPreviewDto() {
        // Given
        String rsqlQuery = EntityConstants.ACTIVE_RSQL_QUERY;
        List<PetEntity> entities = Arrays.asList(testPetEntity);
        when(petSearchService.search(eq(rsqlQuery), any(String[].class))).thenReturn(entities);
        when(petMapper.toPreviewDto(testPetEntity)).thenReturn(testPetPreviewDto);

        // When
        List<PetPreviewDto> result = petService.searchPreview(rsqlQuery);

        // Then
        assertThat(result).hasSize(1);
        verify(petSearchService).search(eq(rsqlQuery), any(String[].class));
    }

    @Test
    @DisplayName("searchPreviewWithPagination - should return Page of PetPreviewDto")
    void searchPreviewWithPagination_shouldReturnPageOfPetPreviewDto() {
        // Given
        String rsqlQuery = EntityConstants.ACTIVE_RSQL_QUERY;
        Pageable pageable = PageRequest.of(0, 10);
        Page<PetEntity> page = new PageImpl<>(Arrays.asList(testPetEntity), pageable, 1);
        
        when(petSearchService.searchWithPagination(eq(rsqlQuery), eq(pageable), any(String[].class)))
                .thenReturn(page);
        when(petMapper.toPreviewDto(testPetEntity)).thenReturn(testPetPreviewDto);

        // When
        Page<PetPreviewDto> result = petService.searchPreviewWithPagination(rsqlQuery, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(petSearchService).searchWithPagination(eq(rsqlQuery), eq(pageable), any(String[].class));
    }

    @Test
    @DisplayName("findPreviewsByIds - should return list of PetPreviewDto")
    void findPreviewsByIds_shouldReturnListOfPetPreviewDto() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L);
        List<PetEntity> entities = Arrays.asList(testPetEntity, testPetEntity);
        lenient().when(petRepository.findAllById(eq(ids), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(entities);
        lenient().when(petRepository.findAllById(eq(ids))).thenReturn(entities);
        when(petMapper.toPreviewDto(any(PetEntity.class))).thenReturn(testPetPreviewDto);

        // When
        List<PetPreviewDto> result = petService.findPreviewsByIds(ids);

        // Then
        assertThat(result).isNotNull();
        verify(petMapper, atLeastOnce()).toPreviewDto(any(PetEntity.class));
    }

    @Test
    @DisplayName("findPreviewsByIds - should return empty list when no pets found")
    void findPreviewsByIds_shouldReturnEmptyList_whenNoPetsFound() {
        // Given
        List<Long> ids = Arrays.asList(999L);
        lenient().when(petRepository.findAllById(eq(ids), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Arrays.asList());
        lenient().when(petRepository.findAllById(eq(ids))).thenReturn(Arrays.asList());

        // When
        List<PetPreviewDto> result = petService.findPreviewsByIds(ids);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("countByType - should return map with type counts")
    void countByType_shouldReturnMapWithTypeCounts() {
        // Given
        List<Object[]> results = Arrays.asList(
                new Object[]{"DOG", 5L},
                new Object[]{"CAT", 3L}
        );
        when(petRepository.countByTypeGrouped()).thenReturn(results);

        // When
        Map<String, Long> result = petService.countByType();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get("DOG")).isEqualTo(5L);
        assertThat(result.get("CAT")).isEqualTo(3L);
        verify(petRepository).countByTypeGrouped();
    }

    @Test
    @DisplayName("countByType - should return empty map when no pets")
    void countByType_shouldReturnEmptyMap_whenNoPets() {
        // Given
        when(petRepository.countByTypeGrouped()).thenReturn(Arrays.asList());

        // When
        Map<String, Long> result = petService.countByType();

        // Then
        assertThat(result).isEmpty();
        verify(petRepository).countByTypeGrouped();
    }

    @Test
    @DisplayName("checkExistence - should return empty map when empty list provided")
    void checkExistence_shouldReturnEmptyMap_whenEmptyListProvided() {
        // Given
        List<Long> ids = Arrays.asList();

        // When
        Map<Long, Boolean> result = petService.checkExistence(ids);

        // Then
        assertThat(result).isEmpty();
        verify(petRepository, never()).existsAndActive(anyLong());
    }

    @Test
    @DisplayName("softDelete - should throw ResourceNotFoundException when pet not exists")
    void softDelete_shouldThrowException_whenPetNotExists() {
        // Given
        Long id = 999L;
        when(petRepository.softDelete(id, null)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.softDelete(id))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(petRepository).softDelete(id, null);
    }

    @Test
    @DisplayName("softDelete - should return PetFullDto when pet exists")
    void softDelete_shouldReturnPetFullDto_whenPetExists() {
        // Given
        Long id = 1L;
        when(petRepository.softDelete(id, null)).thenReturn(Optional.of(testPetEntity));
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.softDelete(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(petRepository).softDelete(id, null);
        verify(petMapper).toDto(testPetEntity);
    }

    @Test
    @DisplayName("delete - should throw ResourceNotFoundException when pet not exists")
    void delete_shouldThrowException_whenPetNotExists() {
        // Given
        Long id = 999L;
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.empty());
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete - should delete pet when exists")
    void delete_shouldDeletePet_whenExists() {
        // Given
        Long id = 1L;
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPetEntity));
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.of(testPetEntity));
        doNothing().when(petRepository).deleteById(id);

        // When
        petService.delete(id);

        // Then
        verify(petRepository).deleteById(id);
    }

    @Test
    @DisplayName("existsById - should return true when pet exists")
    void existsById_shouldReturnTrue_whenPetExists() {
        // Given
        Long id = 1L;
        when(petRepository.existsById(id)).thenReturn(true);

        // When
        boolean result = petService.existsById(id);

        // Then
        assertThat(result).isTrue();
        verify(petRepository).existsById(id);
    }

    @Test
    @DisplayName("existsById - should return false when pet not exists")
    void existsById_shouldReturnFalse_whenPetNotExists() {
        // Given
        Long id = 999L;
        when(petRepository.existsById(id)).thenReturn(false);

        // When
        boolean result = petService.existsById(id);

        // Then
        assertThat(result).isFalse();
        verify(petRepository).existsById(id);
    }

    @Test
    @DisplayName("isActive - should return true when pet is active")
    void isActive_shouldReturnTrue_whenPetIsActive() {
        // Given
        Long id = 1L;
        when(petRepository.isActive(id)).thenReturn(true);

        // When
        boolean result = petService.isActive(id);

        // Then
        assertThat(result).isTrue();
        verify(petRepository).isActive(id);
    }

    @Test
    @DisplayName("isActive - should return false when pet is not active")
    void isActive_shouldReturnFalse_whenPetIsNotActive() {
        // Given
        Long id = 1L;
        when(petRepository.isActive(id)).thenReturn(false);

        // When
        boolean result = petService.isActive(id);

        // Then
        assertThat(result).isFalse();
        verify(petRepository).isActive(id);
    }

    @Test
    @DisplayName("existsByIds - should return map with existence status")
    void existsByIds_shouldReturnMapWithExistenceStatus() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(petRepository.existsById(1L)).thenReturn(true);
        when(petRepository.existsById(2L)).thenReturn(false);
        when(petRepository.existsById(3L)).thenReturn(true);

        // When
        Map<Long, Boolean> result = petService.existsByIds(ids);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(1L)).isTrue();
        assertThat(result.get(2L)).isFalse();
        assertThat(result.get(3L)).isTrue();
        verify(petRepository, times(3)).existsById(anyLong());
    }

    @Test
    @DisplayName("findAll - should return list of PetFullDto")
    void findAll_shouldReturnListOfPetFullDto() {
        // Given
        List<PetEntity> entities = Arrays.asList(testPetEntity);
        lenient().when(petRepository.findAll(any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(entities);
        lenient().when(petRepository.findAll()).thenReturn(entities);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        List<PetFullDto> result = petService.findAll();

        // Then
        assertThat(result).hasSize(1);
        verify(petMapper).toDto(testPetEntity);
    }

    @Test
    @DisplayName("findAllActive - should return list of active PetFullDto")
    void findAllActive_shouldReturnListOfActivePetFullDto() {
        // Given
        List<PetEntity> entities = Arrays.asList(testPetEntity);
        when(petRepository.findAllActive()).thenReturn(entities);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        List<PetFullDto> result = petService.findAllActive();

        // Then
        assertThat(result).hasSize(1);
        verify(petMapper).toDto(testPetEntity);
        verify(petRepository).findAllActive();
    }

    @Test
    @DisplayName("findAllActive with graphAttributes - should return list using graph")
    void findAllActive_withGraphAttributes_shouldReturnListUsingGraph() {
        // Given
        List<PetEntity> entities = Arrays.asList(testPetEntity);
        String[] graphAttributes = {"type"};
        lenient().when(petRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), 
                any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(entities);
        lenient().when(petRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(entities);
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        List<PetFullDto> result = petService.findAllActive(graphAttributes);

        // Then
        assertThat(result).hasSize(1);
        verify(petMapper).toDto(testPetEntity);
    }

    @Test
    @DisplayName("findAllActivePreview - should return list of active PetPreviewDto")
    void findAllActivePreview_shouldReturnListOfActivePetPreviewDto() {
        // Given
        List<PetEntity> entities = Arrays.asList(testPetEntity);
        lenient().when(petRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), 
                any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(entities);
        lenient().when(petRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(entities);
        when(petMapper.toPreviewDto(testPetEntity)).thenReturn(testPetPreviewDto);

        // When
        List<PetPreviewDto> result = petService.findAllActivePreview();

        // Then
        assertThat(result).hasSize(1);
        verify(petMapper).toPreviewDto(testPetEntity);
    }

    @Test
    @DisplayName("findAllPreview - should return list of PetPreviewDto")
    void findAllPreview_shouldReturnListOfPetPreviewDto() {
        // Given
        List<PetEntity> entities = Arrays.asList(testPetEntity);
        lenient().when(petRepository.findAll(any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(entities);
        lenient().when(petRepository.findAll()).thenReturn(entities);
        when(petMapper.toPreviewDto(testPetEntity)).thenReturn(testPetPreviewDto);

        // When
        List<PetPreviewDto> result = petService.findAllPreview();

        // Then
        assertThat(result).hasSize(1);
        verify(petMapper).toPreviewDto(testPetEntity);
    }

    @Test
    @DisplayName("findAllPreview with pagination - should return Page of PetPreviewDto")
    void findAllPreviewWithPagination_shouldReturnPageOfPetPreviewDto() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<PetEntity> page = new PageImpl<>(Arrays.asList(testPetEntity), pageable, 1);
        lenient().when(petRepository.findAll(eq(pageable), 
                any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(page);
        lenient().when(petRepository.findAll(eq(pageable))).thenReturn(page);
        when(petMapper.toPreviewDto(testPetEntity)).thenReturn(testPetPreviewDto);

        // When
        Page<PetPreviewDto> result = petService.findAllPreview(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("findAllActivePreview with pagination - should return Page of PetPreviewDto")
    void findAllActivePreviewWithPagination_shouldReturnPageOfPetPreviewDto() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<PetEntity> page = new PageImpl<>(Arrays.asList(testPetEntity), pageable, 1);
        lenient().when(petRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), 
                eq(pageable), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(page);
        lenient().when(petRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable)))
                .thenReturn(page);
        when(petMapper.toPreviewDto(testPetEntity)).thenReturn(testPetPreviewDto);

        // When
        Page<PetPreviewDto> result = petService.findAllActivePreview(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("findById - should return PetFullDto when pet exists")
    void findById_shouldReturnPetFullDto_whenPetExists() {
        // Given
        Long id = 1L;
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPetEntity));
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.of(testPetEntity));
        when(petMapper.toDto(testPetEntity)).thenReturn(testPetFullDto);

        // When
        PetFullDto result = petService.findById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(petMapper).toDto(testPetEntity);
    }

    @Test
    @DisplayName("findById - should throw ResourceNotFoundException when pet not exists")
    void findById_shouldThrowException_whenPetNotExists() {
        // Given
        Long id = 999L;
        lenient().when(petRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.empty());
        lenient().when(petRepository.findById(eq(id))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("softDeleteAll - should return count of deleted pets")
    void softDeleteAll_shouldReturnCountOfDeletedPets() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(petRepository.softDeleteAll(ids, null)).thenReturn(3);

        // When
        int result = petService.softDeleteAll(ids);

        // Then
        assertThat(result).isEqualTo(3);
        verify(petRepository).softDeleteAll(ids, null);
    }

    @Test
    @DisplayName("softDeleteAll - should return 0 when no pets deleted")
    void softDeleteAll_shouldReturn0_whenNoPetsDeleted() {
        // Given
        List<Long> ids = Arrays.asList(999L);
        when(petRepository.softDeleteAll(ids, null)).thenReturn(0);

        // When
        int result = petService.softDeleteAll(ids);

        // Then
        assertThat(result).isEqualTo(0);
        verify(petRepository).softDeleteAll(ids, null);
    }

    @Test
    @DisplayName("createAll - should return list of created PetFullDto")
    void createAll_shouldReturnListOfCreatedPetFullDto() {
        // Given
        PetEntity entity1 = TestDataBuilder.petEntity().build();
        entity1.setId(1L);
        PetEntity entity2 = TestDataBuilder.petEntity().build();
        entity2.setId(2L);
        
        List<PetCreateDto> createDtos = Arrays.asList(testPetCreateDto, testPetCreateDto);
        List<PetEntity> entities = Arrays.asList(entity1, entity2);
        
        // createAll uses mapper.toEntity() from BaseService (BaseMapper interface)
        when(petMapper.toEntity(any(PetCreateDto.class))).thenReturn(testPetEntity);
        when(petRepository.saveAll(anyList())).thenReturn(entities);
        when(petMapper.toDto(any(PetEntity.class))).thenReturn(testPetFullDto);

        // When
        List<PetFullDto> result = petService.createAll(createDtos);

        // Then
        assertThat(result).hasSize(2);
        verify(petRepository).saveAll(anyList());
        verify(petMapper, times(2)).toDto(any(PetEntity.class));
    }
}


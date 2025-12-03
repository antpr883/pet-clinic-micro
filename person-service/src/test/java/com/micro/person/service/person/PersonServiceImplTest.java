package com.micro.person.service.person;

import com.micro.person.data.dto.mapper.PersonMapper;
import com.micro.person.data.dto.person.PersonDto;
import com.micro.person.data.dto.person.PersonCreateDto;
import com.micro.person.data.dto.person.PersonFullDto;
import com.micro.person.data.dto.person.PersonUpdateDto;
import com.micro.person.data.dto.person.PersonPreviewDto;
import com.micro.person.data.entities.PersonEntity;
import com.micro.person.data.entities.enums.ContactType;
import com.micro.person.data.entities.enums.PersonRole;
import com.micro.person.exception.ResourceNotFoundException;
import com.micro.person.repository.PersonRepository;
import com.micro.person.repository.rsql.PersonSearchService;
import com.micro.person.service.graph.GraphBuilderMappingService;
import com.micro.person.util.TestDataBuilder;
import com.micro.person.util.TestFixtures;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Unit тести для PersonServiceImpl.
 * 
 * Тестує бізнес-логіку PersonService без залежності від бази даних.
 * Використовує Mockito для мокування залежностей.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PersonServiceImpl Unit Tests")
class PersonServiceImplTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper personMapper;

    @Mock
    private GraphBuilderMappingService graphBuilderMappingService;

    @Mock
    private PersonSearchService personSearchService;

    @InjectMocks
    private PersonServiceImpl personService;

    private PersonEntity testPersonEntity;
    private PersonDto testPersonDto;
    private PersonFullDto testPersonFullDto;
    private PersonPreviewDto testPersonPreviewDto;

    @BeforeEach
    void setUp() {
        testPersonEntity = TestFixtures.IVAN_PETRENKO_ENTITY;
        testPersonDto = TestFixtures.IVAN_PETRENKO_DTO;
        testPersonFullDto = PersonFullDto.builder()
                .id(1L)
                .firstName("Іван")
                .lastName("Петренко")
                .personRole(PersonRole.OWNER)
                .build();
        testPersonPreviewDto = PersonPreviewDto.builder()
                .id(1L)
                .firstName("Іван")
                .lastName("Петренко")
                .build();
    }

    // ========== CRUD Tests (from AbstractBaseService) ==========

    @Test
    @DisplayName("findById - should return PersonDto when person exists")
    void findById_shouldReturnPersonDto_whenPersonExists() {
        // Given
        Long id = 1L;
        lenient().when(personRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPersonEntity));
        lenient().when(personRepository.findById(eq(id))).thenReturn(Optional.of(testPersonEntity));
        when(personMapper.toDto(testPersonEntity)).thenReturn(testPersonDto);

        // When
        PersonDto result = personService.findById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFirstName()).isEqualTo("Іван");
        assertThat(result.getLastName()).isEqualTo("Петренко");
        verify(personMapper).toDto(testPersonEntity);
    }

    @Test
    @DisplayName("findById - should throw ResourceNotFoundException when person not exists")
    void findById_shouldThrowException_whenPersonNotExists() {
        // Given
        Long id = 999L;
        lenient().when(personRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.empty());
        lenient().when(personRepository.findById(eq(id))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> personService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("PersonEntity")
                .hasMessageContaining("999");
        verify(personMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("findAll - should return list of PersonDto")
    void findAll_shouldReturnListOfPersonDto() {
        // Given
        List<PersonEntity> entities = Arrays.asList(
                TestFixtures.IVAN_PETRENKO_ENTITY,
                TestFixtures.MARIA_KOVALENKO_ENTITY
        );

        lenient().when(personRepository.findAll(any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(entities);
        lenient().when(personRepository.findAll()).thenReturn(entities);
        when(personMapper.toDto(any(PersonEntity.class))).thenAnswer(invocation -> {
            PersonEntity entity = invocation.getArgument(0);
            return entity.getId().equals(1L) ? TestFixtures.IVAN_PETRENKO_DTO : TestFixtures.MARIA_KOVALENKO_DTO;
        });

        // When
        List<PersonDto> result = personService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(personMapper, times(2)).toDto(any(PersonEntity.class));
    }

    @Test
    @DisplayName("create - should create and return PersonFullDto")
    void create_shouldCreateAndReturnPersonFullDto() {
        // Given
        PersonCreateDto inputDto = PersonCreateDto.builder()
                .firstName("Тест")
                .lastName("Персона")
                .personRole(PersonRole.OWNER)
                .build();
        PersonEntity savedEntity = PersonEntity.builder()
                .id(1L)
                .firstName(inputDto.getFirstName())
                .lastName(inputDto.getLastName())
                .personRole(inputDto.getPersonRole())
                .active(true)
                .build();
        PersonEntity reloadedEntity = PersonEntity.builder()
                .id(1L)
                .firstName(inputDto.getFirstName())
                .lastName(inputDto.getLastName())
                .personRole(inputDto.getPersonRole())
                .active(true)
                .build();
        PersonFullDto outputDto = PersonFullDto.builder()
                .id(1L)
                .firstName(inputDto.getFirstName())
                .lastName(inputDto.getLastName())
                .personRole(inputDto.getPersonRole())
                .build();

        when(personMapper.fromCreateDto(inputDto)).thenReturn(savedEntity);
        when(personRepository.save(savedEntity)).thenReturn(savedEntity);
        // After optimization: no reload needed, entity is used directly after save
        when(personMapper.toFullDto(savedEntity)).thenReturn(outputDto);

        // When
        PersonFullDto result = personService.create(inputDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo(inputDto.getFirstName());
        verify(personMapper).fromCreateDto(inputDto);
        verify(personRepository).save(savedEntity);
        verify(personMapper).toFullDto(savedEntity);
        // Verify no reload after save
        verify(personRepository, never()).findById(anyLong());
        verify(personRepository, never()).findById(anyLong(), any());
    }

    @Test
    @DisplayName("update - should update and return PersonFullDto")
    void update_shouldUpdateAndReturnPersonFullDto() {
        // Given
        Long id = 1L;
        PersonUpdateDto inputDto = PersonUpdateDto.builder()
                .firstName("Оновлене Ім'я")
                .lastName("Оновлене Прізвище")
                .personRole(PersonRole.OWNER)
                .build();
        PersonEntity existingEntity = TestFixtures.IVAN_PETRENKO_ENTITY;
        PersonEntity updatedEntity = PersonEntity.builder()
                .id(id)
                .firstName(inputDto.getFirstName())
                .lastName(inputDto.getLastName())
                .personRole(inputDto.getPersonRole())
                .active(true)
                .build();
        PersonEntity reloadedEntity = PersonEntity.builder()
                .id(id)
                .firstName(inputDto.getFirstName())
                .lastName(inputDto.getLastName())
                .personRole(inputDto.getPersonRole())
                .active(true)
                .build();
        PersonFullDto outputDto = PersonFullDto.builder()
                .id(id)
                .firstName(inputDto.getFirstName())
                .lastName(inputDto.getLastName())
                .personRole(inputDto.getPersonRole())
                .build();

        com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph mockGraph = 
                com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph.fetching().build();
        
        // After optimization: load with Entity Graph from start
        when(graphBuilderMappingService.getGraphWithAttributes(eq(PersonEntity.class), any(String[].class)))
                .thenReturn(mockGraph);
        when(personRepository.findById(eq(id), eq(mockGraph)))
                .thenReturn(Optional.of(existingEntity));
        when(personRepository.save(any(PersonEntity.class))).thenReturn(updatedEntity);
        // After optimization: no reload needed, entity already has relationships
        when(personMapper.toFullDto(updatedEntity)).thenReturn(outputDto);

        // When
        PersonFullDto result = personService.update(id, inputDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(inputDto.getFirstName());
        assertThat(result.getLastName()).isEqualTo(inputDto.getLastName());
        // Verify single query with Entity Graph (no reload after save)
        verify(personRepository, never()).findById(id); // No call without graph
        verify(personRepository, times(1)).findById(eq(id), eq(mockGraph)); // Single call with graph
        verify(personMapper).updateFromUpdateDto(inputDto, existingEntity);
        verify(personRepository).save(existingEntity);
        verify(personMapper).toFullDto(updatedEntity);
    }

    @Test
    @DisplayName("delete - should delete person")
    void delete_shouldDeletePerson() {
        // Given
        Long id = 1L;
        lenient().when(personRepository.findById(id)).thenReturn(Optional.of(testPersonEntity));

        // When
        personService.delete(id);

        // Then
        verify(personRepository).findById(id);
        verify(personRepository).deleteById(id);
    }

    @Test
    @DisplayName("softDelete - should set active=false")
    void softDelete_shouldSetActiveFalse() {
        // Given
        Long id = 1L;
        PersonEntity inactiveEntity = PersonEntity.builder()
                .id(id)
                .firstName("Іван")
                .lastName("Петренко")
                .personRole(PersonRole.OWNER)
                .active(false)
                .build();
        PersonDto inactiveDto = PersonDto.builder()
                .id(id)
                .firstName("Іван")
                .lastName("Петренко")
                .personRole(PersonRole.OWNER)
                .build();

        when(personRepository.softDelete(id, null)).thenReturn(Optional.of(inactiveEntity));
        when(personMapper.toDto(inactiveEntity)).thenReturn(inactiveDto);

        // When
        PersonDto result = personService.softDelete(id);

        // Then
        assertThat(result).isNotNull();
        verify(personRepository).softDelete(id, null);
        verify(personMapper).toDto(inactiveEntity);
    }

    // ========== Optimized Methods Tests ==========

    @Test
    @DisplayName("findFullById - should return PersonFullDto when person exists")
    void findFullById_shouldReturnPersonFullDto_whenPersonExists() {
        // Given
        Long id = 1L;
        com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph mockGraph = 
                com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph.fetching().build();
        
        when(graphBuilderMappingService.getGraphWithAttributes(eq(PersonEntity.class), any(String[].class)))
                .thenReturn(mockGraph);
        when(personRepository.findById(eq(id), eq(mockGraph)))
                .thenReturn(Optional.of(testPersonEntity));
        when(personMapper.toFullDto(testPersonEntity)).thenReturn(testPersonFullDto);

        // When
        PersonFullDto result = personService.findFullById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFirstName()).isEqualTo("Іван");
        verify(graphBuilderMappingService).getGraphWithAttributes(eq(PersonEntity.class), any(String[].class));
        verify(personRepository).findById(eq(id), eq(mockGraph));
        verify(personMapper).toFullDto(testPersonEntity);
    }

    @Test
    @DisplayName("findPreviewById - should return PersonPreviewDto when person exists")
    void findPreviewById_shouldReturnPersonPreviewDto_whenPersonExists() {
        // Given
        Long id = 1L;
        lenient().when(personRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testPersonEntity));
        lenient().when(personRepository.findById(eq(id))).thenReturn(Optional.of(testPersonEntity));
        when(personMapper.toPreviewDto(testPersonEntity)).thenReturn(testPersonPreviewDto);

        // When
        PersonPreviewDto result = personService.findPreviewById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(personMapper).toPreviewDto(testPersonEntity);
    }

    @Test
    @DisplayName("countActive - should return count of active persons")
    void countActive_shouldReturnCountOfActivePersons() {
        // Given
        long expectedCount = 5L;
        when(personRepository.countActive()).thenReturn(expectedCount);

        // When
        long result = personService.countActive();

        // Then
        assertThat(result).isEqualTo(expectedCount);
        verify(personRepository).countActive();
    }

    // ========== RSQL Search Tests ==========

    @Test
    @DisplayName("search - should return list of PersonFullDto from RSQL query")
    void search_shouldReturnListOfPersonFullDto_fromRsqlQuery() {
        // Given
        String rsqlQuery = "firstName==Іван";
        List<PersonEntity> entities = Collections.singletonList(testPersonEntity);

        when(personSearchService.search(eq(rsqlQuery), any(String[].class))).thenReturn(entities);
        when(personMapper.toFullDto(testPersonEntity)).thenReturn(testPersonFullDto);

        // When
        List<PersonFullDto> result = personService.search(rsqlQuery);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(personSearchService).search(eq(rsqlQuery), any(String[].class));
    }

    @Test
    @DisplayName("searchWithPagination - should return Page of PersonFullDto")
    void searchWithPagination_shouldReturnPageOfPersonFullDto() {
        // Given
        String rsqlQuery = "firstName==Іван";
        Pageable pageable = PageRequest.of(0, 10);
        Page<PersonEntity> entityPage = new PageImpl<>(Collections.singletonList(testPersonEntity));

        when(personSearchService.searchWithPagination(eq(rsqlQuery), eq(pageable), any(String[].class))).thenReturn(entityPage);
        when(personMapper.toFullDto(testPersonEntity)).thenReturn(testPersonFullDto);

        // When
        Page<PersonFullDto> result = personService.searchWithPagination(rsqlQuery, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(personSearchService).searchWithPagination(eq(rsqlQuery), eq(pageable), any(String[].class));
    }

    // ========== Microservice Integration Tests ==========

    @Test
    @DisplayName("checkPersonsExist - should return Map with existence status")
    void checkPersonsExist_shouldReturnMapWithExistenceStatus() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L, 999L);
        // After optimization: use single existsAndActive query instead of two separate queries
        when(personRepository.existsAndActive(1L)).thenReturn(true);
        when(personRepository.existsAndActive(2L)).thenReturn(true);
        when(personRepository.existsAndActive(999L)).thenReturn(false);

        // When
        Map<Long, Boolean> result = personService.checkPersonsExist(ids);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(1L)).isTrue();
        assertThat(result.get(2L)).isTrue();
        assertThat(result.get(999L)).isFalse();
        // Verify optimized single query per ID
        verify(personRepository, times(3)).existsAndActive(anyLong());
        verify(personRepository, never()).existsById(anyLong());
        verify(personRepository, never()).isActive(anyLong());
    }

    @Test
    @DisplayName("getPreviewsByIds - should return list of PersonPreviewDto")
    void getPreviewsByIds_shouldReturnListOfPersonPreviewDto() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L);
        List<PersonEntity> entities = Arrays.asList(
                TestFixtures.IVAN_PETRENKO_ENTITY,
                TestFixtures.MARIA_KOVALENKO_ENTITY
        );
        PersonPreviewDto preview1 = PersonPreviewDto.builder()
                .id(1L)
                .firstName("Іван")
                .lastName("Петренко")
                .build();
        PersonPreviewDto preview2 = PersonPreviewDto.builder()
                .id(2L)
                .firstName("Марія")
                .lastName("Коваленко")
                .build();

        lenient().when(personRepository.findAllById(eq(ids), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(entities);
        lenient().when(personRepository.findAllById(eq(ids))).thenReturn(entities);
        when(personMapper.toPreviewDto(any(PersonEntity.class))).thenAnswer(invocation -> {
            PersonEntity entity = invocation.getArgument(0);
            return entity.getId().equals(1L) ? preview1 : preview2;
        });

        // When
        List<PersonPreviewDto> result = personService.getPreviewsByIds(ids);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(personMapper, times(2)).toPreviewDto(any(PersonEntity.class));
    }
}


package com.micro.person.service.contact;

import com.micro.person.data.dto.contact.ContactCreateDto;
import com.micro.person.data.dto.contact.ContactDto;
import com.micro.person.data.dto.contact.ContactPreviewDto;
import com.micro.person.data.dto.contact.ContactUpdateDto;
import com.micro.person.data.dto.mapper.ContactMapper;
import com.micro.person.data.entities.ContactEntity;
import com.micro.person.data.entities.PersonEntity;
import com.micro.person.data.entities.enums.ContactType;
import com.micro.person.exception.ResourceNotFoundException;
import com.micro.person.repository.ContactRepository;
import com.micro.person.repository.rsql.ContactSearchService;
import com.micro.person.service.graph.GraphBuilderMappingService;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Unit тести для ContactServiceImpl.
 * 
 * Тестує бізнес-логіку ContactService без залежності від бази даних.
 * Використовує Mockito для мокування залежностей.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ContactServiceImpl Unit Tests")
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ContactMapper contactMapper;

    @Mock
    private GraphBuilderMappingService graphBuilderMappingService;

    @Mock
    private ContactSearchService contactSearchService;

    @InjectMocks
    private ContactServiceImpl contactService;

    private ContactEntity testContactEntity;
    private ContactDto testContactDto;
    private PersonEntity testPersonEntity;

    @BeforeEach
    void setUp() {
        testPersonEntity = TestFixtures.IVAN_PETRENKO_ENTITY;
        testContactEntity = TestFixtures.IVAN_EMAIL_CONTACT;
        testContactDto = TestFixtures.EMAIL_CONTACT_DTO;
    }

    // ========== CRUD Tests (from AbstractBaseService) ==========

    @Test
    @DisplayName("findById - should return ContactDto when contact exists")
    void findById_shouldReturnContactDto_whenContactExists() {
        // Given
        Long id = 101L;
        lenient().when(contactRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.of(testContactEntity));
        lenient().when(contactRepository.findById(eq(id))).thenReturn(Optional.of(testContactEntity));
        when(contactMapper.toDto(testContactEntity)).thenReturn(testContactDto);

        // When
        ContactDto result = contactService.findById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(contactMapper).toDto(testContactEntity);
    }

    @Test
    @DisplayName("findById - should throw ResourceNotFoundException when contact not exists")
    void findById_shouldThrowException_whenContactNotExists() {
        // Given
        Long id = 999L;
        lenient().when(contactRepository.findById(eq(id), any(com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph.class)))
                .thenReturn(Optional.empty());
        lenient().when(contactRepository.findById(eq(id))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> contactService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ContactEntity")
                .hasMessageContaining("999");
        verify(contactMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("create - should create and return ContactDto")
    void create_shouldCreateAndReturnContactDto() {
        // Given
        ContactCreateDto inputDto = ContactCreateDto.builder()
                .contactType(ContactType.EMAIL)
                .value("new.contact@example.com")
                .label("Новий контакт")
                .isPrimary(false)
                .build();
        ContactEntity savedEntity = ContactEntity.builder()
                .id(1L)
                .contactType(inputDto.getContactType())
                .value(inputDto.getValue())
                .label(inputDto.getLabel())
                .isPrimary(inputDto.getIsPrimary())
                .active(true)
                .build();
        ContactDto outputDto = ContactDto.builder()
                .id(1L)
                .contactType(inputDto.getContactType())
                .value(inputDto.getValue())
                .label(inputDto.getLabel())
                .isPrimary(inputDto.getIsPrimary())
                .build();

        when(contactMapper.fromCreateDto(inputDto)).thenReturn(savedEntity);
        when(contactRepository.save(savedEntity)).thenReturn(savedEntity);
        when(contactMapper.toDto(savedEntity)).thenReturn(outputDto);

        // When
        ContactDto result = contactService.create(inputDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getValue()).isEqualTo(inputDto.getValue());
        verify(contactMapper).fromCreateDto(inputDto);
        verify(contactRepository).save(savedEntity);
        verify(contactMapper).toDto(savedEntity);
    }

    @Test
    @DisplayName("update - should update and return ContactDto")
    void update_shouldUpdateAndReturnContactDto() {
        // Given
        Long id = 1L;
        ContactUpdateDto inputDto = ContactUpdateDto.builder()
                .value("updated.contact@example.com")
                .label("Оновлений контакт")
                .build();
        ContactEntity existingEntity = TestFixtures.IVAN_EMAIL_CONTACT;
        ContactEntity updatedEntity = existingEntity.toBuilder()
                .value(inputDto.getValue())
                .label(inputDto.getLabel())
                .build();
        ContactDto outputDto = ContactDto.builder()
                .id(id)
                .contactType(updatedEntity.getContactType())
                .value(updatedEntity.getValue())
                .label(updatedEntity.getLabel())
                .isPrimary(updatedEntity.getIsPrimary())
                .build();

        when(contactRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(contactRepository.save(any(ContactEntity.class))).thenReturn(updatedEntity);
        when(contactMapper.toDto(updatedEntity)).thenReturn(outputDto);

        // When
        ContactDto result = contactService.update(id, inputDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(inputDto.getValue());
        verify(contactRepository).findById(id);
        verify(contactMapper).updateFromUpdateDto(inputDto, existingEntity);
        verify(contactRepository).save(existingEntity);
    }

    @Test
    @DisplayName("delete - should delete contact")
    void delete_shouldDeleteContact() {
        // Given
        Long id = 101L;
        when(contactRepository.findById(id)).thenReturn(Optional.of(testContactEntity));

        // When
        contactService.delete(id);

        // Then
        verify(contactRepository).findById(id);
        verify(contactRepository).deleteById(id);
    }

    @Test
    @DisplayName("softDelete - should set active=false")
    void softDelete_shouldSetActiveFalse() {
        // Given
        Long id = 101L;
        ContactEntity inactiveEntity = ContactEntity.builder()
                .id(id)
                .contactType(ContactType.EMAIL)
                .value("test@example.com")
                .label("Email")
                .active(false)
                .build();
        ContactDto inactiveDto = ContactDto.builder()
                .id(id)
                .contactType(ContactType.EMAIL)
                .value("test@example.com")
                .label("Email")
                .build();

        when(contactRepository.softDelete(id, null)).thenReturn(Optional.of(inactiveEntity));
        when(contactMapper.toDto(inactiveEntity)).thenReturn(inactiveDto);

        // When
        ContactDto result = contactService.softDelete(id);

        // Then
        assertThat(result).isNotNull();
        verify(contactRepository).softDelete(id, null);
        verify(contactMapper).toDto(inactiveEntity);
    }

    // ========== Person-Specific Methods Tests ==========

    @Test
    @DisplayName("findByPersonId - should return list of ContactDto")
    void findByPersonId_shouldReturnListOfContactDto() {
        // Given
        Long personId = 1L;
        List<ContactEntity> entities = TestFixtures.IVAN_CONTACTS;
        ContactDto emailDto = TestFixtures.EMAIL_CONTACT_DTO;
        ContactDto phoneDto = TestFixtures.PHONE_CONTACT_DTO;

        when(contactRepository.findByPersonIdAndActiveTrue(personId)).thenReturn(entities);
        when(contactMapper.toDto(any(ContactEntity.class))).thenAnswer(invocation -> {
            ContactEntity entity = invocation.getArgument(0);
            return entity.getContactType() == ContactType.EMAIL ? emailDto : phoneDto;
        });

        // When
        List<ContactDto> result = contactService.findByPersonId(personId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(contactRepository).findByPersonIdAndActiveTrue(personId);
        verify(contactMapper, times(2)).toDto(any(ContactEntity.class));
    }

    @Test
    @DisplayName("findPrimaryByPersonId - should return Optional with ContactDto when found")
    void findPrimaryByPersonId_shouldReturnOptional_whenFound() {
        // Given
        Long personId = 1L;
        ContactEntity primaryContact = ContactEntity.builder()
                .id(101L)
                .contactType(ContactType.EMAIL)
                .value("primary@example.com")
                .label("Основний email")
                .isPrimary(true)
                .active(true)
                .person(testPersonEntity)
                .build();
        ContactDto primaryDto = ContactDto.builder()
                .id(101L)
                .contactType(ContactType.EMAIL)
                .value("primary@example.com")
                .label("Основний email")
                .isPrimary(true)
                .build();

        when(contactRepository.findByPersonIdAndIsPrimaryTrueAndActiveTrue(personId))
                .thenReturn(Optional.of(primaryContact));
        when(contactMapper.toDto(primaryContact)).thenReturn(primaryDto);

        // When
        Optional<ContactDto> result = contactService.findPrimaryByPersonId(personId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getIsPrimary()).isTrue();
        verify(contactRepository).findByPersonIdAndIsPrimaryTrueAndActiveTrue(personId);
    }

    @Test
    @DisplayName("findPrimaryByPersonId - should return empty Optional when not found")
    void findPrimaryByPersonId_shouldReturnEmpty_whenNotFound() {
        // Given
        Long personId = 999L;
        when(contactRepository.findByPersonIdAndIsPrimaryTrueAndActiveTrue(personId))
                .thenReturn(Optional.empty());

        // When
        Optional<ContactDto> result = contactService.findPrimaryByPersonId(personId);

        // Then
        assertThat(result).isEmpty();
        verify(contactRepository).findByPersonIdAndIsPrimaryTrueAndActiveTrue(personId);
        verify(contactMapper, never()).toDto(any());
    }

    // ========== RSQL Search Tests ==========

    @Test
    @DisplayName("search - should return list of ContactDto from RSQL query")
    void search_shouldReturnListOfContactDto_fromRsqlQuery() {
        // Given
        String rsqlQuery = "contactType==EMAIL";
        List<ContactEntity> entities = Collections.singletonList(testContactEntity);

        when(contactSearchService.search(rsqlQuery)).thenReturn(entities);
        when(contactMapper.toDto(testContactEntity)).thenReturn(testContactDto);

        // When
        List<ContactDto> result = contactService.search(rsqlQuery);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(contactSearchService).search(rsqlQuery);
    }

    @Test
    @DisplayName("searchWithPagination - should return Page of ContactDto")
    void searchWithPagination_shouldReturnPageOfContactDto() {
        // Given
        String rsqlQuery = "contactType==EMAIL";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ContactEntity> entityPage = new PageImpl<>(Collections.singletonList(testContactEntity));

        when(contactSearchService.searchWithPagination(rsqlQuery, pageable)).thenReturn(entityPage);
        when(contactMapper.toDto(testContactEntity)).thenReturn(testContactDto);

        // When
        Page<ContactDto> result = contactService.searchWithPagination(rsqlQuery, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(contactSearchService).searchWithPagination(rsqlQuery, pageable);
    }

}


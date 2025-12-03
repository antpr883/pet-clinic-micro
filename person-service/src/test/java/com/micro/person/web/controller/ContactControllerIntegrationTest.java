package com.micro.person.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.person.data.dto.contact.ContactCreateDto;
import com.micro.person.data.dto.contact.ContactDto;
import com.micro.person.data.dto.contact.ContactPreviewDto;
import com.micro.person.data.dto.contact.ContactUpdateDto;
import com.micro.person.data.entities.enums.ContactType;
import com.micro.person.exception.ResourceNotFoundException;
import com.micro.person.service.contact.ContactService;
import com.micro.person.util.TestFixtures;
import com.micro.person.config.jpa.JpaAuditingConfig;
import com.micro.person.web.exception.ControllerExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration тести для ContactController.
 * 
 * Тестує HTTP endpoints через MockMvc без підняття повного Spring контексту.
 * Використовує @WebMvcTest для ізоляції web layer.
 */
@WebMvcTest(controllers = ContactController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration.class,
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
    JpaAuditingConfig.class  // Виключаємо JPA Auditing для уникнення помилок з JPA метамоделлю
})
@Import(ControllerExceptionHandler.class)  // Імпортуємо ControllerExceptionHandler для обробки валідації
@ActiveProfiles("test")
@DisplayName("ContactController Integration Tests")
class ContactControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    private ContactDto testContactDto;

    @BeforeEach
    void setUp() {
        testContactDto = TestFixtures.EMAIL_CONTACT_DTO;
    }

    // ========== GET Tests ==========

    @Test
    @DisplayName("GET /api/v1/contacts/{id} - should return 200 OK with ContactDto")
    void getById_shouldReturn200Ok() throws Exception {
        // Given
        Long id = 101L;
        when(contactService.findById(id)).thenReturn(testContactDto);
        // When & Then
        mockMvc.perform(get("/api/v1/contacts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.id").value(id))
                .andExpect(jsonPath("$.content.value").value("test@example.com"))
                .andExpect(jsonPath("$.content.contactType").value("EMAIL"));

        verify(contactService).findById(id);
    }

    @Test
    @DisplayName("GET /api/v1/contacts/{id} - should return 404 when contact not found")
    void getById_shouldReturn404_whenContactNotFound() throws Exception {
        // Given
        Long id = 999L;
        when(contactService.findById(id))
                .thenThrow(new ResourceNotFoundException("ContactEntity", id));

        // When & Then
        mockMvc.perform(get("/api/v1/contacts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(contactService).findById(id);
    }

    @Test
    @DisplayName("GET /api/v1/persons/{personId}/contacts - should return 200 OK with list of ContactDto")
    void findByPersonId_shouldReturn200Ok() throws Exception {
        // Given
        Long personId = 1L;
        List<ContactDto> dtos = Arrays.asList(
                ContactDto.builder()
                        .id(101L)
                        .contactType(ContactType.EMAIL)
                        .value("ivan.petrenko@example.com")
                        .label("Особистий email")
                        .isPrimary(true)
                        .build(),
                ContactDto.builder()
                        .id(102L)
                        .contactType(ContactType.MOBILE)
                        .value("+380501234567")
                        .label("Мобільний телефон")
                        .isPrimary(false)
                        .build()
        );
        when(contactService.findByPersonId(personId)).thenReturn(dtos);

        // When & Then
        mockMvc.perform(get("/api/v1/contacts/persons/{personId}", personId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isArray());

        verify(contactService).findByPersonId(personId);
    }

    @Test
    @DisplayName("GET /api/v1/contacts/persons/{personId}/primary - should return 200 OK with ContactPreviewDto")
    void findPrimaryByPersonId_shouldReturn200Ok() throws Exception {
        // Given
        Long personId = 1L;
        ContactPreviewDto primaryDto = ContactPreviewDto.builder()
                .id(101L)
                .contactType(ContactType.EMAIL)
                .value("primary@example.com")
                .label("Основний email")
                .isPrimary(true)
                .build();
        when(contactService.findPrimaryPreviewByPersonId(personId))
                .thenReturn(Optional.of(primaryDto));

        // When & Then
        mockMvc.perform(get("/api/v1/contacts/persons/{personId}/primary", personId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.isPrimary").value(true));

        verify(contactService).findPrimaryPreviewByPersonId(personId);
    }

    // ========== POST Tests ==========

    @Test
    @DisplayName("POST /api/v1/contacts - should return 201 Created with ContactDto")
    void create_shouldReturn201Created() throws Exception {
        // Given
        ContactCreateDto inputDto = ContactCreateDto.builder()
                .contactType(ContactType.EMAIL)
                .value("new@example.com")
                .label("Новий email")
                .isPrimary(false)
                .build();
        ContactDto createdDto = ContactDto.builder()
                .id(201L)
                .contactType(ContactType.EMAIL)
                .value("new@example.com")
                .label("Новий email")
                .isPrimary(false)
                .build();

        when(contactService.create(any(ContactCreateDto.class))).thenReturn(createdDto);

        // When & Then
        mockMvc.perform(post("/api/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.id").value(201L))
                .andExpect(jsonPath("$.content.value").value("new@example.com"));

        verify(contactService).create(any(ContactCreateDto.class));
    }

    @Test
    @DisplayName("POST /api/v1/contacts - should return 400 when validation fails")
    void create_shouldReturn400_whenValidationFails() throws Exception {
        // Given - ContactCreateDto without required contactType
        ContactCreateDto invalidDto = ContactCreateDto.builder()
                .value("test@example.com")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists());

        verify(contactService, never()).create(any(ContactCreateDto.class));
    }

    // ========== PUT Tests ==========

    @Test
    @DisplayName("PUT /api/v1/contacts/{id} - should return 200 OK with updated ContactDto")
    void update_shouldReturn200Ok() throws Exception {
        // Given
        Long id = 101L;
        ContactUpdateDto inputDto = ContactUpdateDto.builder()
                .value("updated@example.com")
                .label("Оновлений email")
                .build();
        ContactDto updatedDto = ContactDto.builder()
                .id(id)
                .contactType(ContactType.EMAIL)
                .value("updated@example.com")
                .label("Оновлений email")
                .isPrimary(false)
                .build();

        when(contactService.update(eq(id), any(ContactUpdateDto.class))).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/contacts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.value").value("updated@example.com"));

        verify(contactService).update(eq(id), any(ContactUpdateDto.class));
    }

    // ========== DELETE Tests ==========

    @Test
    @DisplayName("DELETE /api/v1/contacts/{id} - should return 204 No Content (soft delete by default)")
    void delete_shouldReturn204NoContent() throws Exception {
        // Given
        Long id = 101L;
        when(contactService.softDelete(id)).thenReturn(testContactDto);

        // When & Then
        mockMvc.perform(delete("/api/v1/contacts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(contactService).softDelete(id);
        verify(contactService, never()).delete(id);
    }

    // ========== RSQL Search Tests ==========

    @Test
    @DisplayName("GET /api/v1/contacts/search?rsqlQuery=contactType==EMAIL - should return 200 OK")
    void search_shouldReturn200Ok() throws Exception {
        // Given
        String rsqlQuery = "contactType==EMAIL";
        List<ContactDto> dtos = Collections.singletonList(testContactDto);
        when(contactService.search(rsqlQuery)).thenReturn(dtos);

        // When & Then
        mockMvc.perform(get("/api/v1/contacts/search")
                        .param("rsqlQuery", rsqlQuery)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(contactService).search(rsqlQuery);
    }

    @Test
    @DisplayName("GET /api/v1/contacts/search/paginated?rsqlQuery=contactType==EMAIL&page=0&size=10 - should return 200 OK with pagination")
    void searchWithPagination_shouldReturn200Ok() throws Exception {
        // Given
        String rsqlQuery = "contactType==EMAIL";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ContactDto> page = new PageImpl<>(
                Collections.singletonList(testContactDto),
                pageable,
                1L
        );
        when(contactService.searchWithPagination(eq(rsqlQuery), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/contacts/search/paginated")
                        .param("rsqlQuery", rsqlQuery)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.pagination.total").value(1L))
                .andExpect(jsonPath("$.pagination.pages").value(1))
                .andExpect(jsonPath("$.pagination.page").value(0))
                .andExpect(jsonPath("$.pagination.limit").value(10));

        verify(contactService).searchWithPagination(eq(rsqlQuery), any(Pageable.class));
    }
}


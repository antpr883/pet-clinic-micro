package com.micro.person.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.person.data.constants.PersonEntityGraphConstants;
import com.micro.person.data.dto.person.PersonDto;
import com.micro.person.data.dto.person.PersonCreateDto;
import com.micro.person.data.dto.person.PersonFullDto;
import com.micro.person.data.dto.person.PersonFullExtendedDto;
import com.micro.person.data.dto.person.PersonUpdateDto;
import com.micro.person.data.entities.enums.PersonRole;
import com.micro.person.data.dto.person.PersonPreviewDto;
import com.micro.person.data.dto.person.PersonFullPreviewDto;
import com.micro.person.exception.ResourceNotFoundException;
import com.micro.person.service.person.PersonService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration тести для PersonController.
 * 
 * Тестує HTTP endpoints через MockMvc без підняття повного Spring контексту.
 * Використовує @WebMvcTest для ізоляції web layer.
 */
@WebMvcTest(controllers = PersonController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration.class,
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
    JpaAuditingConfig.class  // Виключаємо JPA Auditing для уникнення помилок з JPA метамоделлю
})
@Import(ControllerExceptionHandler.class)  // Імпортуємо ControllerExceptionHandler для обробки валідації
@ActiveProfiles("test")
@DisplayName("PersonController Integration Tests")
class PersonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @Autowired
    private ObjectMapper objectMapper;

    private PersonDto testPersonDto;
    private PersonFullDto testPersonFullDto;
    private PersonPreviewDto testPersonPreviewDto;

    @BeforeEach
    void setUp() {
        testPersonDto = PersonDto.builder()
                .id(1L)
                .firstName("Іван")
                .lastName("Петренко")
                .personRole(PersonRole.OWNER)
                .build();
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

    // ========== GET Tests ==========

    @Test
    @DisplayName("GET /api/v1/persons/{id} - should return 200 OK with PersonFullDto including contacts and addresses")
    void getById_shouldReturn200Ok() throws Exception {
        // Given
        Long id = 1L;
        PersonFullDto fullDto = PersonFullDto.builder()
                .id(id)
                .firstName("Іван")
                .lastName("Петренко")
                .personRole(PersonRole.OWNER)
                .contacts(java.util.Set.of(
                        com.micro.person.data.dto.contact.ContactDto.builder()
                                .id(1L)
                                .contactType(com.micro.person.data.entities.enums.ContactType.EMAIL)
                                .value("ivan.petrenko@example.com")
                                .label("Особистий email")
                                .isPrimary(true)
                                .build()
                ))
                .addresses(java.util.Set.of(
                        com.micro.person.data.dto.address.AddressDto.builder()
                                .id(1L)
                                .addressType(com.micro.person.data.entities.enums.AddressType.HOME)
                                .city("Київ")
                                .street("вул. Хрещатик")
                                .country("Україна")
                                .build()
                ))
                .build();
        when(personService.findFullById(id)).thenReturn(fullDto);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.id").value(id))
                .andExpect(jsonPath("$.content.firstName").value("Іван"))
                .andExpect(jsonPath("$.content.lastName").value("Петренко"))
                .andExpect(jsonPath("$.content.contacts").isArray())
                .andExpect(jsonPath("$.content.contacts[0].value").value("ivan.petrenko@example.com"))
                .andExpect(jsonPath("$.content.addresses").isArray())
                .andExpect(jsonPath("$.content.addresses[0].city").value("Київ"));

        verify(personService).findFullById(id);
    }

    @Test
    @DisplayName("GET /api/v1/persons/{id}/preview - should return 200 OK with PersonPreviewDto")
    void getPreviewById_shouldReturn200Ok() throws Exception {
        // Given
        Long id = 1L;
        when(personService.findPreviewById(id)).thenReturn(testPersonPreviewDto);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/{id}/preview", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.id").value(id))
                .andExpect(jsonPath("$.content.firstName").value("Іван"))
                .andExpect(jsonPath("$.content.lastName").value("Петренко"));

        verify(personService).findPreviewById(id);
    }

    @Test
    @DisplayName("GET /api/v1/persons/{id}/full-preview - should return 200 OK with PersonFullPreviewDto")
    void getFullPreviewById_shouldReturn200Ok() throws Exception {
        // Given
        Long id = 1L;
        PersonFullPreviewDto fullPreviewDto = PersonFullPreviewDto.builder()
                .id(id)
                .fullName("Іван Петренко")
                .role(PersonRole.OWNER)
                .primaryPhone("+380501234567")
                .primaryEmail("ivan@example.com")
                .primaryAddress("Київ, Хрещатик 1")
                .build();
        when(personService.findFullPreviewById(id)).thenReturn(fullPreviewDto);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/{id}/full-preview", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.id").value(id))
                .andExpect(jsonPath("$.content.fullName").value("Іван Петренко"))
                .andExpect(jsonPath("$.content.primaryPhone").value("+380501234567"))
                .andExpect(jsonPath("$.content.primaryEmail").value("ivan@example.com"))
                .andExpect(jsonPath("$.content.primaryAddress").value("Київ, Хрещатик 1"));

        verify(personService).findFullPreviewById(id);
    }

    @Test
    @DisplayName("GET /api/v1/persons/{id}/full-extended - should return 200 OK with PersonFullExtendedDto including contacts and addresses")
    void getFullExtendedById_shouldReturn200Ok() throws Exception {
        // Given
        Long id = 1L;
        PersonFullExtendedDto extendedDto = PersonFullExtendedDto.builder()
                .firstName("Іван")
                .lastName("Петренко")
                .personRole(PersonRole.OWNER)
                .contacts(java.util.Set.of(
                        com.micro.person.data.dto.contact.ContactDto.builder()
                                .id(1L)
                                .contactType(com.micro.person.data.entities.enums.ContactType.EMAIL)
                                .value("ivan.petrenko@example.com")
                                .label("Особистий email")
                                .isPrimary(true)
                                .build()
                ))
                .addresses(java.util.Set.of(
                        com.micro.person.data.dto.address.AddressDto.builder()
                                .id(1L)
                                .addressType(com.micro.person.data.entities.enums.AddressType.HOME)
                                .city("Київ")
                                .street("вул. Хрещатик")
                                .country("Україна")
                                .build()
                ))
                .build();
        extendedDto.setId(id);
        when(personService.findFullExtendedById(id)).thenReturn(extendedDto);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/{id}/full-extended", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.id").value(id))
                .andExpect(jsonPath("$.content.firstName").value("Іван"))
                .andExpect(jsonPath("$.content.lastName").value("Петренко"))
                .andExpect(jsonPath("$.content.contacts").isArray())
                .andExpect(jsonPath("$.content.contacts[0].value").value("ivan.petrenko@example.com"))
                .andExpect(jsonPath("$.content.addresses").isArray())
                .andExpect(jsonPath("$.content.addresses[0].city").value("Київ"));

        verify(personService).findFullExtendedById(id);
    }

    // ========== List All Tests ==========

    @Test
    @DisplayName("GET /api/v1/persons - should return 200 OK with list of PersonFullDto")
    void getAll_shouldReturn200Ok() throws Exception {
        // Given
        List<PersonFullDto> dtos = Collections.singletonList(testPersonFullDto);
        when(personService.findAllFull()).thenReturn(dtos);

        // When & Then
        mockMvc.perform(get("/api/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(personService).findAllFull();
    }

    @Test
    @DisplayName("GET /api/v1/persons/paginated - should return 200 OK with paginated PersonFullDto")
    void getAllWithPagination_shouldReturn200Ok() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<PersonFullDto> page = new PageImpl<>(
                Collections.singletonList(testPersonFullDto),
                pageable,
                1L
        );
        when(personService.findAllFullWithPagination(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.pagination.total").value(1L));

        verify(personService).findAllFullWithPagination(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/persons/previews - should return 200 OK with list of PersonPreviewDto")
    void getAllPreviews_shouldReturn200Ok() throws Exception {
        // Given
        List<PersonPreviewDto> dtos = Collections.singletonList(testPersonPreviewDto);
        when(personService.findAllPreviews()).thenReturn(dtos);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/previews")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(personService).findAllPreviews();
    }

    @Test
    @DisplayName("GET /api/v1/persons/previews/paginated - should return 200 OK with paginated PersonPreviewDto")
    void getAllPreviewsWithPagination_shouldReturn200Ok() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<PersonPreviewDto> page = new PageImpl<>(
                Collections.singletonList(testPersonPreviewDto),
                pageable,
                1L
        );
        when(personService.findAllPreviewsWithPagination(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/previews/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.pagination.total").value(1L));

        verify(personService).findAllPreviewsWithPagination(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/persons/full-previews - should return 200 OK with list of PersonFullPreviewDto")
    void getAllFullPreviews_shouldReturn200Ok() throws Exception {
        // Given
        PersonFullPreviewDto fullPreviewDto = PersonFullPreviewDto.builder()
                .id(1L)
                .fullName("Іван Петренко")
                .role(PersonRole.OWNER)
                .primaryPhone("+380501234567")
                .primaryEmail("ivan@example.com")
                .primaryAddress("Київ, Хрещатик 1")
                .build();
        List<PersonFullPreviewDto> dtos = Collections.singletonList(fullPreviewDto);
        when(personService.findAllFullPreviews()).thenReturn(dtos);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/full-previews")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].fullName").value("Іван Петренко"));

        verify(personService).findAllFullPreviews();
    }

    @Test
    @DisplayName("GET /api/v1/persons/full-previews/paginated - should return 200 OK with paginated PersonFullPreviewDto")
    void getAllFullPreviewsWithPagination_shouldReturn200Ok() throws Exception {
        // Given
        PersonFullPreviewDto fullPreviewDto = PersonFullPreviewDto.builder()
                .id(1L)
                .fullName("Іван Петренко")
                .role(PersonRole.OWNER)
                .primaryPhone("+380501234567")
                .primaryEmail("ivan@example.com")
                .primaryAddress("Київ, Хрещатик 1")
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<PersonFullPreviewDto> page = new PageImpl<>(
                Collections.singletonList(fullPreviewDto),
                pageable,
                1L
        );
        when(personService.findAllFullPreviewsWithPagination(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/full-previews/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.pagination.total").value(1L))
                .andExpect(jsonPath("$.content[0].fullName").value("Іван Петренко"));

        verify(personService).findAllFullPreviewsWithPagination(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/persons/{id} - should return 404 when person not found")
    void getById_shouldReturn404_whenPersonNotFound() throws Exception {
        // Given
        Long id = 999L;
        when(personService.findFullById(id))
                .thenThrow(new ResourceNotFoundException("PersonEntity", id));

        // When & Then
        mockMvc.perform(get("/api/v1/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(personService).findFullById(id);
    }



    // ========== POST Tests ==========

    @Test
    @DisplayName("POST /api/v1/persons - should return 201 Created with PersonDto")
    void create_shouldReturn201Created() throws Exception {
        // Given
        PersonCreateDto inputDto = PersonCreateDto.builder()
                .firstName("Нова")
                .lastName("Персона")
                .personRole(PersonRole.OWNER)
                .build();
        PersonFullDto createdDto = PersonFullDto.builder()
                .id(1L)
                .firstName("Нова")
                .lastName("Персона")
                .personRole(PersonRole.OWNER)
                .build();

        when(personService.create(any(PersonCreateDto.class))).thenReturn(createdDto);

        // When & Then
        mockMvc.perform(post("/api/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.id").value(1L))
                .andExpect(jsonPath("$.content.firstName").value("Нова"))
                .andExpect(jsonPath("$.content.lastName").value("Персона"));

        verify(personService).create(any(PersonCreateDto.class));
    }

    @Test
    @DisplayName("POST /api/v1/persons - should return 400 when validation fails")
    void create_shouldReturn400_whenValidationFails() throws Exception {
        // Given - PersonCreateDto without required firstName
        PersonCreateDto invalidDto = PersonCreateDto.builder()
                .lastName("Персона")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists());

        verify(personService, never()).create(any(PersonCreateDto.class));
    }

    // ========== PUT Tests ==========

    @Test
    @DisplayName("PUT /api/v1/persons/{id} - should return 200 OK with updated PersonDto")
    void update_shouldReturn200Ok() throws Exception {
        // Given
        Long id = 1L;
        PersonUpdateDto inputDto = PersonUpdateDto.builder()
                .firstName("Оновлене")
                .lastName("Ім'я")
                .personRole(PersonRole.OWNER)
                .build();
        PersonFullDto updatedDto = PersonFullDto.builder()
                .id(id)
                .firstName("Оновлене")
                .lastName("Ім'я")
                .personRole(PersonRole.OWNER)
                .build();

        when(personService.update(eq(id), any(PersonUpdateDto.class))).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.id").value(id))
                .andExpect(jsonPath("$.content.firstName").value("Оновлене"));

        verify(personService).update(eq(id), any(PersonUpdateDto.class));
    }

    @Test
    @DisplayName("PUT /api/v1/persons/{id} - should return 404 when person not found")
    void update_shouldReturn404_whenPersonNotFound() throws Exception {
        // Given
        Long id = 999L;
        PersonUpdateDto inputDto = PersonUpdateDto.builder()
                .firstName("Оновлене")
                .lastName("Ім'я")
                .personRole(PersonRole.OWNER)
                .build();

        when(personService.update(eq(id), any(PersonUpdateDto.class)))
                .thenThrow(new ResourceNotFoundException("PersonEntity", id));

        // When & Then
        mockMvc.perform(put("/api/v1/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isNotFound());

        verify(personService).update(eq(id), any(PersonUpdateDto.class));
    }

    // ========== DELETE Tests ==========

    @Test
    @DisplayName("DELETE /api/v1/persons/{id} - should return 204 No Content (soft delete by default)")
    void delete_shouldReturn204NoContent_softDeleteByDefault() throws Exception {
        // Given
        Long id = 1L;
        when(personService.softDelete(id)).thenReturn(testPersonDto);

        // When & Then
        mockMvc.perform(delete("/api/v1/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(personService).softDelete(id);
        verify(personService, never()).delete(id);
    }

    @Test
    @DisplayName("DELETE /api/v1/persons/{id}?hard=false - should return 204 No Content (soft delete)")
    void delete_shouldReturn204NoContent_softDelete() throws Exception {
        // Given
        Long id = 1L;
        when(personService.softDelete(id)).thenReturn(testPersonDto);

        // When & Then
        mockMvc.perform(delete("/api/v1/persons/{id}", id)
                        .param("hard", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(personService).softDelete(id);
        verify(personService, never()).delete(id);
    }

    @Test
    @DisplayName("DELETE /api/v1/persons/{id}?hard=true - should return 204 No Content (hard delete)")
    void delete_shouldReturn204NoContent_hardDelete() throws Exception {
        // Given
        Long id = 1L;
        // PersonService extends BaseService which has delete() method - використовуємо через reflection або просто мокуємо через контролер
        // Контролер викликає personService.delete(id) або personService.softDelete(id)
        doNothing().when(personService).delete(id);

        // When & Then
        mockMvc.perform(delete("/api/v1/persons/{id}", id)
                        .param("hard", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(personService).delete(id);
        verify(personService, never()).softDelete(id);
    }

    // ========== RSQL Search Tests ==========

    @Test
    @DisplayName("GET /api/v1/persons/search?rsqlQuery=firstName==Іван - should return 200 OK")
    void search_shouldReturn200Ok() throws Exception {
        // Given
        String rsqlQuery = "firstName==Іван";
        List<PersonFullDto> dtos = Collections.singletonList(testPersonFullDto);
        when(personService.search(eq(rsqlQuery))).thenReturn(dtos);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/search")
                        .param("rsqlQuery", rsqlQuery)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(personService).search(eq(rsqlQuery));
    }

    @Test
    @DisplayName("GET /api/v1/persons/search?rsqlQuery=firstName==Іван&graphAttributes=contacts,addresses - should return 200 OK (graphAttributes ignored)")
    void search_shouldReturn200Ok_withGraphAttributes() throws Exception {
        // Given
        String rsqlQuery = "firstName==Іван";
        List<PersonFullDto> dtos = Collections.singletonList(testPersonFullDto);
        when(personService.search(eq(rsqlQuery))).thenReturn(dtos);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/search")
                        .param("rsqlQuery", rsqlQuery)
                        .param("graphAttributes", 
                                PersonEntityGraphConstants.CONTACTS, 
                                PersonEntityGraphConstants.ADDRESSES)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(personService).search(eq(rsqlQuery));
    }

    @Test
    @DisplayName("GET /api/v1/persons/search/paginated?rsqlQuery=firstName==Іван&page=0&size=10 - should return 200 OK with pagination")
    void searchWithPagination_shouldReturn200Ok() throws Exception {
        // Given
        String rsqlQuery = "firstName==Іван";
        Pageable pageable = PageRequest.of(0, 10);
        Page<PersonFullDto> page = new PageImpl<>(
                Collections.singletonList(testPersonFullDto),
                pageable,
                1L
        );
        when(personService.searchWithPagination(eq(rsqlQuery), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/search/paginated")
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

        verify(personService).searchWithPagination(eq(rsqlQuery), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/persons/search/paginated?rsqlQuery=firstName==Іван&graphAttributes=contacts - should return 200 OK with pagination (graphAttributes ignored)")
    void searchWithPagination_shouldReturn200Ok_withGraphAttributes() throws Exception {
        // Given
        String rsqlQuery = "firstName==Іван";
        Pageable pageable = PageRequest.of(0, 10);
        Page<PersonFullDto> page = new PageImpl<>(
                Collections.singletonList(testPersonFullDto),
                pageable,
                1L
        );
        when(personService.searchWithPagination(eq(rsqlQuery), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/search/paginated")
                        .param("rsqlQuery", rsqlQuery)
                        .param("page", "0")
                        .param("size", "10")
                        .param("graphAttributes", PersonEntityGraphConstants.CONTACTS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.pagination.total").value(1L));

        verify(personService).searchWithPagination(eq(rsqlQuery), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/persons/search/preview?rsqlQuery=firstName==Іван - should return 200 OK with PersonPreviewDto")
    void searchPreview_shouldReturn200Ok() throws Exception {
        // Given
        String rsqlQuery = "firstName==Іван";
        List<PersonPreviewDto> dtos = Collections.singletonList(testPersonPreviewDto);
        when(personService.searchPreview(rsqlQuery)).thenReturn(dtos);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/search/preview")
                        .param("rsqlQuery", rsqlQuery)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("Іван"));

        verify(personService).searchPreview(rsqlQuery);
    }

    @Test
    @DisplayName("GET /api/v1/persons/search/preview/paginated?rsqlQuery=firstName==Іван - should return 200 OK with pagination")
    void searchPreviewWithPagination_shouldReturn200Ok() throws Exception {
        // Given
        String rsqlQuery = "firstName==Іван";
        Pageable pageable = PageRequest.of(0, 10);
        Page<PersonPreviewDto> page = new PageImpl<>(
                Collections.singletonList(testPersonPreviewDto),
                pageable,
                1L
        );
        when(personService.searchPreviewWithPagination(eq(rsqlQuery), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/search/preview/paginated")
                        .param("rsqlQuery", rsqlQuery)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.pagination.total").value(1L));

        verify(personService).searchPreviewWithPagination(eq(rsqlQuery), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/persons/search/full-preview?rsqlQuery=firstName==Іван - should return 200 OK with PersonFullPreviewDto")
    void searchFullPreview_shouldReturn200Ok() throws Exception {
        // Given
        String rsqlQuery = "firstName==Іван";
        PersonFullPreviewDto fullPreviewDto = PersonFullPreviewDto.builder()
                .id(1L)
                .fullName("Іван Петренко")
                .role(PersonRole.OWNER)
                .primaryPhone("+380501234567")
                .primaryEmail("ivan@example.com")
                .primaryAddress("Київ, Хрещатик 1")
                .build();
        List<PersonFullPreviewDto> dtos = Collections.singletonList(fullPreviewDto);
        when(personService.searchFullPreview(rsqlQuery)).thenReturn(dtos);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/search/full-preview")
                        .param("rsqlQuery", rsqlQuery)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].fullName").value("Іван Петренко"))
                .andExpect(jsonPath("$.content[0].primaryPhone").value("+380501234567"))
                .andExpect(jsonPath("$.content[0].primaryEmail").value("ivan@example.com"))
                .andExpect(jsonPath("$.content[0].primaryAddress").value("Київ, Хрещатик 1"));

        verify(personService).searchFullPreview(rsqlQuery);
    }

    @Test
    @DisplayName("GET /api/v1/persons/search/full-preview/paginated?rsqlQuery=firstName==Іван - should return 200 OK with pagination")
    void searchFullPreviewWithPagination_shouldReturn200Ok() throws Exception {
        // Given
        String rsqlQuery = "firstName==Іван";
        PersonFullPreviewDto fullPreviewDto = PersonFullPreviewDto.builder()
                .id(1L)
                .fullName("Іван Петренко")
                .role(PersonRole.OWNER)
                .primaryPhone("+380501234567")
                .primaryEmail("ivan@example.com")
                .primaryAddress("Київ, Хрещатик 1")
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<PersonFullPreviewDto> page = new PageImpl<>(
                Collections.singletonList(fullPreviewDto),
                pageable,
                1L
        );
        when(personService.searchFullPreviewWithPagination(eq(rsqlQuery), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/search/full-preview/paginated")
                        .param("rsqlQuery", rsqlQuery)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.pagination.total").value(1L))
                .andExpect(jsonPath("$.content[0].fullName").value("Іван Петренко"));

        verify(personService).searchFullPreviewWithPagination(eq(rsqlQuery), any(Pageable.class));
    }

    // ========== Microservice Integration Tests ==========

    @Test
    @DisplayName("POST /api/v1/persons/check-exist - should return 200 OK with Map")
    void checkPersonsExist_shouldReturn200Ok() throws Exception {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L, 999L);
        Map<Long, Boolean> result = Map.of(
                1L, true,
                2L, true,
                999L, false
        );
        when(personService.checkPersonsExist(ids)).thenReturn(result);

        // When & Then
        mockMvc.perform(post("/api/v1/persons/check-existence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content['1']").value(true))
                .andExpect(jsonPath("$.content['2']").value(true))
                .andExpect(jsonPath("$.content['999']").value(false));

        verify(personService).checkPersonsExist(ids);
    }

    @Test
    @DisplayName("POST /api/v1/persons/previews/by-ids - should return 200 OK with list of PersonPreviewDto")
    void getPreviewsByIds_shouldReturn200Ok() throws Exception {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L);
        List<PersonPreviewDto> previews = Arrays.asList(
                PersonPreviewDto.builder().id(1L).firstName("Іван").lastName("Петренко").build(),
                PersonPreviewDto.builder().id(2L).firstName("Марія").lastName("Коваленко").build()
        );
        when(personService.getPreviewsByIds(ids)).thenReturn(previews);

        // When & Then
        mockMvc.perform(post("/api/v1/persons/previews/by-ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(personService).getPreviewsByIds(ids);
    }

    @Test
    @DisplayName("GET /api/v1/persons/count/active - should return 200 OK with count")
    void countActive_shouldReturn200Ok() throws Exception {
        // Given
        long count = 42L;
        when(personService.countActive()).thenReturn(count);

        // When & Then
        mockMvc.perform(get("/api/v1/persons/count/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").value(42L));

        verify(personService).countActive();
    }
}


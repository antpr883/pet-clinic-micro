package com.micro.person.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.person.data.dto.address.AddressCreateDto;
import com.micro.person.data.dto.address.AddressDto;
import com.micro.person.data.dto.address.AddressPreviewDto;
import com.micro.person.data.dto.address.AddressUpdateDto;
import com.micro.person.data.entities.enums.AddressType;
import com.micro.person.exception.ResourceNotFoundException;
import com.micro.person.service.address.AddressService;
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
 * Integration тести для AddressController.
 * 
 * Тестує HTTP endpoints через MockMvc без підняття повного Spring контексту.
 * Використовує @WebMvcTest для ізоляції web layer.
 */
@WebMvcTest(controllers = AddressController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration.class,
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
    JpaAuditingConfig.class  // Виключаємо JPA Auditing для уникнення помилок з JPA метамоделлю
})
@Import(ControllerExceptionHandler.class)  // Імпортуємо ControllerExceptionHandler для обробки валідації
@ActiveProfiles("test")
@DisplayName("AddressController Integration Tests")
class AddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    private AddressDto testAddressDto;

    @BeforeEach
    void setUp() {
        testAddressDto = AddressDto.builder()
                .id(1L)
                .addressType(AddressType.HOME)
                .country("Україна")
                .city("Київ")
                .street("вул. Хрещатик")
                .building("1")
                .postalCode("01001")
                .build();
    }

    // ========== GET Tests ==========

    @Test
    @DisplayName("GET /api/v1/addresses/{id} - should return 200 OK with AddressDto")
    void getById_shouldReturn200Ok() throws Exception {
        // Given
        Long id = 1L;
        when(addressService.findById(id)).thenReturn(testAddressDto);

        // When & Then
        mockMvc.perform(get("/api/v1/addresses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.id").value(id))
                .andExpect(jsonPath("$.content.city").value("Київ"))
                .andExpect(jsonPath("$.content.addressType").value("HOME"));

        verify(addressService).findById(id);
    }

    @Test
    @DisplayName("GET /api/v1/addresses/{id} - should return 404 when address not found")
    void getById_shouldReturn404_whenAddressNotFound() throws Exception {
        // Given
        Long id = 999L;
        when(addressService.findById(id))
                .thenThrow(new ResourceNotFoundException("AddressEntity", id));

        // When & Then
        mockMvc.perform(get("/api/v1/addresses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(addressService).findById(id);
    }

    // ========== POST Tests ==========

    @Test
    @DisplayName("POST /api/v1/addresses - should return 201 Created with AddressDto")
    void create_shouldReturn201Created() throws Exception {
        // Given
        AddressCreateDto inputDto = AddressCreateDto.builder()
                .addressType(AddressType.HOME)
                .country("Україна")
                .city("Львів")
                .street("вул. Свободи")
                .building("10")
                .postalCode("79000")
                .build();
        AddressDto createdDto = AddressDto.builder()
                .id(2L)
                .addressType(AddressType.HOME)
                .country("Україна")
                .city("Львів")
                .street("вул. Свободи")
                .building("10")
                .postalCode("79000")
                .build();

        when(addressService.create(any(AddressCreateDto.class))).thenReturn(createdDto);

        // When & Then
        mockMvc.perform(post("/api/v1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.id").value(2L))
                .andExpect(jsonPath("$.content.city").value("Львів"));

        verify(addressService).create(any(AddressCreateDto.class));
    }

    @Test
    @DisplayName("POST /api/v1/addresses - should return 400 when validation fails")
    void create_shouldReturn400_whenValidationFails() throws Exception {
        // Given - AddressCreateDto without required addressType
        AddressCreateDto invalidDto = AddressCreateDto.builder()
                .city("Київ")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists());

        verify(addressService, never()).create(any(AddressCreateDto.class));
    }

    // ========== PUT Tests ==========

    @Test
    @DisplayName("PUT /api/v1/addresses/{id} - should return 200 OK with updated AddressDto")
    void update_shouldReturn200Ok() throws Exception {
        // Given
        Long id = 1L;
        AddressUpdateDto inputDto = AddressUpdateDto.builder()
                .city("Одеса")
                .street("вул. Дерибасівська")
                .build();
        AddressDto updatedDto = AddressDto.builder()
                .id(id)
                .addressType(AddressType.HOME)
                .country("Україна")
                .city("Одеса")
                .street("вул. Дерибасівська")
                .building("1")
                .postalCode("01001")
                .build();

        when(addressService.update(eq(id), any(AddressUpdateDto.class))).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/addresses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.city").value("Одеса"));

        verify(addressService).update(eq(id), any(AddressUpdateDto.class));
    }

    @Test
    @DisplayName("PUT /api/v1/addresses/{id} - should return 404 when address not found")
    void update_shouldReturn404_whenAddressNotFound() throws Exception {
        // Given
        Long id = 999L;
        AddressUpdateDto inputDto = AddressUpdateDto.builder()
                .city("Одеса")
                .build();

        when(addressService.update(eq(id), any(AddressUpdateDto.class)))
                .thenThrow(new ResourceNotFoundException("AddressEntity", id));

        // When & Then
        mockMvc.perform(put("/api/v1/addresses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isNotFound());

        verify(addressService).update(eq(id), any(AddressUpdateDto.class));
    }

    // ========== DELETE Tests ==========

    @Test
    @DisplayName("DELETE /api/v1/addresses/{id} - should return 204 No Content (soft delete by default)")
    void delete_shouldReturn204NoContent_softDeleteByDefault() throws Exception {
        // Given
        Long id = 1L;
        when(addressService.softDelete(id)).thenReturn(testAddressDto);

        // When & Then
        mockMvc.perform(delete("/api/v1/addresses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(addressService).softDelete(id);
        verify(addressService, never()).delete(id);
    }

    @Test
    @DisplayName("DELETE /api/v1/addresses/{id}?hard=false - should return 204 No Content (soft delete)")
    void delete_shouldReturn204NoContent_softDelete() throws Exception {
        // Given
        Long id = 1L;
        when(addressService.softDelete(id)).thenReturn(testAddressDto);

        // When & Then
        mockMvc.perform(delete("/api/v1/addresses/{id}", id)
                        .param("hard", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(addressService).softDelete(id);
        verify(addressService, never()).delete(id);
    }

    @Test
    @DisplayName("DELETE /api/v1/addresses/{id}?hard=true - should return 204 No Content (hard delete)")
    void delete_shouldReturn204NoContent_hardDelete() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(addressService).delete(id);

        // When & Then
        mockMvc.perform(delete("/api/v1/addresses/{id}", id)
                        .param("hard", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(addressService).delete(id);
        verify(addressService, never()).softDelete(id);
    }

    // ========== RSQL Search Tests ==========

    @Test
    @DisplayName("GET /api/v1/addresses/search?rsqlQuery=city==Київ - should return 200 OK")
    void search_shouldReturn200Ok() throws Exception {
        // Given
        String rsqlQuery = "city==Київ";
        List<AddressDto> dtos = Collections.singletonList(testAddressDto);
        when(addressService.search(rsqlQuery)).thenReturn(dtos);

        // When & Then
        mockMvc.perform(get("/api/v1/addresses/search")
                        .param("rsqlQuery", rsqlQuery)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(addressService).search(rsqlQuery);
    }

    @Test
    @DisplayName("GET /api/v1/addresses/search/paginated?rsqlQuery=city==Київ&page=0&size=10 - should return 200 OK with pagination")
    void searchWithPagination_shouldReturn200Ok() throws Exception {
        // Given
        String rsqlQuery = "city==Київ";
        Pageable pageable = PageRequest.of(0, 10);
        Page<AddressDto> page = new PageImpl<>(
                Collections.singletonList(testAddressDto),
                pageable,
                1L
        );
        when(addressService.searchWithPagination(eq(rsqlQuery), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/addresses/search/paginated")
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

        verify(addressService).searchWithPagination(eq(rsqlQuery), any(Pageable.class));
    }

    // ========== Person-specific Methods Tests ==========

    @Test
    @DisplayName("GET /api/v1/addresses/persons/{personId} - should return 200 OK with list of AddressDto")
    void findByPersonId_shouldReturn200Ok() throws Exception {
        // Given
        Long personId = 1L;
        List<AddressDto> dtos = Arrays.asList(
                AddressDto.builder()
                        .id(1L)
                        .addressType(AddressType.HOME)
                        .city("Київ")
                        .street("вул. Хрещатик")
                        .build(),
                AddressDto.builder()
                        .id(2L)
                        .addressType(AddressType.WORK)
                        .city("Київ")
                        .street("вул. Банкова")
                        .build()
        );
        when(addressService.findByPersonId(personId)).thenReturn(dtos);

        // When & Then
        mockMvc.perform(get("/api/v1/addresses/persons/{personId}", personId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(addressService).findByPersonId(personId);
    }

    @Test
    @DisplayName("GET /api/v1/addresses/persons/{personId}/primary - should return 200 OK with AddressPreviewDto")
    void findPrimaryByPersonId_shouldReturn200Ok() throws Exception {
        // Given
        Long personId = 1L;
        AddressPreviewDto primaryDto = AddressPreviewDto.builder()
                .id(1L)
                .addressType(AddressType.HOME)
                .city("Київ")
                .street("вул. Хрещатик")
                .build();
        when(addressService.findPrimaryPreviewByPersonId(personId))
                .thenReturn(Optional.of(primaryDto));

        // When & Then
        mockMvc.perform(get("/api/v1/addresses/persons/{personId}/primary", personId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.addressType").value("HOME"));

        verify(addressService).findPrimaryPreviewByPersonId(personId);
    }

    @Test
    @DisplayName("GET /api/v1/addresses/persons/{personId}/primary - should return 200 OK with null when primary address not found")
    void findPrimaryByPersonId_shouldReturn200Ok_whenNotFound() throws Exception {
        // Given
        Long personId = 999L;
        when(addressService.findPrimaryPreviewByPersonId(personId))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/addresses/persons/{personId}/primary", personId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").isEmpty());

        verify(addressService).findPrimaryPreviewByPersonId(personId);
    }
}


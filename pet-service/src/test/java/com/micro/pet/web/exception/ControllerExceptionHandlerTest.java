package com.micro.pet.web.exception;

import com.micro.pet.exception.ResourceNotFoundException;
import com.micro.pet.web.response.ErrorResponse;
import com.micro.pet.web.response.ValidationErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit тести для ControllerExceptionHandler.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ControllerExceptionHandler Tests")
class ControllerExceptionHandlerTest {

    @InjectMocks
    private ControllerExceptionHandler exceptionHandler;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        webRequest = new ServletWebRequest(servletRequest);
    }

    @Test
    @DisplayName("handleResourceNotFoundException - should return 404 NOT FOUND")
    void handleResourceNotFoundException_shouldReturn404NotFound() {
        // Given
        ResourceNotFoundException ex = new ResourceNotFoundException("PetEntity", 1L);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).contains("PetEntity");
    }

    @Test
    @DisplayName("handleNoSuchElementException - should return 404 NOT FOUND")
    void handleNoSuchElementException_shouldReturn404NotFound() {
        // Given
        NoSuchElementException ex = new NoSuchElementException("Pet not found");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNoSuchElementException(ex, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    @DisplayName("handleIllegalArgumentException - should return 400 BAD REQUEST")
    void handleIllegalArgumentException_shouldReturn400BadRequest() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("handleConstraintViolationException - should return 400 BAD REQUEST with validation errors")
    void handleConstraintViolationException_shouldReturn400BadRequest() {
        // Given - Create mock constraint violation
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("name");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("Name is required");
        
        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation);
        
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", violations);

        // When
        ResponseEntity<ValidationErrorResponse> response = 
                exceptionHandler.handleConstraintViolationException(ex, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("handleMethodArgumentNotValidException - should return 400 BAD REQUEST with field errors")
    void handleMethodArgumentNotValidException_shouldReturn400BadRequest() throws Exception {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("petCreateDto", "name", "Name is required");
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(ex.getMessage()).thenReturn("Validation failed");

        // When
        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(
                ex, null, HttpStatus.BAD_REQUEST, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ValidationErrorResponse.class);
        ValidationErrorResponse errorResponse = (ValidationErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("handleGlobalException - should return 500 INTERNAL SERVER ERROR")
    void handleGlobalException_shouldReturn500InternalServerError() {
        // Given
        Exception ex = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getDetails()).isNotNull();
    }

    @Test
    @DisplayName("handleGlobalException - should handle null message")
    void handleGlobalException_shouldHandleNullMessage() {
        // Given
        Exception ex = new RuntimeException((String) null);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        // When message is null, handler should use default message
        assertThat(response.getBody().getMessage()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
    }
}


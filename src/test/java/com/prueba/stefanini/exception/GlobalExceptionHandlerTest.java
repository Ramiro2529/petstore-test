package com.prueba.stefanini.exception;

import com.prueba.stefanini.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler - Tests unitarios")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("handlePetNotFound - debe retornar 404 con el mensaje de la excepcion")
    void handlePetNotFound_shouldReturn404_withExceptionMessage() {
        PetNotFoundException ex = new PetNotFoundException(5L);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handlePetNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessages()).contains("No se encontro la mascota con id: 5");
    }

    @Test
    @DisplayName("handleValidationErrors - debe retornar 400 con la lista de errores de campo")
    void handleValidationErrors_shouldReturn400_withFieldErrorMessages() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("petRequest", "name", "El campo name es obligatorio");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Validation Error");
        assertThat(response.getBody().getMessages()).contains("'name': El campo name es obligatorio");
    }

    @Test
    @DisplayName("handleConstraintViolation - debe retornar 400 cuando falla validacion en PathVariable")
    void handleConstraintViolation_shouldReturn400_withViolationMessages() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        jakarta.validation.Path path = mock(jakarta.validation.Path.class);
        when(path.toString()).thenReturn("getPetById.petId");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("El petId debe ser un numero positivo");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Validation Error");
    }

    @Test
    @DisplayName("handleNotReadable - debe retornar 400 con mensaje de cuerpo invalido")
    void handleNotReadable_shouldReturn400_withMalformedRequestMessage() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "JSON parse error",
                new MockHttpInputMessage("bad json".getBytes(StandardCharsets.UTF_8)));

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Malformed Request");
    }

    @Test
    @DisplayName("handleClientError - debe retornar el mismo codigo HTTP que retorno la API externa")
    void handleClientError_shouldReturnSameHttpStatus_asExternalApi() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleClientError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("External API Error");
    }

    @Test
    @DisplayName("handleServerError - debe retornar 502 cuando la API externa falla con 5xx")
    void handleServerError_shouldReturn502_whenExternalApiFailsWith5xx() {
        HttpServerErrorException ex = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", null, null, null);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleServerError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(502);
        assertThat(response.getBody().getError()).isEqualTo("External Server Error");
    }

    @Test
    @DisplayName("handleGenericError - debe retornar 500 con mensaje de error interno")
    void handleGenericError_shouldReturn500_withInternalServerErrorMessage() {
        Exception ex = new Exception("Error inesperado");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessages()).contains("Ocurrio un error inesperado");
    }
}

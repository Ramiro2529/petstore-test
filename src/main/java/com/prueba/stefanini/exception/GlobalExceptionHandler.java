package com.prueba.stefanini.exception;

import com.prueba.stefanini.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Manejador global de excepciones para todos los controladores REST.
 *
 * <p>Intercepta las excepciones lanzadas en cualquier capa de la aplicacion
 * y las transforma en respuestas HTTP estandarizadas con estructura
 * {@link ErrorResponse}. Garantiza que el cliente siempre reciba un cuerpo
 * de error consistente independientemente del tipo de fallo.</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja la excepcion cuando una mascota no es encontrada en la API externa.
     *
     * @param ex excepcion lanzada por el servicio cuando el id no existe
     * @return {@link ResponseEntity} con estado HTTP 404 y mensaje descriptivo
     */
    @ExceptionHandler(PetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePetNotFound(PetNotFoundException ex) {
        log.warn("Mascota no encontrada: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error("Not Found")
                        .messages(List.of(ex.getMessage()))
                        .timestamp(OffsetDateTime.now())
                        .build());
    }

    /**
     * Maneja los errores de validacion de campos producidos por la anotacion {@code @Valid}.
     *
     * <p>Extrae los mensajes de error por campo desde el {@code BindingResult}
     * y los retorna en formato legible {@code 'campo': mensaje}.</p>
     *
     * @param ex excepcion lanzada por Spring cuando falla la validacion del body
     * @return {@link ResponseEntity} con estado HTTP 400 y lista de errores por campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> "'" + fe.getField() + "': " + fe.getDefaultMessage())
                .toList();

        log.warn("Error de validacion en body: {}", messages);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Validation Error")
                        .messages(messages)
                        .timestamp(OffsetDateTime.now())
                        .build());
    }

    /**
     * Maneja los errores de validacion de parametros de ruta producidos por {@code @Validated}.
     *
     * @param ex excepcion lanzada cuando falla una constraint en un {@code @PathVariable}
     * @return {@link ResponseEntity} con estado HTTP 400 y lista de violaciones
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> messages = ex.getConstraintViolations()
                .stream()
                .map(cv -> "'" + cv.getPropertyPath() + "': " + cv.getMessage())
                .toList();

        log.warn("Error de validacion en parametros: {}", messages);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Validation Error")
                        .messages(messages)
                        .timestamp(OffsetDateTime.now())
                        .build());
    }

    /**
     * Maneja los errores producidos por un cuerpo de solicitud malformado o con tipo incorrecto.
     *
     * @param ex excepcion lanzada por Spring cuando el body no puede ser leido o parseado
     * @return {@link ResponseEntity} con estado HTTP 400 y mensaje de cuerpo invalido
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Body no legible: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Malformed Request")
                        .messages(List.of("El cuerpo de la solicitud es invalido o tiene formato incorrecto"))
                        .timestamp(OffsetDateTime.now())
                        .build());
    }

    /**
     * Maneja los errores HTTP 4xx retornados por la API externa Petstore.
     *
     * @param ex excepcion que encapsula el codigo HTTP y el mensaje de error de la API externa
     * @return {@link ResponseEntity} con el mismo codigo HTTP recibido de la API externa
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleClientError(HttpClientErrorException ex) {
        log.error("Error 4xx del cliente externo: {} - {}", ex.getStatusCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ErrorResponse.builder()
                        .status(ex.getStatusCode().value())
                        .error("External API Error")
                        .messages(List.of(ex.getMessage()))
                        .timestamp(OffsetDateTime.now())
                        .build());
    }

    /**
     * Maneja los errores HTTP 5xx retornados por la API externa Petstore.
     *
     * @param ex excepcion que encapsula el error de servidor de la API externa
     * @return {@link ResponseEntity} con estado HTTP 502 Bad Gateway
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleServerError(HttpServerErrorException ex) {
        log.error("Error 5xx del servidor externo: {} - {}", ex.getStatusCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_GATEWAY.value())
                        .error("External Server Error")
                        .messages(List.of("El servicio externo no esta disponible en este momento"))
                        .timestamp(OffsetDateTime.now())
                        .build());
    }

    /**
     * Maneja cualquier excepcion no controlada explicitamente por los otros handlers.
     *
     * @param ex excepcion generica no controlada lanzada en cualquier capa
     * @return {@link ResponseEntity} con estado HTTP 500 y mensaje generico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error("Internal Server Error")
                        .messages(List.of("Ocurrio un error inesperado"))
                        .timestamp(OffsetDateTime.now())
                        .build());
    }
}

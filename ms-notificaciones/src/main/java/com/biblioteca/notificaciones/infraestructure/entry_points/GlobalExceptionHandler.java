package com.biblioteca.notificaciones.infraestructure.entry_points;

import com.biblioteca.notificaciones.application.dto.ApiResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Error interno";
        log.warn("RuntimeException en ms-notificaciones: {}", msg);
        int status = msg.startsWith("No existe") ? 404 : 400;
        return ResponseEntity.status(status)
                .body(new ApiResponseDTO<>(false, msg, null));
    }

    /**
     * Captura errores de deserialización de Jackson.
     * Sin este handler, cuando llega un tipo desconocido el error se traga silenciosamente.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleDeserializacion(HttpMessageNotReadableException ex) {
        log.error("Error deserializando petición entrante: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ApiResponseDTO<>(false,
                        "Error al leer el cuerpo de la petición: " + ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("Validación fallida en ms-notificaciones: {}", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<>(false, "Validación fallida: " + mensaje, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGeneral(Exception ex) {
        log.error("Error inesperado en ms-notificaciones: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(false, "Error interno del servidor", null));
    }
}

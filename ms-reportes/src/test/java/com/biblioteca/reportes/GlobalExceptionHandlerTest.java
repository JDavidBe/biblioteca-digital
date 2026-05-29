package com.biblioteca.reportes;

import com.biblioteca.reportes.application.dto.ApiResponseDTO;
import com.biblioteca.reportes.infraestructure.entry_points.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler - Pruebas unitarias directas")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleRuntime: retorna 400 con mensaje de la excepción")
    void handleRuntime_retorna400() {
        RuntimeException ex = new RuntimeException("error de negocio");
        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleRuntime(ex);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getExito()).isFalse();
        assertThat(response.getBody().getMensaje()).isEqualTo("error de negocio");
    }

    @Test
    @DisplayName("handleValidation: retorna 400 con mensajes de campos")
    void handleValidation_retorna400ConMensajes() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "tipoEvento", "no debe estar en blanco");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleValidation(ex);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getExito()).isFalse();
        assertThat(response.getBody().getMensaje()).contains("Validación fallida");
        assertThat(response.getBody().getMensaje()).contains("no debe estar en blanco");
    }

    @Test
    @DisplayName("handleMissingHeader: retorna 400 con nombre del header")
    void handleMissingHeader_retorna400() {
        MissingRequestHeaderException ex = mock(MissingRequestHeaderException.class);
        when(ex.getHeaderName()).thenReturn("Authorization");

        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleMissingHeader(ex);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getExito()).isFalse();
        assertThat(response.getBody().getMensaje()).contains("Authorization");
    }

    @Test
    @DisplayName("handleGeneral: retorna 500 para excepciones no controladas")
    void handleGeneral_retorna500() throws Exception {
        Exception ex = new Exception("fallo inesperado");
        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleGeneral(ex);
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().getExito()).isFalse();
        assertThat(response.getBody().getMensaje()).isEqualTo("Error interno del servidor");
    }
}
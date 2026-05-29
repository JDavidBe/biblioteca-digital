package com.biblioteca.valoraciones;

import com.biblioteca.valoraciones.application.dto.ApiResponseDTO;
import com.biblioteca.valoraciones.infraestructure.entry_points.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler - Pruebas unitarias")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleRuntime: retorna 400 con mensaje")
    void handleRuntime_retorna400() {
        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleRuntime(new RuntimeException("error"));
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getExito()).isFalse();
        assertThat(response.getBody().getMensaje()).isEqualTo("error");
    }

    @Test
    @DisplayName("handleValidation: retorna 400 con mensajes de campos")
    void handleValidation_retorna400ConMensajes() {
        BindingResult br = mock(BindingResult.class);
        when(br.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "puntuacion", "debe estar entre 1 y 5")));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(br);

        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleValidation(ex);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMensaje()).contains("Validación fallida");
        assertThat(response.getBody().getMensaje()).contains("debe estar entre 1 y 5");
    }

    @Test
    @DisplayName("handleMissingHeader: retorna 400 con nombre del header")
    void handleMissingHeader_retorna400() {
        MissingRequestHeaderException ex = mock(MissingRequestHeaderException.class);
        when(ex.getHeaderName()).thenReturn("Authorization");

        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleMissingHeader(ex);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMensaje()).contains("Authorization");
    }

    @Test
    @DisplayName("handleGeneral: retorna 500")
    void handleGeneral_retorna500() throws Exception {
        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleGeneral(new Exception("boom"));
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().getMensaje()).isEqualTo("Error interno del servidor");
    }
}
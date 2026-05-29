package com.biblioteca.recursos;

import com.biblioteca.recursos.infraestructure.entry_points.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Recursos - Pruebas unitarias")
class GlobalExceptionHandlerRecursosTest {

    GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleRuntime: retorna 400 con mensaje")
    void handleRuntime_retorna400() {
        var response = handler.handleRuntime(new RuntimeException("error de prueba"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getExito()).isFalse();
        assertThat(response.getBody().getMensaje()).isEqualTo("error de prueba");
    }

    @Test
    @DisplayName("handleValidation: retorna 400 con mensajes de validación")
    void handleValidation_retorna400() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(br);
        when(br.getFieldErrors()).thenReturn(
                List.of(new FieldError("obj", "titulo", "El título es obligatorio"))
        );

        var response = handler.handleValidation(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMensaje()).contains("El título es obligatorio");
    }

    @Test
    @DisplayName("handleMissingHeader: retorna 400 con nombre del header")
    void handleMissingHeader_retorna400() throws Exception {
        MissingRequestHeaderException ex = mock(MissingRequestHeaderException.class);
        when(ex.getHeaderName()).thenReturn("Authorization");

        var response = handler.handleMissingHeader(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMensaje()).contains("Authorization");
    }

    @Test
    @DisplayName("handleGeneral: retorna 500")
    void handleGeneral_retorna500() throws Exception {
        var response = handler.handleGeneral(new Exception("error inesperado"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getExito()).isFalse();
    }
}
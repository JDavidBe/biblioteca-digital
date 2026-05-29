package com.biblioteca.notificaciones;

import com.biblioteca.notificaciones.infraestructure.entry_points.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler - Pruebas unitarias")
class GlobalExceptionHandlerNotificacionesTest {

    GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleRuntime: RuntimeException genérica retorna 400")
    void handleRuntime_retorna400() {
        var response = handler.handleRuntime(new RuntimeException("Error interno"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getExito()).isFalse();
    }

    @Test
    @DisplayName("handleRuntime: mensaje 'No existe' retorna 404")
    void handleRuntime_noExiste_retorna404() {
        var response = handler.handleRuntime(new RuntimeException("No existe notificación con id: 1"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getExito()).isFalse();
    }

    @Test
    @DisplayName("handleRuntime: mensaje de validación retorna 400")
    void handleRuntime_mensajeValidacion_retorna400() {
        var response = handler.handleRuntime(new RuntimeException("El mensaje es obligatorio"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMensaje()).contains("obligatorio");
    }

    @Test
    @DisplayName("handleGeneral: Exception inesperada retorna 500")
    void handleGeneral_retorna500() throws Exception {
        var response = handler.handleGeneral(new Exception("error inesperado"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getExito()).isFalse();
        assertThat(response.getBody().getMensaje()).contains("Error interno");
    }

    @Test
    @DisplayName("handleValidation: MethodArgumentNotValidException retorna 400 con mensaje")
    void handleValidation_retorna400() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(br);
        when(br.getFieldErrors()).thenReturn(
                List.of(new FieldError("obj", "mensaje", "requerido"))
        );

        var response = handler.handleValidation(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMensaje()).contains("requerido");
    }

    @Test
    @DisplayName("handleDeserializacion: body ilegible retorna 400")
    void handleDeserializacion_retorna400() {
        var inputMessage = new MockHttpInputMessage("bad json".getBytes(StandardCharsets.UTF_8));
        var ex = new HttpMessageNotReadableException("bad", inputMessage);

        ResponseEntity<?> response = handler.handleDeserializacion(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
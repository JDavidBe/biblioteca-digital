package com.biblioteca.descargas;

import com.biblioteca.descargas.application.dto.ApiResponseDTO;
import com.biblioteca.descargas.infraestructure.entry_points.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler Descargas - cobertura completa")
class GlobalExceptionHandlerDescargasTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleGeneral: Exception genérica retorna 500")
    void handleGeneral_retorna500() {
        ResponseEntity<ApiResponseDTO<Void>> response =
                handler.handleGeneral(new Exception("error inesperado"));

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody().getMensaje()).isEqualTo("Error interno del servidor");
        assertThat(response.getBody().getExito()).isFalse();
        assertThat(response.getBody().getDatos()).isNull();
    }

    @Test
    @DisplayName("handleRuntime: RuntimeException retorna 400 con mensaje")
    void handleRuntime_retorna400() {
        ResponseEntity<ApiResponseDTO<Void>> response =
                handler.handleRuntime(new RuntimeException("recurso no encontrado"));

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().getMensaje()).isEqualTo("recurso no encontrado");
        assertThat(response.getBody().getExito()).isFalse();
        assertThat(response.getBody().getDatos()).isNull();
    }

    @Test
    @DisplayName("handleRuntime: mensaje nulo no lanza excepción")
    void handleRuntime_mensajeNulo() {
        ResponseEntity<ApiResponseDTO<Void>> response =
                handler.handleRuntime(new RuntimeException((String) null));

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().getMensaje()).isNull();
    }

    @Test
    @DisplayName("handleValidation: errores de campo retorna 400 con mensaje concatenado")
    void handleValidation_retorna400ConCampos() throws Exception {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "objeto");
        bindingResult.addError(new FieldError("objeto", "campo1", "no debe ser nulo"));
        bindingResult.addError(new FieldError("objeto", "campo2", "tamaño inválido"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().getExito()).isFalse();
        assertThat(response.getBody().getMensaje())
                .contains("Validación fallida")
                .contains("no debe ser nulo")
                .contains("tamaño inválido");
        assertThat(response.getBody().getDatos()).isNull();
    }

    @Test
    @DisplayName("handleValidation: un solo error de campo")
    void handleValidation_unSoloError() throws Exception {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "objeto");
        bindingResult.addError(new FieldError("objeto", "nombre", "requerido"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleValidation(ex);

        assertThat(response.getBody().getMensaje())
                .isEqualTo("Validación fallida: requerido");
    }

    @Test
    @DisplayName("handleMissingHeader: header faltante retorna 400 con nombre del header")
    void handleMissingHeader_retorna400ConNombreHeader() {
        MissingRequestHeaderException ex =
                new MissingRequestHeaderException("Authorization", null);

        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleMissingHeader(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().getExito()).isFalse();
        assertThat(response.getBody().getMensaje())
                .isEqualTo("Header requerido: Authorization");
        assertThat(response.getBody().getDatos()).isNull();
    }

    @Test
    @DisplayName("handleMissingHeader: header X-User-Id retorna mensaje correcto")
    void handleMissingHeader_otroHeader() {
        MissingRequestHeaderException ex =
                new MissingRequestHeaderException("X-User-Id", null);

        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleMissingHeader(ex);

        assertThat(response.getBody().getMensaje())
                .isEqualTo("Header requerido: X-User-Id");
    }
}
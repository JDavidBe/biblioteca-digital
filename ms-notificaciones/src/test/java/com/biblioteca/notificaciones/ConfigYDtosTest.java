package com.biblioteca.notificaciones;

import com.biblioteca.notificaciones.application.config.SwaggerConfig;
import com.biblioteca.notificaciones.application.config.UseCaseConfig;
import com.biblioteca.notificaciones.application.dto.*;
import com.biblioteca.notificaciones.domain.model.gateway.EmailGateway;
import com.biblioteca.notificaciones.domain.model.gateway.NotificacionGateway;
import com.biblioteca.notificaciones.domain.model.gateway.SmsGateway;
import com.biblioteca.notificaciones.domain.usecase.NotificacionUseCase;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Configuraciones y DTOs - Pruebas unitarias")
class ConfigYDtosTest {

    @Test
    @DisplayName("SwaggerConfig: openAPI bean se crea correctamente")
    void swaggerConfig_creaOpenAPI() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI api = config.openAPI();
        assertThat(api).isNotNull();
        assertThat(api.getInfo().getTitle()).contains("ms-notificaciones");
        assertThat(api.getComponents().getSecuritySchemes()).containsKey("bearerAuth");
    }

    @Test
    @DisplayName("UseCaseConfig: notificacionUseCase bean se crea correctamente")
    void useCaseConfig_creaUseCase() {
        UseCaseConfig config = new UseCaseConfig();
        NotificacionUseCase uc = config.notificacionUseCase(
                mock(NotificacionGateway.class),
                mock(EmailGateway.class),
                mock(SmsGateway.class)
        );
        assertThat(uc).isNotNull();
    }

    @Test
    @DisplayName("NotificacionRequestDTO: setters y getters funcionan")
    void notificacionRequestDTO_settersGetters() {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setDestinatarioCorreo("user@test.com");
        dto.setDestinatarioTelefono("+573001234567");
        dto.setDestinatarioId(1L);
        dto.setCanal("EMAIL");
        dto.setTipo("GENERAL");
        dto.setAsunto("Asunto");
        dto.setMensaje("Mensaje");

        assertThat(dto.getDestinatarioCorreo()).isEqualTo("user@test.com");
        assertThat(dto.getDestinatarioTelefono()).isEqualTo("+573001234567");
        assertThat(dto.getDestinatarioId()).isEqualTo(1L);
        assertThat(dto.getCanal()).isEqualTo("EMAIL");
        assertThat(dto.getTipo()).isEqualTo("GENERAL");
        assertThat(dto.getAsunto()).isEqualTo("Asunto");
        assertThat(dto.getMensaje()).isEqualTo("Mensaje");
    }

    @Test
    @DisplayName("BienvenidaRequestDTO: setters y getters funcionan")
    void bienvenidaRequestDTO_settersGetters() {
        BienvenidaRequestDTO dto = new BienvenidaRequestDTO();
        dto.setCorreo("user@test.com");
        dto.setTelefono("+573001234567");
        dto.setUsuarioId(5L);
        dto.setNombre("Ana");

        assertThat(dto.getCorreo()).isEqualTo("user@test.com");
        assertThat(dto.getTelefono()).isEqualTo("+573001234567");
        assertThat(dto.getUsuarioId()).isEqualTo(5L);
        assertThat(dto.getNombre()).isEqualTo("Ana");
    }

    @Test
    @DisplayName("RecursoNotificacionRequestDTO: setters y getters funcionan")
    void recursoNotificacionRequestDTO_settersGetters() {
        RecursoNotificacionRequestDTO dto = new RecursoNotificacionRequestDTO();
        dto.setCorreo("user@test.com");
        dto.setTelefono("+573001234567");
        dto.setUsuarioId(3L);
        dto.setTituloRecurso("Álgebra");

        assertThat(dto.getCorreo()).isEqualTo("user@test.com");
        assertThat(dto.getTelefono()).isEqualTo("+573001234567");
        assertThat(dto.getUsuarioId()).isEqualTo(3L);
        assertThat(dto.getTituloRecurso()).isEqualTo("Álgebra");
    }

    @Test
    @DisplayName("NotificacionResponseDTO: constructor y getters funcionan")
    void notificacionResponseDTO_constructorGetters() {
        LocalDateTime ahora = LocalDateTime.now();
        NotificacionResponseDTO dto = new NotificacionResponseDTO(
                1L, "user@test.com", "+573001234567", 5L,
                "EMAIL", "GENERAL", "Asunto", "Mensaje",
                true, false, ahora, ahora
        );

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDestinatarioCorreo()).isEqualTo("user@test.com");
        assertThat(dto.getDestinatarioTelefono()).isEqualTo("+573001234567");
        assertThat(dto.getDestinatarioId()).isEqualTo(5L);
        assertThat(dto.getCanal()).isEqualTo("EMAIL");
        assertThat(dto.getTipo()).isEqualTo("GENERAL");
        assertThat(dto.getAsunto()).isEqualTo("Asunto");
        assertThat(dto.getMensaje()).isEqualTo("Mensaje");
        assertThat(dto.getEnviada()).isTrue();
        assertThat(dto.getEnviadaSms()).isFalse();
        assertThat(dto.getCreadaEn()).isEqualTo(ahora);
        assertThat(dto.getEnviadaEn()).isEqualTo(ahora);
    }

    @Test
    @DisplayName("ApiResponseDTO: constructor y getters funcionan")
    void apiResponseDTO_constructorGetters() {
        ApiResponseDTO<String> dto = new ApiResponseDTO<>(true, "ok", "datos");
        assertThat(dto.getExito()).isTrue();
        assertThat(dto.getMensaje()).isEqualTo("ok");
        assertThat(dto.getDatos()).isEqualTo("datos");
    }

    @Test
    @DisplayName("ApiResponseDTO: datos null funciona")
    void apiResponseDTO_datosNull() {
        ApiResponseDTO<Void> dto = new ApiResponseDTO<>(false, "error", null);
        assertThat(dto.getExito()).isFalse();
        assertThat(dto.getDatos()).isNull();
    }
}
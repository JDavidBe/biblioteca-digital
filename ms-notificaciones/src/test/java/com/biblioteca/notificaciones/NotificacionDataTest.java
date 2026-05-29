package com.biblioteca.notificaciones;

import com.biblioteca.notificaciones.infraestructure.driver_adapters.jpa_repository.NotificacionData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NotificacionData - Pruebas unitarias de entidad")
class NotificacionDataTest {

    @Test
    @DisplayName("Valores por defecto: canal EMAIL, enviada false, enviadaSms false")
    void valoresPorDefecto() {
        NotificacionData data = new NotificacionData();
        assertThat(data.getCanal()).isEqualTo("EMAIL");
        assertThat(data.getEnviada()).isFalse();
        assertThat(data.getEnviadaSms()).isFalse();
    }

    @Test
    @DisplayName("Setters y getters funcionan correctamente")
    void settersYGetters() {
        LocalDateTime ahora = LocalDateTime.now();
        NotificacionData data = new NotificacionData();
        data.setId(1L);
        data.setDestinatarioCorreo("user@test.com");
        data.setDestinatarioTelefono("+573001234567");
        data.setDestinatarioId(5L);
        data.setCanal("AMBOS");
        data.setTipo("GENERAL");
        data.setAsunto("Asunto test");
        data.setMensaje("Mensaje test");
        data.setEnviada(true);
        data.setEnviadaSms(true);
        data.setCreadaEn(ahora);
        data.setEnviadaEn(ahora);

        assertThat(data.getId()).isEqualTo(1L);
        assertThat(data.getDestinatarioCorreo()).isEqualTo("user@test.com");
        assertThat(data.getDestinatarioTelefono()).isEqualTo("+573001234567");
        assertThat(data.getDestinatarioId()).isEqualTo(5L);
        assertThat(data.getCanal()).isEqualTo("AMBOS");
        assertThat(data.getTipo()).isEqualTo("GENERAL");
        assertThat(data.getAsunto()).isEqualTo("Asunto test");
        assertThat(data.getMensaje()).isEqualTo("Mensaje test");
        assertThat(data.getEnviada()).isTrue();
        assertThat(data.getEnviadaSms()).isTrue();
        assertThat(data.getCreadaEn()).isEqualTo(ahora);
        assertThat(data.getEnviadaEn()).isEqualTo(ahora);
    }

    @Test
    @DisplayName("Campos opcionales pueden ser null")
    void camposOpcionales_puedanSerNull() {
        NotificacionData data = new NotificacionData();
        assertThat(data.getId()).isNull();
        assertThat(data.getDestinatarioCorreo()).isNull();
        assertThat(data.getDestinatarioTelefono()).isNull();
        assertThat(data.getAsunto()).isNull();
        assertThat(data.getCreadaEn()).isNull();
        assertThat(data.getEnviadaEn()).isNull();
    }
}
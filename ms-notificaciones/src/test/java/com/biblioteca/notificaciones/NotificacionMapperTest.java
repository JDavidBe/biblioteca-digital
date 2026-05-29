package com.biblioteca.notificaciones;

import com.biblioteca.notificaciones.domain.model.Notificacion;
import com.biblioteca.notificaciones.infraestructure.driver_adapters.jpa_repository.NotificacionData;
import com.biblioteca.notificaciones.infraestructure.driver_adapters.jpa_repository.NotificacionMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NotificacionMapper - Pruebas unitarias")
class NotificacionMapperTest {

    @Test
    @DisplayName("toDomain: mapea todos los campos incluyendo SMS")
    void toDomain_mapeoCompleto() {
        NotificacionData data = new NotificacionData();
        data.setId(1L);
        data.setDestinatarioCorreo("user@test.com");
        data.setDestinatarioTelefono("+573001234567");
        data.setDestinatarioId(5L);
        data.setCanal("AMBOS");
        data.setTipo("BIENVENIDA");
        data.setAsunto("Asunto");
        data.setMensaje("Mensaje");
        data.setEnviada(true);
        data.setEnviadaSms(true);
        LocalDateTime ahora = LocalDateTime.now();
        data.setCreadaEn(ahora);
        data.setEnviadaEn(ahora);

        Notificacion n = NotificacionMapper.toDomain(data);

        assertThat(n.getId()).isEqualTo(1L);
        assertThat(n.getDestinatarioCorreo()).isEqualTo("user@test.com");
        assertThat(n.getDestinatarioTelefono()).isEqualTo("+573001234567");
        assertThat(n.getCanal()).isEqualTo("AMBOS");
        assertThat(n.getDestinatarioId()).isEqualTo(5L);
        assertThat(n.getTipo()).isEqualTo("BIENVENIDA");
        assertThat(n.getEnviada()).isTrue();
        assertThat(n.getEnviadaSms()).isTrue();
        assertThat(n.getCreadaEn()).isEqualTo(ahora);
        assertThat(n.getEnviadaEn()).isEqualTo(ahora);
    }

    @Test
    @DisplayName("toDomain: retorna null si data es null")
    void toDomain_null_retornaNull() {
        assertThat(NotificacionMapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toData: mapea todos los campos correctamente")
    void toData_mapeoCompleto() {
        LocalDateTime ahora = LocalDateTime.now();
        Notificacion n = new Notificacion();
        n.setId(1L);
        n.setDestinatarioCorreo("user@test.com");
        n.setDestinatarioTelefono("+573001234567");
        n.setDestinatarioId(5L);
        n.setCanal("AMBOS");
        n.setTipo("GENERAL");
        n.setAsunto("Asunto");
        n.setMensaje("Mensaje");
        n.setEnviada(false);
        n.setEnviadaSms(false);
        n.setCreadaEn(ahora);

        NotificacionData data = NotificacionMapper.toData(n);

        assertThat(data.getId()).isEqualTo(1L);
        assertThat(data.getDestinatarioCorreo()).isEqualTo("user@test.com");
        assertThat(data.getDestinatarioTelefono()).isEqualTo("+573001234567");
        assertThat(data.getCanal()).isEqualTo("AMBOS");
        assertThat(data.getTipo()).isEqualTo("GENERAL");
        assertThat(data.getEnviada()).isFalse();
        assertThat(data.getEnviadaSms()).isFalse();
        assertThat(data.getCreadaEn()).isEqualTo(ahora);
    }

    @Test
    @DisplayName("toData: tipo null se mapea como GENERAL")
    void toData_tipoNull_asignaGeneral() {
        Notificacion n = new Notificacion();
        n.setDestinatarioCorreo("user@test.com");
        n.setMensaje("M");
        n.setTipo(null);
        assertThat(NotificacionMapper.toData(n).getTipo()).isEqualTo("GENERAL");
    }

    @Test
    @DisplayName("toData: canal null se mapea como EMAIL")
    void toData_canalNull_asignaEmail() {
        Notificacion n = new Notificacion();
        n.setDestinatarioCorreo("user@test.com");
        n.setMensaje("M");
        n.setCanal(null);
        assertThat(NotificacionMapper.toData(n).getCanal()).isEqualTo("EMAIL");
    }

    @Test
    @DisplayName("toData: enviada/enviadaSms null se mapean como false")
    void toData_enviadaNull_asignaFalse() {
        Notificacion n = new Notificacion();
        n.setDestinatarioCorreo("user@test.com");
        n.setMensaje("M");
        n.setEnviada(null);
        n.setEnviadaSms(null);
        NotificacionData data = NotificacionMapper.toData(n);
        assertThat(data.getEnviada()).isFalse();
        assertThat(data.getEnviadaSms()).isFalse();
    }

    @Test
    @DisplayName("toData: retorna null si domain es null")
    void toData_null_retornaNull() {
        assertThat(NotificacionMapper.toData(null)).isNull();
    }

    @Test
    @DisplayName("round-trip: toDomain → toData conserva datos incluyendo SMS")
    void roundTrip_conservaDatos() {
        Notificacion original = new Notificacion();
        original.setId(1L);
        original.setDestinatarioCorreo("user@test.com");
        original.setDestinatarioTelefono("+573001234567");
        original.setDestinatarioId(3L);
        original.setCanal("AMBOS");
        original.setTipo("NUEVA_DESCARGA");
        original.setAsunto("Asunto");
        original.setMensaje("Cuerpo");
        original.setEnviada(true);
        original.setEnviadaSms(true);
        original.setCreadaEn(LocalDateTime.now());
        original.setEnviadaEn(LocalDateTime.now());

        NotificacionData data = NotificacionMapper.toData(original);
        Notificacion recuperado = NotificacionMapper.toDomain(data);

        assertThat(recuperado.getDestinatarioCorreo()).isEqualTo(original.getDestinatarioCorreo());
        assertThat(recuperado.getDestinatarioTelefono()).isEqualTo(original.getDestinatarioTelefono());
        assertThat(recuperado.getCanal()).isEqualTo(original.getCanal());
        assertThat(recuperado.getTipo()).isEqualTo(original.getTipo());
        assertThat(recuperado.getEnviada()).isEqualTo(original.getEnviada());
        assertThat(recuperado.getEnviadaSms()).isEqualTo(original.getEnviadaSms());
    }
}

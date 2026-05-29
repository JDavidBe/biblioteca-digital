package com.biblioteca.notificaciones;

import com.biblioteca.notificaciones.domain.model.Notificacion;
import com.biblioteca.notificaciones.domain.model.gateway.EmailGateway;
import com.biblioteca.notificaciones.domain.model.gateway.NotificacionGateway;
import com.biblioteca.notificaciones.domain.model.gateway.SmsGateway;
import com.biblioteca.notificaciones.domain.usecase.NotificacionUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacionUseCase - Pruebas unitarias")
class NotificacionUseCaseTest {

    @Mock NotificacionGateway notificacionGateway;
    @Mock EmailGateway emailGateway;
    @Mock SmsGateway smsGateway;

    NotificacionUseCase useCase;

    private Notificacion notificacionGuardada;

    @BeforeEach
    void setUp() {
        useCase = new NotificacionUseCase(notificacionGateway, emailGateway, smsGateway);
        notificacionGuardada = new Notificacion();
        notificacionGuardada.setId(1L);
        notificacionGuardada.setDestinatarioCorreo("user@test.com");
        notificacionGuardada.setDestinatarioId(5L);
        notificacionGuardada.setCanal("EMAIL");
        notificacionGuardada.setTipo("GENERAL");
        notificacionGuardada.setAsunto("Asunto");
        notificacionGuardada.setMensaje("Mensaje");
        notificacionGuardada.setEnviada(false);
        notificacionGuardada.setEnviadaSms(false);
        notificacionGuardada.setCreadaEn(LocalDateTime.now());
    }

    // ── enviarNotificacion: EMAIL ──

    @Test
    @DisplayName("enviarNotificacion: guarda y envía email correctamente")
    void enviar_email_guardaYEnvia() {
        when(notificacionGateway.guardar(any())).thenReturn(notificacionGuardada);
        doNothing().when(emailGateway).enviar(anyString(), anyString(), anyString());
        doNothing().when(notificacionGateway).marcarComoEnviada(1L);

        Notificacion n = new Notificacion();
        n.setDestinatarioCorreo("user@test.com");
        n.setAsunto("Asunto"); n.setMensaje("Mensaje");

        Notificacion resultado = useCase.enviarNotificacion(n);

        assertThat(resultado).isNotNull();
        verify(emailGateway).enviar("user@test.com", "Asunto", "Mensaje");
        verify(notificacionGateway).marcarComoEnviada(1L);
        verify(smsGateway, never()).enviar(anyString(), anyString());
    }

    @Test
    @DisplayName("enviarNotificacion: asigna tipo GENERAL si no se especifica")
    void enviar_sinTipo_asignaGeneral() {
        when(notificacionGateway.guardar(any())).thenReturn(notificacionGuardada);
        doNothing().when(emailGateway).enviar(anyString(), anyString(), anyString());
        doNothing().when(notificacionGateway).marcarComoEnviada(anyLong());

        Notificacion n = new Notificacion();
        n.setDestinatarioCorreo("user@test.com");
        n.setAsunto("Asunto"); n.setMensaje("Mensaje"); n.setTipo(null);

        useCase.enviarNotificacion(n);
        assertThat(n.getTipo()).isEqualTo("GENERAL");
    }

    @Test
    @DisplayName("enviarNotificacion: guarda aunque falle el email")
    void enviar_emailFalla_guardaDeTodasFormas() {
        when(notificacionGateway.guardar(any())).thenReturn(notificacionGuardada);
        doThrow(new RuntimeException("SMTP caído")).when(emailGateway).enviar(anyString(), anyString(), anyString());

        Notificacion n = new Notificacion();
        n.setDestinatarioCorreo("user@test.com");
        n.setAsunto("Asunto"); n.setMensaje("Mensaje");

        Notificacion resultado = useCase.enviarNotificacion(n);
        assertThat(resultado).isNotNull();
        verify(notificacionGateway).guardar(any());
        verify(notificacionGateway, never()).marcarComoEnviada(anyLong());
    }

    // ── enviarNotificacion: SMS ──

    @Test
    @DisplayName("enviarNotificacion: envía SMS correctamente")
    void enviar_sms_guardaYEnvia() {
        Notificacion smsGuardada = new Notificacion();
        smsGuardada.setId(2L); smsGuardada.setDestinatarioTelefono("+573001234567");
        smsGuardada.setCanal("SMS"); smsGuardada.setMensaje("Hola"); smsGuardada.setEnviada(false); smsGuardada.setEnviadaSms(false);
        smsGuardada.setCreadaEn(LocalDateTime.now());

        when(notificacionGateway.guardar(any())).thenReturn(smsGuardada);
        doNothing().when(smsGateway).enviar(anyString(), anyString());
        doNothing().when(notificacionGateway).marcarSmsComotEnviado(2L);

        Notificacion n = new Notificacion();
        n.setDestinatarioTelefono("+573001234567");
        n.setCanal("SMS"); n.setMensaje("Hola");

        Notificacion resultado = useCase.enviarNotificacion(n);

        assertThat(resultado).isNotNull();
        verify(smsGateway).enviar("+573001234567", "Hola");
        verify(notificacionGateway).marcarSmsComotEnviado(2L);
        verify(emailGateway, never()).enviar(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("enviarNotificacion: canal AMBOS envía email y SMS")
    void enviar_ambosCanales_enviaEmailYSms() {
        Notificacion ambosGuardada = new Notificacion();
        ambosGuardada.setId(3L); ambosGuardada.setDestinatarioCorreo("u@t.com");
        ambosGuardada.setDestinatarioTelefono("+573001234567");
        ambosGuardada.setCanal("AMBOS"); ambosGuardada.setTipo("GENERAL");
        ambosGuardada.setAsunto("A"); ambosGuardada.setMensaje("M");
        ambosGuardada.setEnviada(false); ambosGuardada.setEnviadaSms(false);
        ambosGuardada.setCreadaEn(LocalDateTime.now());

        when(notificacionGateway.guardar(any())).thenReturn(ambosGuardada);
        doNothing().when(emailGateway).enviar(anyString(), anyString(), anyString());
        doNothing().when(smsGateway).enviar(anyString(), anyString());
        doNothing().when(notificacionGateway).marcarComoEnviada(anyLong());
        doNothing().when(notificacionGateway).marcarSmsComotEnviado(anyLong());

        Notificacion n = new Notificacion();
        n.setDestinatarioCorreo("u@t.com"); n.setDestinatarioTelefono("+573001234567");
        n.setCanal("AMBOS"); n.setAsunto("A"); n.setMensaje("M");

        useCase.enviarNotificacion(n);

        verify(emailGateway).enviar(anyString(), anyString(), anyString());
        verify(smsGateway).enviar(anyString(), anyString());
    }

    @Test
    @DisplayName("enviarNotificacion: lanza excepción si correo vacío en canal EMAIL")
    void enviar_correoVacio_lanzaExcepcion() {
        Notificacion n = new Notificacion();
        n.setCanal("EMAIL"); n.setDestinatarioCorreo(""); n.setAsunto("A"); n.setMensaje("M");

        assertThatThrownBy(() -> useCase.enviarNotificacion(n))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("correo");
    }

    @Test
    @DisplayName("enviarNotificacion: lanza excepción si teléfono vacío en canal SMS")
    void enviar_telefonoVacio_canalSms_lanzaExcepcion() {
        Notificacion n = new Notificacion();
        n.setCanal("SMS"); n.setDestinatarioTelefono(""); n.setMensaje("M");

        assertThatThrownBy(() -> useCase.enviarNotificacion(n))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("teléfono");
    }

    @Test
    @DisplayName("enviarNotificacion: lanza excepción si mensaje vacío")
    void enviar_mensajeVacio_lanzaExcepcion() {
        Notificacion n = new Notificacion();
        n.setDestinatarioCorreo("user@test.com"); n.setAsunto("A"); n.setMensaje("");

        assertThatThrownBy(() -> useCase.enviarNotificacion(n))
                .isInstanceOf(RuntimeException.class).hasMessageContaining("mensaje");
    }

    // ── enviarBienvenida ──

    @Test
    @DisplayName("enviarBienvenida: crea notificación tipo BIENVENIDA")
    void bienvenida_datosValidos_creaTipoBienvenida() {
        when(notificacionGateway.guardar(any())).thenAnswer(inv -> {
            Notificacion saved = inv.getArgument(0); saved.setId(1L); return saved;
        });
        doNothing().when(emailGateway).enviar(anyString(), anyString(), anyString());
        doNothing().when(notificacionGateway).marcarComoEnviada(anyLong());

        Notificacion resultado = useCase.enviarBienvenida("user@test.com", null, 1L, "Ana");

        assertThat(resultado.getTipo()).isEqualTo("BIENVENIDA");
        assertThat(resultado.getMensaje()).contains("Ana");
    }

    @Test
    @DisplayName("enviarBienvenida: lanza excepción si no hay correo ni teléfono")
    void bienvenida_sinContacto_lanzaExcepcion() {
        assertThatThrownBy(() -> useCase.enviarBienvenida("", null, 1L, "Ana"))
                .isInstanceOf(RuntimeException.class).hasMessageContaining("correo");
    }

    // ── notificarNuevoRecurso ──

    @Test
    @DisplayName("notificarNuevoRecurso: crea notificación tipo NUEVO_RECURSO")
    void nuevoRecurso_datosValidos_creaTipo() {
        when(notificacionGateway.guardar(any())).thenAnswer(inv -> {
            Notificacion saved = inv.getArgument(0); saved.setId(2L); return saved;
        });
        doNothing().when(emailGateway).enviar(anyString(), anyString(), anyString());
        doNothing().when(notificacionGateway).marcarComoEnviada(anyLong());

        Notificacion resultado = useCase.notificarNuevoRecurso("user@test.com", null, 1L, "Álgebra");

        assertThat(resultado.getTipo()).isEqualTo("NUEVO_RECURSO");
        assertThat(resultado.getMensaje()).contains("Álgebra");
    }

    @Test
    @DisplayName("notificarNuevoRecurso: lanza excepción si título vacío")
    void nuevoRecurso_tituloVacio_lanzaExcepcion() {
        assertThatThrownBy(() -> useCase.notificarNuevoRecurso("user@test.com", null, 1L, ""))
                .isInstanceOf(RuntimeException.class).hasMessageContaining("título");
    }

    // ── notificarDescarga ──

    @Test
    @DisplayName("notificarDescarga: crea notificación tipo NUEVA_DESCARGA")
    void descarga_datosValidos_creaTipo() {
        when(notificacionGateway.guardar(any())).thenAnswer(inv -> {
            Notificacion saved = inv.getArgument(0); saved.setId(3L); return saved;
        });
        doNothing().when(emailGateway).enviar(anyString(), anyString(), anyString());
        doNothing().when(notificacionGateway).marcarComoEnviada(anyLong());

        Notificacion resultado = useCase.notificarDescarga("user@test.com", null, 1L, "Física");

        assertThat(resultado.getTipo()).isEqualTo("NUEVA_DESCARGA");
        assertThat(resultado.getMensaje()).contains("Física");
    }

    // ── listar / obtener ──

    @Test
    @DisplayName("listarPorDestinatario: retorna lista del gateway")
    void listarPorDestinatario_retornaLista() {
        when(notificacionGateway.listarPorDestinatario(5L)).thenReturn(List.of(notificacionGuardada));
        assertThat(useCase.listarPorDestinatario(5L)).hasSize(1);
    }

    @Test
    @DisplayName("listarPorDestinatario: lanza excepción si id null")
    void listarPorDestinatario_idNull_lanzaExcepcion() {
        assertThatThrownBy(() -> useCase.listarPorDestinatario(null)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("listarPendientes: delega en gateway")
    void listarPendientes_delegaEnGateway() {
        when(notificacionGateway.listarPendientes()).thenReturn(List.of(notificacionGuardada));
        assertThat(useCase.listarPendientes()).hasSize(1);
    }

    @Test
    @DisplayName("listarTodas: delega en gateway")
    void listarTodas_delegaEnGateway() {
        when(notificacionGateway.listarTodas()).thenReturn(List.of(notificacionGuardada));
        assertThat(useCase.listarTodas()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId: retorna notificación existente")
    void obtenerPorId_existente_retorna() {
        when(notificacionGateway.buscarPorId(1L)).thenReturn(notificacionGuardada);
        assertThat(useCase.obtenerPorId(1L)).isEqualTo(notificacionGuardada);
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepción si no existe")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(notificacionGateway.buscarPorId(99L)).thenReturn(null);
        assertThatThrownBy(() -> useCase.obtenerPorId(99L))
                .isInstanceOf(RuntimeException.class).hasMessageContaining("99");
    }
}

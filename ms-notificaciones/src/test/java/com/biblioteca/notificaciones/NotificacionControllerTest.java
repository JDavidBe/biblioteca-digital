package com.biblioteca.notificaciones;

import com.biblioteca.notificaciones.application.dto.BienvenidaRequestDTO;
import com.biblioteca.notificaciones.application.dto.NotificacionRequestDTO;
import com.biblioteca.notificaciones.application.dto.RecursoNotificacionRequestDTO;
import com.biblioteca.notificaciones.domain.model.Notificacion;
import com.biblioteca.notificaciones.domain.usecase.NotificacionUseCase;
import com.biblioteca.notificaciones.infraestructure.entry_points.GlobalExceptionHandler;
import com.biblioteca.notificaciones.infraestructure.entry_points.NotificacionController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificacionController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@DisplayName("NotificacionController - Pruebas de integración web")
class NotificacionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    NotificacionUseCase notificacionUseCase;

    private Notificacion sampleEmail() {
        Notificacion n = new Notificacion();
        n.setId(1L);
        n.setDestinatarioCorreo("user@test.com");
        n.setDestinatarioId(5L);
        n.setCanal("EMAIL");
        n.setTipo("GENERAL");
        n.setAsunto("Asunto test");
        n.setMensaje("Mensaje test");
        n.setEnviada(true);
        n.setEnviadaSms(false);
        n.setCreadaEn(LocalDateTime.now());
        n.setEnviadaEn(LocalDateTime.now());
        return n;
    }

    private Notificacion sampleSms() {
        Notificacion n = new Notificacion();
        n.setId(2L);
        n.setDestinatarioTelefono("+573001234567");
        n.setDestinatarioId(5L);
        n.setCanal("SMS");
        n.setTipo("GENERAL");
        n.setMensaje("Mensaje SMS");
        n.setEnviada(false);
        n.setEnviadaSms(true);
        n.setCreadaEn(LocalDateTime.now());
        n.setEnviadaEn(LocalDateTime.now());
        return n;
    }

    @Test
    @DisplayName("POST /api/notificaciones - email retorna 201")
    void enviar_email_retorna201() throws Exception {
        when(notificacionUseCase.enviarNotificacion(any())).thenReturn(sampleEmail());

        NotificacionRequestDTO req = new NotificacionRequestDTO();
        req.setDestinatarioCorreo("user@test.com");
        req.setAsunto("Asunto");
        req.setMensaje("Mensaje");

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.destinatarioCorreo").value("user@test.com"));
    }

    @Test
    @DisplayName("POST /api/notificaciones - SMS retorna 201")
    void enviar_sms_retorna201() throws Exception {
        when(notificacionUseCase.enviarNotificacion(any())).thenReturn(sampleSms());

        NotificacionRequestDTO req = new NotificacionRequestDTO();
        req.setDestinatarioTelefono("+573001234567");
        req.setCanal("SMS");
        req.setMensaje("Hola por SMS");

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.canal").value("SMS"));
    }

    @Test
    @DisplayName("POST /api/notificaciones - correo inválido retorna 400")
    void enviar_correoInvalido_retorna400() throws Exception {
        NotificacionRequestDTO req = new NotificacionRequestDTO();
        req.setDestinatarioCorreo("no-email");
        req.setAsunto("Asunto");
        req.setMensaje("Mensaje");

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("POST /api/notificaciones/bienvenida - retorna 201")
    void bienvenida_datosValidos_retorna201() throws Exception {
        when(notificacionUseCase.enviarBienvenida(anyString(), isNull(), anyLong(), anyString()))
                .thenReturn(sampleEmail());

        BienvenidaRequestDTO req = new BienvenidaRequestDTO();
        req.setCorreo("user@test.com");
        req.setUsuarioId(5L);
        req.setNombre("Ana");

        mockMvc.perform(post("/api/notificaciones/bienvenida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("POST /api/notificaciones/bienvenida - sin nombre retorna 400")
    void bienvenida_sinNombre_retorna400() throws Exception {
        BienvenidaRequestDTO req = new BienvenidaRequestDTO();
        req.setCorreo("user@test.com");
        req.setNombre("");

        mockMvc.perform(post("/api/notificaciones/bienvenida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/notificaciones/nuevo-recurso - retorna 201")
    void nuevoRecurso_retorna201() throws Exception {
        when(notificacionUseCase.notificarNuevoRecurso(anyString(), isNull(), anyLong(), anyString()))
                .thenReturn(sampleEmail());

        RecursoNotificacionRequestDTO req = new RecursoNotificacionRequestDTO();
        req.setCorreo("user@test.com");
        req.setUsuarioId(5L);
        req.setTituloRecurso("Álgebra");

        mockMvc.perform(post("/api/notificaciones/nuevo-recurso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("POST /api/notificaciones/descarga - retorna 201")
    void descarga_retorna201() throws Exception {
        when(notificacionUseCase.notificarDescarga(anyString(), isNull(), anyLong(), anyString()))
                .thenReturn(sampleEmail());

        RecursoNotificacionRequestDTO req = new RecursoNotificacionRequestDTO();
        req.setCorreo("user@test.com");
        req.setUsuarioId(5L);
        req.setTituloRecurso("Física");

        mockMvc.perform(post("/api/notificaciones/descarga")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/notificaciones - retorna lista")
    void listarTodas_retorna200() throws Exception {
        when(notificacionUseCase.listarTodas()).thenReturn(List.of(sampleEmail()));

        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos").isArray())
                .andExpect(jsonPath("$.datos[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/notificaciones - lista vacía retorna 200")
    void listarTodas_vacio_retorna200() throws Exception {
        when(notificacionUseCase.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos").isArray());
    }

    @Test
    @DisplayName("GET /api/notificaciones/pendientes - retorna lista")
    void pendientes_retorna200() throws Exception {
        when(notificacionUseCase.listarPendientes()).thenReturn(List.of(sampleEmail()));

        mockMvc.perform(get("/api/notificaciones/pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos").isArray());
    }

    @Test
    @DisplayName("GET /api/notificaciones/pendientes - lista vacía retorna 200")
    void pendientes_vacio_retorna200() throws Exception {
        when(notificacionUseCase.listarPendientes()).thenReturn(List.of());

        mockMvc.perform(get("/api/notificaciones/pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos").isArray());
    }

    @Test
    @DisplayName("GET /api/notificaciones/usuario/{id} - retorna lista del usuario")
    void porUsuario_retorna200() throws Exception {
        when(notificacionUseCase.listarPorDestinatario(5L)).thenReturn(List.of(sampleEmail()));

        mockMvc.perform(get("/api/notificaciones/usuario/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos").isArray());
    }

    @Test
    @DisplayName("GET /api/notificaciones/{id} - retorna 200 si existe")
    void obtener_existente_retorna200() throws Exception {
        when(notificacionUseCase.obtenerPorId(1L)).thenReturn(sampleEmail());

        mockMvc.perform(get("/api/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.id").value(1));
    }

    @Test
    @DisplayName("GET /api/notificaciones/{id} - retorna 404 si no existe")
    void obtener_noExiste_retorna404() throws Exception {
        when(notificacionUseCase.obtenerPorId(99L))
                .thenThrow(new RuntimeException("No existe notificación con id: 99"));

        mockMvc.perform(get("/api/notificaciones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exito").value(false));
    }
}
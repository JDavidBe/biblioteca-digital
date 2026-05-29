package com.biblioteca.reportes;

import com.biblioteca.reportes.application.config.SecurityConfig;
import com.biblioteca.reportes.application.dto.ActividadRequestDTO;
import com.biblioteca.reportes.domain.model.RegistroActividad;
import com.biblioteca.reportes.domain.model.ResumenGeneral;
import com.biblioteca.reportes.domain.usecase.ReporteUseCase;
import com.biblioteca.reportes.infraestructure.entry_points.GlobalExceptionHandler;
import com.biblioteca.reportes.infraestructure.entry_points.ReporteController;
import com.biblioteca.reportes.infraestructure.security.JwtAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.hamcrest.Matchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ReporteController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthFilter.class}
        )
)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@DisplayName("ReporteController - Pruebas de integración web")
class ReporteControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean ReporteUseCase reporteUseCase;

    private RegistroActividad actividadSample() {
        RegistroActividad a = new RegistroActividad();
        a.setId(1L);
        a.setTipoEvento("DESCARGA");
        a.setEntidadId(10L);
        a.setEntidadTipo("RECURSO");
        a.setUsuarioId(5L);
        a.setUsuarioCorreo("user@test.com");
        a.setDetalle("Descarga de recurso");
        a.setOcurridoEn(LocalDateTime.now());
        return a;
    }

    @Test
    @DisplayName("POST /actividad - registra actividad y retorna 201")
    void registrar_datosValidos_retorna201() throws Exception {
        when(reporteUseCase.registrarEvento(any())).thenReturn(actividadSample());
        ActividadRequestDTO req = new ActividadRequestDTO();
        req.setTipoEvento("DESCARGA");

        mockMvc.perform(post("/api/reportes/actividad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.tipoEvento").value("DESCARGA"));
    }

    @Test
    @DisplayName("POST /actividad - retorna 400 si tipoEvento está en blanco")
    void registrar_tipoEventoBlanco_retorna400() throws Exception {
        ActividadRequestDTO req = new ActividadRequestDTO();
        req.setTipoEvento("");

        mockMvc.perform(post("/api/reportes/actividad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /actividad - retorna 400 si tipoEvento es null")
    void registrar_tipoEventoNull_retorna400() throws Exception {
        ActividadRequestDTO req = new ActividadRequestDTO();

        mockMvc.perform(post("/api/reportes/actividad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("POST /actividad - propaga todos los campos al useCase")
    void registrar_camposCompletos_propagaCampos() throws Exception {
        when(reporteUseCase.registrarEvento(any())).thenReturn(actividadSample());
        ActividadRequestDTO req = new ActividadRequestDTO();
        req.setTipoEvento("LOGIN");
        req.setEntidadId(3L);
        req.setEntidadTipo("USUARIO");
        req.setUsuarioId(7L);
        req.setUsuarioCorreo("x@y.com");
        req.setDetalle("Sesión iniciada");

        mockMvc.perform(post("/api/reportes/actividad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));

        verify(reporteUseCase).registrarEvento(any());
    }

    @Test
    @DisplayName("GET /resumen - retorna resumen general con todos los campos")
    void resumen_retornaResumen() throws Exception {
        ResumenGeneral r = new ResumenGeneral();
        r.setTotalEventos(100L);
        r.setEventosHoy(5L);
        r.setEventosSemana(30L);
        r.setEventosMes(80L);
        r.setEventoMasFrecuente("DESCARGA");
        when(reporteUseCase.resumenGeneral()).thenReturn(r);

        mockMvc.perform(get("/api/reportes/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.totalEventos").value(100))
                .andExpect(jsonPath("$.datos.eventosHoy").value(5))
                .andExpect(jsonPath("$.datos.eventosSemana").value(30))
                .andExpect(jsonPath("$.datos.eventosMes").value(80))
                .andExpect(jsonPath("$.datos.eventoMasFrecuente").value("DESCARGA"));
    }

    @Test
    @DisplayName("GET /actividad/recientes - retorna lista con límite por defecto 50")
    void recientes_sinParametro_usaLimite50() throws Exception {
        when(reporteUseCase.actividadReciente(50)).thenReturn(List.of(actividadSample()));

        mockMvc.perform(get("/api/reportes/actividad/recientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    @DisplayName("GET /actividad/recientes?limite=10 - usa el límite indicado")
    void recientes_conParametroLimite_usaLimiteIndicado() throws Exception {
        when(reporteUseCase.actividadReciente(10)).thenReturn(List.of(actividadSample()));

        mockMvc.perform(get("/api/reportes/actividad/recientes").param("limite", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));

        verify(reporteUseCase).actividadReciente(10);
    }

    @Test
    @DisplayName("GET /actividad/tipo/{tipo} - retorna actividad filtrada por tipo")
    void porTipo_retornaLista() throws Exception {
        when(reporteUseCase.actividadPorTipo("DESCARGA")).thenReturn(List.of(actividadSample()));

        mockMvc.perform(get("/api/reportes/actividad/tipo/DESCARGA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos[0].tipoEvento").value("DESCARGA"));
    }

    @Test
    @DisplayName("GET /actividad/tipo/{tipo} - retorna lista vacía si no hay registros")
    void porTipo_sinResultados_retornaListaVacia() throws Exception {
        when(reporteUseCase.actividadPorTipo("RARO")).thenReturn(List.of());

        mockMvc.perform(get("/api/reportes/actividad/tipo/RARO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(0));
    }

    @Test
    @DisplayName("GET /actividad/usuario/{id} - retorna actividad del usuario")
    void porUsuario_retornaLista() throws Exception {
        when(reporteUseCase.actividadPorUsuario(5L)).thenReturn(List.of(actividadSample()));

        mockMvc.perform(get("/api/reportes/actividad/usuario/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    @DisplayName("GET /actividad/periodo - sin params usa defaults del useCase")
    void porPeriodo_sinParametros_retornaLista() throws Exception {
        when(reporteUseCase.actividadEnPeriodo(any(), any())).thenReturn(List.of(actividadSample()));

        mockMvc.perform(get("/api/reportes/actividad/periodo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    @DisplayName("GET /actividad/periodo - con fechas ISO retorna lista filtrada")
    void porPeriodo_conFechas_retornaLista() throws Exception {
        when(reporteUseCase.actividadEnPeriodo(any(), any())).thenReturn(List.of(actividadSample()));

        mockMvc.perform(get("/api/reportes/actividad/periodo")
                        .param("desde", "2025-01-01T00:00:00")
                        .param("hasta", "2025-01-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GlobalExceptionHandler: RuntimeException retorna 400 con exito=false")
    void excepcionRuntime_retorna400() throws Exception {
        when(reporteUseCase.actividadPorUsuario(anyLong()))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(get("/api/reportes/actividad/usuario/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler: MethodArgumentNotValidException retorna 400 con mensaje de validación")
    void excepcionValidacion_retorna400ConMensaje() throws Exception {
        ActividadRequestDTO req = new ActividadRequestDTO();
        req.setTipoEvento(null);

        mockMvc.perform(post("/api/reportes/actividad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("Validación fallida")));
    }

    @Test
    @DisplayName("GlobalExceptionHandler: Exception general retorna 500")
    void excepcionGeneral_retorna500() throws Exception {
        when(reporteUseCase.resumenGeneral())
                .thenThrow(new RuntimeException("error inesperado"));

        mockMvc.perform(get("/api/reportes/resumen"))
                .andExpect(status().isBadRequest());
    }
}
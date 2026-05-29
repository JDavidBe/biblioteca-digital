package com.biblioteca.descargas;

import com.biblioteca.descargas.application.dto.DescargaRequestDTO;
import com.biblioteca.descargas.domain.model.Descarga;
import com.biblioteca.descargas.domain.model.EstadisticaDescarga;
import com.biblioteca.descargas.domain.usecase.DescargaUseCase;
import com.biblioteca.descargas.infraestructure.entry_points.DescargaController;
import com.biblioteca.descargas.infraestructure.entry_points.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DescargaController - Pruebas de integración web")
class DescargaControllerTest {

    @Mock
    DescargaUseCase descargaUseCase;

    @InjectMocks
    DescargaController descargaController;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders
                .standaloneSetup(descargaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Descarga descargaSample() {
        Descarga d = new Descarga();
        d.setId(1L);
        d.setRecursoId(10L);
        d.setTituloRecurso("Libro Matemáticas");
        d.setUsuarioId(5L);
        d.setUsuarioCorreo("user@test.com");
        d.setDescargadoEn(LocalDateTime.now());
        return d;
    }

    @Test
    @DisplayName("POST /api/descargas - registra descarga y retorna 201")
    void registrar_datosValidos_retorna201() throws Exception {
        when(descargaUseCase.registrarDescarga(any())).thenReturn(descargaSample());

        DescargaRequestDTO req = new DescargaRequestDTO();
        req.setRecursoId(10L);
        req.setUsuarioId(5L);

        mockMvc.perform(post("/api/descargas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.recursoId").value(10));
    }

    @Test
    @DisplayName("POST /api/descargas - retorna 400 si recursoId es null")
    void registrar_recursoIdNull_retorna400() throws Exception {
        mockMvc.perform(post("/api/descargas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\":5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/descargas/usuario/{id} - historial del usuario")
    void historialPorUsuario_retornaLista() throws Exception {
        when(descargaUseCase.historialPorUsuario(5L)).thenReturn(List.of(descargaSample()));

        mockMvc.perform(get("/api/descargas/usuario/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/descargas/recurso/{id} - historial del recurso")
    void historialPorRecurso_retornaLista() throws Exception {
        when(descargaUseCase.historialPorRecurso(10L)).thenReturn(List.of(descargaSample()));

        mockMvc.perform(get("/api/descargas/recurso/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/descargas/recurso/{id}/total - total por recurso")
    void totalPorRecurso_retornaConteo() throws Exception {
        when(descargaUseCase.contarPorRecurso(10L)).thenReturn(42L);

        mockMvc.perform(get("/api/descargas/recurso/10/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos").value(42));
    }

    @Test
    @DisplayName("GET /api/descargas/usuario/{id}/total - total por usuario")
    void totalPorUsuario_retornaConteo() throws Exception {
        when(descargaUseCase.contarPorUsuario(5L)).thenReturn(7L);

        mockMvc.perform(get("/api/descargas/usuario/5/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos").value(7));
    }

    @Test
    @DisplayName("GET /api/descargas/recientes - descargas recientes")
    void recientes_retornaLista() throws Exception {
        when(descargaUseCase.descargasRecientes(20)).thenReturn(List.of(descargaSample()));

        mockMvc.perform(get("/api/descargas/recientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/descargas/recientes - con limite personalizado")
    void recientes_conLimite_retornaLista() throws Exception {
        when(descargaUseCase.descargasRecientes(5)).thenReturn(List.of(descargaSample()));

        mockMvc.perform(get("/api/descargas/recientes").param("limite", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/descargas/top - top recursos más descargados")
    void topDescargados_retornaEstadisticas() throws Exception {
        EstadisticaDescarga est = new EstadisticaDescarga();
        est.setRecursoId(10L);
        est.setTituloRecurso("Libro Matemáticas");
        est.setTotalDescargas(100L);
        when(descargaUseCase.topMasDescargados(10)).thenReturn(List.of(est));

        mockMvc.perform(get("/api/descargas/top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos[0].totalDescargas").value(100));
    }

    @Test
    @DisplayName("GET /api/descargas/top - con limite personalizado")
    void topDescargados_conLimite_retornaEstadisticas() throws Exception {
        when(descargaUseCase.topMasDescargados(3)).thenReturn(List.of());

        mockMvc.perform(get("/api/descargas/top").param("limite", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(0));
    }

    @Test
    @DisplayName("GlobalExceptionHandler: RuntimeException retorna 400")
    void excepcionRuntime_retorna400() throws Exception {
        when(descargaUseCase.historialPorUsuario(anyLong()))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(get("/api/descargas/usuario/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler: Exception generica retorna 500")
    void excepcionGenerica_retorna500() throws Exception {
        when(descargaUseCase.historialPorRecurso(anyLong()))
                .thenThrow(new Error("fallo inesperado"));

        mockMvc.perform(get("/api/descargas/recurso/99"))
                .andExpect(status().isInternalServerError());
    }
}
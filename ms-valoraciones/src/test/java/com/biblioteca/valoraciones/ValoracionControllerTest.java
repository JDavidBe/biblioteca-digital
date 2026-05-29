package com.biblioteca.valoraciones;

import com.biblioteca.valoraciones.application.config.SecurityConfig;
import com.biblioteca.valoraciones.application.dto.ValoracionRequestDTO;
import com.biblioteca.valoraciones.domain.model.ResumenValoracion;
import com.biblioteca.valoraciones.domain.model.Valoracion;
import com.biblioteca.valoraciones.domain.usecase.ValoracionUseCase;
import com.biblioteca.valoraciones.infraestructure.entry_points.GlobalExceptionHandler;
import com.biblioteca.valoraciones.infraestructure.entry_points.ValoracionController;
import com.biblioteca.valoraciones.infraestructure.security.JwtAuthFilter;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ValoracionController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthFilter.class}
        )
)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@DisplayName("ValoracionController - Pruebas de integración web")
class ValoracionControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean ValoracionUseCase valoracionUseCase;

    private Valoracion valoracionSample() {
        Valoracion v = new Valoracion();
        v.setId(1L); v.setRecursoId(10L); v.setUsuarioId(5L);
        v.setUsuarioCorreo("user@test.com"); v.setPuntuacion(4);
        v.setComentario("Buen recurso"); v.setCreadoEn(LocalDateTime.now());
        return v;
    }

    @Test
    @DisplayName("POST /api/valoraciones - registra y retorna 201")
    void calificar_datosValidos_retorna201() throws Exception {
        when(valoracionUseCase.calificar(any())).thenReturn(valoracionSample());
        ValoracionRequestDTO req = new ValoracionRequestDTO();
        req.setRecursoId(10L); req.setPuntuacion(4);

        mockMvc.perform(post("/api/valoraciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.puntuacion").value(4));
    }

    @Test
    @DisplayName("POST /api/valoraciones - retorna 400 si puntuación fuera de rango")
    void calificar_puntuacionInvalida_retorna400() throws Exception {
        ValoracionRequestDTO req = new ValoracionRequestDTO();
        req.setRecursoId(10L); req.setPuntuacion(6);

        mockMvc.perform(post("/api/valoraciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/valoraciones - retorna 400 si recursoId es null")
    void calificar_recursoIdNull_retorna400() throws Exception {
        ValoracionRequestDTO req = new ValoracionRequestDTO();
        req.setPuntuacion(3);

        mockMvc.perform(post("/api/valoraciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("POST /api/valoraciones - retorna 400 si puntuación es null")
    void calificar_puntuacionNull_retorna400() throws Exception {
        ValoracionRequestDTO req = new ValoracionRequestDTO();
        req.setRecursoId(10L);

        mockMvc.perform(post("/api/valoraciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/valoraciones - propaga todos los campos al useCase")
    void calificar_camposCompletos_propagaCampos() throws Exception {
        when(valoracionUseCase.calificar(any())).thenReturn(valoracionSample());
        ValoracionRequestDTO req = new ValoracionRequestDTO();
        req.setRecursoId(10L); req.setUsuarioId(5L);
        req.setUsuarioCorreo("u@t.com"); req.setPuntuacion(5);
        req.setComentario("Excelente");

        mockMvc.perform(post("/api/valoraciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
        verify(valoracionUseCase).calificar(any());
    }

    @Test
    @DisplayName("GET /api/valoraciones/recurso/{id} - lista valoraciones del recurso")
    void porRecurso_retornaLista() throws Exception {
        when(valoracionUseCase.obtenerPorRecurso(10L)).thenReturn(List.of(valoracionSample()));

        mockMvc.perform(get("/api/valoraciones/recurso/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1))
                .andExpect(jsonPath("$.datos[0].recursoId").value(10));
    }

    @Test
    @DisplayName("GET /api/valoraciones/recurso/{id} - lista vacía")
    void porRecurso_sinResultados_retornaVacia() throws Exception {
        when(valoracionUseCase.obtenerPorRecurso(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/valoraciones/recurso/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/valoraciones/recurso/{id}/resumen - retorna resumen completo")
    void resumen_retornaPromedio() throws Exception {
        ResumenValoracion resumen = new ResumenValoracion(10L, 4.2, 5L);
        when(valoracionUseCase.resumenPorRecurso(10L)).thenReturn(resumen);

        mockMvc.perform(get("/api/valoraciones/recurso/10/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.recursoId").value(10))
                .andExpect(jsonPath("$.datos.promedio").value(4.2))
                .andExpect(jsonPath("$.datos.totalValoraciones").value(5));
    }

    @Test
    @DisplayName("GET /api/valoraciones/usuario/{id} - lista valoraciones del usuario")
    void porUsuario_retornaLista() throws Exception {
        when(valoracionUseCase.obtenerPorUsuario(5L)).thenReturn(List.of(valoracionSample()));

        mockMvc.perform(get("/api/valoraciones/usuario/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    @DisplayName("DELETE /api/valoraciones/{id} - elimina y retorna 200")
    void eliminar_retorna200() throws Exception {
        doNothing().when(valoracionUseCase).eliminarValoracion(1L);

        mockMvc.perform(delete("/api/valoraciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Valoración eliminada"));
    }

    @Test
    @DisplayName("DELETE /api/valoraciones/{id} - retorna 400 si no existe")
    void eliminar_noExiste_retorna400() throws Exception {
        doThrow(new RuntimeException("No existe valoración con id: 99"))
                .when(valoracionUseCase).eliminarValoracion(99L);

        mockMvc.perform(delete("/api/valoraciones/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("GlobalExceptionHandler: RuntimeException retorna 400 con mensaje")
    void excepcionRuntime_retorna400() throws Exception {
        when(valoracionUseCase.obtenerPorRecurso(anyLong()))
                .thenThrow(new RuntimeException("Recurso no encontrado"));

        mockMvc.perform(get("/api/valoraciones/recurso/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Recurso no encontrado"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler: validación retorna mensaje descriptivo")
    void excepcionValidacion_retornaMensajeDescriptivo() throws Exception {
        ValoracionRequestDTO req = new ValoracionRequestDTO();
        req.setPuntuacion(0);

        mockMvc.perform(post("/api/valoraciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("Validación fallida")));
    }
}
package com.biblioteca.recursos;

import com.biblioteca.recursos.domain.model.Recurso;
import com.biblioteca.recursos.domain.usecase.RecursoUseCase;
import com.biblioteca.recursos.infraestructure.entry_points.GlobalExceptionHandler;
import com.biblioteca.recursos.infraestructure.entry_points.RecursoController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RecursoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class})
@DisplayName("RecursoController - Pruebas de integración web")
class RecursoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RecursoUseCase recursoUseCase;

    private Recurso recursoSample() {
        Recurso r = new Recurso();

        r.setId(1L);
        r.setTitulo("Álgebra Básica");
        r.setDescripcion("Guía de álgebra");
        r.setArea("Matemáticas");
        r.setGrado("10");
        r.setTipo("PDF");
        r.setNombreArchivo("algebra.pdf");
        r.setTamanoBytes(1024L);
        r.setSubidoPorCorreo("prof@test.com");
        r.setCreadoEn(LocalDateTime.now());
        r.setDisponible(true);

        return r;
    }

    @Test
    @DisplayName("POST /api/recursos - publica recurso con archivo y retorna 201")
    void publicar_multipart_retorna201() throws Exception {

        when(recursoUseCase.publicarRecurso(any(), any()))
                .thenReturn(recursoSample());

        String datosJson =
                "{\"titulo\":\"Álgebra Básica\",\"descripcion\":\"Guía\",\"area\":\"Matemáticas\","
                        + "\"grado\":\"10\",\"tipo\":\"PDF\",\"subidoPorId\":1,"
                        + "\"subidoPorCorreo\":\"prof@test.com\"}";

        MockMultipartFile datos =
                new MockMultipartFile(
                        "datos",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        datosJson.getBytes()
                );

        MockMultipartFile archivo =
                new MockMultipartFile(
                        "archivo",
                        "algebra.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        "contenido pdf".getBytes()
                );

        mockMvc.perform(
                        multipart("/api/recursos")
                                .file(datos)
                                .file(archivo)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.titulo").value("Álgebra Básica"));
    }

    @Test
    @DisplayName("GET /api/recursos - lista todos los recursos")
    void listar_sinFiltros_retornaLista() throws Exception {

        when(recursoUseCase.listarTodos())
                .thenReturn(List.of(recursoSample()));

        mockMvc.perform(get("/api/recursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/recursos?area=Matemáticas - busca con filtros")
    void listar_conFiltros_buscaRecursos() throws Exception {

        when(recursoUseCase.buscarRecursos("Matemáticas", null, null))
                .thenReturn(List.of(recursoSample()));

        mockMvc.perform(
                        get("/api/recursos")
                                .param("area", "Matemáticas")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos[0].area").value("Matemáticas"));
    }

    @Test
    @DisplayName("GET /api/recursos/{id} - retorna recurso por ID")
    void obtener_existente_retorna200() throws Exception {

        when(recursoUseCase.obtenerPorId(1L))
                .thenReturn(recursoSample());

        mockMvc.perform(get("/api/recursos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.id").value(1));
    }

    @Test
    @DisplayName("GET /api/recursos/{id} - retorna 400 si no existe")
    void obtener_noExiste_retorna400() throws Exception {

        when(recursoUseCase.obtenerPorId(99L))
                .thenThrow(new RuntimeException("No existe recurso con id: 99"));

        mockMvc.perform(get("/api/recursos/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("GET /api/recursos/usuario/{id} - lista recursos del usuario")
    void listarPorUsuario_retornaLista() throws Exception {

        when(recursoUseCase.listarPorUsuario(1L))
                .thenReturn(List.of(recursoSample()));

        mockMvc.perform(get("/api/recursos/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/recursos/{id}/descargar - descarga el archivo")
    void descargar_retornaBytesConHeader() throws Exception {

        Recurso r = recursoSample();
        r.setContenido("contenido".getBytes());

        when(recursoUseCase.obtenerPorId(1L))
                .thenReturn(r);

        mockMvc.perform(get("/api/recursos/1/descargar"))
                .andExpect(status().isOk())
                .andExpect(
                        header().string(
                                "Content-Disposition",
                                org.hamcrest.Matchers.containsString("algebra.pdf")
                        )
                );
    }

    @Test
    @DisplayName("PUT /api/recursos/{id} - edita recurso")
    void editar_retorna200() throws Exception {

        when(recursoUseCase.editarRecurso(eq(1L), any()))
                .thenReturn(recursoSample());

        mockMvc.perform(
                        put("/api/recursos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"titulo\":\"Álgebra Actualizada\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/recursos/{id} - elimina recurso")
    void eliminar_retorna200() throws Exception {

        doNothing().when(recursoUseCase)
                .eliminarRecurso(1L);

        mockMvc.perform(delete("/api/recursos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GlobalExceptionHandler: RuntimeException retorna 400 con mensaje")
    void excepcionRuntime_retorna400() throws Exception {

        when(recursoUseCase.listarTodos())
                .thenThrow(new RuntimeException("Error de almacenamiento"));

        mockMvc.perform(get("/api/recursos"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Error de almacenamiento"));
    }
}
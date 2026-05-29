package com.biblioteca.usuarios;

import com.biblioteca.usuarios.application.config.SecurityConfig;
import com.biblioteca.usuarios.application.dto.UsuarioRequestDTO;
import com.biblioteca.usuarios.application.dto.UsuarioUpdateDTO;
import com.biblioteca.usuarios.domain.model.Usuario;
import com.biblioteca.usuarios.domain.usecase.UsuarioUseCase;
import com.biblioteca.usuarios.infraestructure.entry_points.GlobalExceptionHandler;
import com.biblioteca.usuarios.infraestructure.entry_points.UsuarioController;
import com.biblioteca.usuarios.infraestructure.security.JwtAuthFilter;
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
        controllers = UsuarioController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthFilter.class}
        )
)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@DisplayName("UsuarioController - Pruebas de integración web")
class UsuarioControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean UsuarioUseCase usuarioUseCase;

    private Usuario usuarioSample() {
        return new Usuario(1L, "Ana García", "ana@test.com", "IED Central", "10", "ESTUDIANTE", true, LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /api/usuarios - crea usuario y retorna 201")
    void crear_datosValidos_retorna201() throws Exception {
        when(usuarioUseCase.crearUsuario(any())).thenReturn(usuarioSample());
        UsuarioRequestDTO req = new UsuarioRequestDTO();
        req.setNombre("Ana García");
        req.setCorreo("ana@test.com");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.nombre").value("Ana García"));
    }

    @Test
    @DisplayName("POST /api/usuarios - retorna 400 si nombre en blanco")
    void crear_nombreBlanco_retorna400() throws Exception {
        UsuarioRequestDTO req = new UsuarioRequestDTO();
        req.setNombre("");
        req.setCorreo("ana@test.com");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/usuarios - retorna 400 si correo inválido")
    void crear_correoInvalido_retorna400() throws Exception {
        UsuarioRequestDTO req = new UsuarioRequestDTO();
        req.setNombre("Ana");
        req.setCorreo("no-es-email");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("Validación fallida")));
    }

    @Test
    @DisplayName("POST /api/usuarios - retorna 400 si nombre es null")
    void crear_nombreNull_retorna400() throws Exception {
        UsuarioRequestDTO req = new UsuarioRequestDTO();
        req.setCorreo("ana@test.com");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("POST /api/usuarios - propaga todos los campos al useCase")
    void crear_camposCompletos_propagaCampos() throws Exception {
        when(usuarioUseCase.crearUsuario(any())).thenReturn(usuarioSample());
        UsuarioRequestDTO req = new UsuarioRequestDTO();
        req.setNombre("Carlos");
        req.setCorreo("carlos@test.com");
        req.setInstitucion("IED Sur");
        req.setGrado("11");
        req.setRol("DOCENTE");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
        verify(usuarioUseCase).crearUsuario(any());
    }

    @Test
    @DisplayName("GET /api/usuarios - lista todos")
    void listar_retornaLista() throws Exception {
        when(usuarioUseCase.listarTodos()).thenReturn(List.of(usuarioSample()));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/usuarios?rol=ADMIN - filtra por rol")
    void listar_conRol_filtraPorRol() throws Exception {
        when(usuarioUseCase.listarPorRol("ADMIN")).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios").param("rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(0));
        verify(usuarioUseCase).listarPorRol("ADMIN");
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} - retorna usuario por ID")
    void obtenerPorId_existente_retorna200() throws Exception {
        when(usuarioUseCase.obtenerPorId(1L)).thenReturn(usuarioSample());

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.id").value(1))
                .andExpect(jsonPath("$.datos.correo").value("ana@test.com"));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} - retorna 400 si no existe")
    void obtenerPorId_noExiste_retorna400() throws Exception {
        when(usuarioUseCase.obtenerPorId(99L))
                .thenThrow(new RuntimeException("No existe usuario con id: 99"));

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("No existe usuario con id: 99"));
    }

    @Test
    @DisplayName("GET /api/usuarios/correo/{correo} - retorna usuario por correo")
    void obtenerPorCorreo_existente_retorna200() throws Exception {
        when(usuarioUseCase.obtenerPorCorreo("ana@test.com")).thenReturn(usuarioSample());

        mockMvc.perform(get("/api/usuarios/correo/ana@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.correo").value("ana@test.com"));
    }

    @Test
    @DisplayName("GET /api/usuarios/correo/{correo} - retorna 400 si no existe")
    void obtenerPorCorreo_noExiste_retorna400() throws Exception {
        when(usuarioUseCase.obtenerPorCorreo("no@test.com"))
                .thenThrow(new RuntimeException("No existe usuario con correo: no@test.com"));

        mockMvc.perform(get("/api/usuarios/correo/no@test.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} - actualiza usuario")
    void actualizar_retorna200() throws Exception {
        when(usuarioUseCase.modificarUsuario(eq(1L), any())).thenReturn(usuarioSample());
        UsuarioUpdateDTO req = new UsuarioUpdateDTO();
        req.setNombre("Ana Actualizada");

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} - retorna 400 si no existe")
    void actualizar_noExiste_retorna400() throws Exception {
        when(usuarioUseCase.modificarUsuario(eq(99L), any()))
                .thenThrow(new RuntimeException("No existe usuario con id: 99"));

        mockMvc.perform(put("/api/usuarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} - elimina usuario")
    void eliminar_retorna200() throws Exception {
        doNothing().when(usuarioUseCase).eliminarUsuario(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Usuario eliminado"));
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} - retorna 400 si no existe")
    void eliminar_noExiste_retorna400() throws Exception {
        doThrow(new RuntimeException("No existe usuario con id: 99"))
                .when(usuarioUseCase).eliminarUsuario(99L);

        mockMvc.perform(delete("/api/usuarios/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("GlobalExceptionHandler: RuntimeException retorna 400 con mensaje")
    void excepcionRuntime_retorna400() throws Exception {
        when(usuarioUseCase.obtenerPorId(anyLong()))
                .thenThrow(new RuntimeException("Error de negocio"));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Error de negocio"));
    }
}
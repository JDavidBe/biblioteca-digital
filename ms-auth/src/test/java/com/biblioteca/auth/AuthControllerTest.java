package com.biblioteca.auth;

import com.biblioteca.auth.application.config.SecurityConfig;
import com.biblioteca.auth.application.dto.LoginRequestDTO;
import com.biblioteca.auth.application.dto.RegistroRequestDTO;
import com.biblioteca.auth.domain.model.Credencial;
import com.biblioteca.auth.domain.usecase.AuthUseCase;
import com.biblioteca.auth.infraestructure.entry_points.AuthController;
import com.biblioteca.auth.infraestructure.entry_points.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        })
@Import(GlobalExceptionHandler.class)
@DisplayName("AuthController - Pruebas de integración web")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AuthUseCase authUseCase;

    @Test
    @DisplayName("POST /api/auth/login - devuelve 200 con token")
    void login_credencialesValidas_retorna200() throws Exception {
        when(authUseCase.iniciarSesionCompleto(eq("user@test.com"), eq("pass123")))
                .thenReturn(new Credencial(1L, "user@test.com", "$hash$", "ESTUDIANTE", true));
        when(authUseCase.generarToken(any())).thenReturn("jwt-token");

        LoginRequestDTO req = new LoginRequestDTO();
        req.setCorreo("user@test.com");
        req.setPassword("pass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.token").value("jwt-token"));
    }

    @Test
    @DisplayName("POST /api/auth/login - devuelve 400 si correo inválido (validación)")
    void login_correoInvalido_retorna400() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setCorreo("no-es-email");
        req.setPassword("pass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/login - devuelve 400 cuando usecase lanza excepción")
    void login_credencialesInvalidas_retorna400() throws Exception {
        when(authUseCase.iniciarSesionCompleto(anyString(), anyString()))
                .thenThrow(new RuntimeException("Contraseña incorrecta"));

        LoginRequestDTO req = new LoginRequestDTO();
        req.setCorreo("user@test.com");
        req.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Contraseña incorrecta"));
    }

    @Test
    @DisplayName("POST /api/auth/registro - devuelve 201 cuando es exitoso")
    void registro_datosValidos_retorna201() throws Exception {
        Credencial guardada = new Credencial(1L, "nuevo@test.com", "$hash$", "ESTUDIANTE", true);
        when(authUseCase.registrar(any())).thenReturn(guardada);

        RegistroRequestDTO req = new RegistroRequestDTO();
        req.setCorreo("nuevo@test.com");
        req.setPassword("pass123");

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Cuenta creada exitosamente"));
    }

    @Test
    @DisplayName("POST /api/auth/registro - devuelve 400 si password muy corta")
    void registro_passwordCorta_retorna400() throws Exception {
        RegistroRequestDTO req = new RegistroRequestDTO();
        req.setCorreo("nuevo@test.com");
        req.setPassword("abc");

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/registro - devuelve 400 si correo inválido")
    void registro_correoInvalido_retorna400() throws Exception {
        RegistroRequestDTO req = new RegistroRequestDTO();
        req.setCorreo("no-es-email");
        req.setPassword("pass123");

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/registro - devuelve 400 si usecase lanza excepción")
    void registro_correoExistente_retorna400() throws Exception {
        when(authUseCase.registrar(any()))
                .thenThrow(new RuntimeException("Ya existe una cuenta con el correo: nuevo@test.com"));

        RegistroRequestDTO req = new RegistroRequestDTO();
        req.setCorreo("nuevo@test.com");
        req.setPassword("pass123");

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("GET /api/auth/validar - devuelve 200 con token válido")
    void validarToken_tokenValido_retorna200() throws Exception {
        when(authUseCase.validarToken("valid-token")).thenReturn(true);
        when(authUseCase.extraerCorreo("valid-token")).thenReturn("user@test.com");

        mockMvc.perform(get("/api/auth/validar")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.valido").value(true))
                .andExpect(jsonPath("$.datos.correo").value("user@test.com"));
    }

    @Test
    @DisplayName("GET /api/auth/validar - devuelve 200 cuando token es inválido")
    void validarToken_tokenInvalido_retorna200ConValidoFalse() throws Exception {
        when(authUseCase.validarToken("bad-token")).thenReturn(false);

        mockMvc.perform(get("/api/auth/validar")
                        .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.valido").value(false));
    }

    @Test
    @DisplayName("GET /api/auth/validar - devuelve 400 si falta header Authorization")
    void validarToken_sinHeader_retorna400() throws Exception {
        mockMvc.perform(get("/api/auth/validar"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/auth/credencial/{correo} - devuelve 200 cuando elimina")
    void eliminarCredencial_correoValido_retorna200() throws Exception {
        doNothing().when(authUseCase).eliminarCredencial("user@test.com");

        mockMvc.perform(delete("/api/auth/credencial/user@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/auth/credencial/{correo} - devuelve 400 si no existe")
    void eliminarCredencial_correoNoExiste_retorna400() throws Exception {
        doThrow(new RuntimeException("No existe credencial con el correo: noexiste@test.com"))
                .when(authUseCase).eliminarCredencial("noexiste@test.com");

        mockMvc.perform(delete("/api/auth/credencial/noexiste@test.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }
}
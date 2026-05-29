package com.biblioteca.auth;

import com.biblioteca.auth.domain.usecase.AuthUseCase;
import com.biblioteca.auth.infraestructure.entry_points.AuthController;
import com.biblioteca.auth.infraestructure.entry_points.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
@DisplayName("GlobalExceptionHandler - ms-auth")
class GlobalExceptionHandlerTest {

    @Autowired MockMvc mockMvc;
    @MockBean AuthUseCase authUseCase;

    @Test
    @DisplayName("handleValidation: cuerpo inválido devuelve 400 con detalle")
    void handleValidation_retorna400ConDetalle() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("Validación fallida")));
    }

    @Test
    @DisplayName("handleRuntime: RuntimeException devuelve 400 con mensaje")
    void handleRuntime_retorna400() throws Exception {
        when(authUseCase.iniciarSesionCompleto(anyString(), anyString()))
                .thenThrow(new RuntimeException("Error de negocio"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"a@b.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Error de negocio"));
    }

    @Test
    @DisplayName("handleGeneral: Exception genérica devuelve 500")
    void handleGeneral_retorna500() throws Exception {
        when(authUseCase.validarToken(anyString()))
                .thenAnswer(inv -> { throw new Exception("fallo inesperado"); });

        mockMvc.perform(get("/api/auth/validar")
                        .header("Authorization", "Bearer some-token"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Error interno del servidor"));
    }

    @Test
    @DisplayName("handleMissingHeader: header ausente devuelve 400")
    void handleMissingHeader_retorna400() throws Exception {
        mockMvc.perform(get("/api/auth/validar"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }
}
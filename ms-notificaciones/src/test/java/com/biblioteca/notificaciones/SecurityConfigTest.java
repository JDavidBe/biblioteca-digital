package com.biblioteca.notificaciones;

import com.biblioteca.notificaciones.application.config.SecurityConfig;
import com.biblioteca.notificaciones.domain.usecase.NotificacionUseCase;
import com.biblioteca.notificaciones.infraestructure.entry_points.NotificacionController;
import com.biblioteca.notificaciones.infraestructure.security.JwtAuthFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificacionController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
@DisplayName("SecurityConfig - Pruebas de configuración de seguridad")
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    NotificacionUseCase notificacionUseCase;

    @Test
    @DisplayName("Sin token retorna 401")
    void sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Sin token el body indica token requerido")
    void sinToken_bodyIndicaTokenRequerido() throws Exception {
        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Token requerido, debes hacer login primero"));
    }

    @Test
    @DisplayName("Con token malformado retorna 401")
    void tokenMalformado_retorna401() throws Exception {
        mockMvc.perform(get("/api/notificaciones")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token.invalido.aqui"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Con token expirado retorna 401")
    void tokenExpirado_retorna401() throws Exception {
        String tokenExpirado = "eyJhbGciOiJIUzI1NiJ9." +
                "eyJzdWIiOiJ1c2VyQHRlc3QuY29tIiwicm9sIjoiQURNSU4iLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDAwMDAwMX0." +
                "abc123";
        mockMvc.perform(get("/api/notificaciones")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenExpirado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Ruta protegida sin token no llega al controller")
    void rutaProtegida_sinToken_noPasaAlController() throws Exception {
        mockMvc.perform(get("/api/notificaciones/1"))
                .andExpect(status().isUnauthorized());
    }
}
package com.biblioteca.recursos;

import com.biblioteca.recursos.application.config.SecurityConfig;
import com.biblioteca.recursos.domain.usecase.RecursoUseCase;
import com.biblioteca.recursos.infraestructure.entry_points.RecursoController;
import com.biblioteca.recursos.infraestructure.security.JwtAuthFilter;
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

@WebMvcTest(controllers = RecursoController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
@DisplayName("SecurityConfig Recursos - Pruebas de seguridad")
class SecurityConfigRecursosTest {

    @Autowired MockMvc mockMvc;
    @MockBean RecursoUseCase recursoUseCase;

    @Test
    @DisplayName("Sin token retorna 401 con body")
    void sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/recursos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Token requerido"));
    }

    @Test
    @DisplayName("Con token malformado retorna 401")
    void tokenMalformado_retorna401() throws Exception {
        mockMvc.perform(get("/api/recursos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token.invalido.aqui"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Ruta con id sin token retorna 401")
    void rutaConId_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/recursos/1"))
                .andExpect(status().isUnauthorized());
    }
}
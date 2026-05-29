package com.biblioteca.descargas;

import com.biblioteca.descargas.application.config.SecurityConfig;
import com.biblioteca.descargas.infraestructure.security.JwtAuthFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SecurityConfig - configuración de seguridad")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    @DisplayName("filterChain: bean no nulo")
    void filterChain_noEsNulo() {
        assertThat(securityFilterChain).isNotNull();
    }

    @Test
    @DisplayName("corsConfigurationSource: retorna bean no nulo")
    void corsConfigurationSource_noEsNulo() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertThat(source).isNotNull();
    }

    @Test
    @DisplayName("actuator/health: accesible sin autenticación")
    void actuatorHealth_sinAuth_retorna200() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("endpoint protegido: sin token retorna acceso permitido")
    void endpointProtegido_sinToken() throws Exception {
        mockMvc.perform(get("/api/descargas"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CORS preflight desde localhost:3000: retorna 200")
    void cors_preflight_localhost3000() throws Exception {
        mockMvc.perform(options("/api/descargas")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CORS preflight desde localhost:5173: retorna 200")
    void cors_preflight_localhost5173() throws Exception {
        mockMvc.perform(options("/api/descargas")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CORS preflight desde origen no permitido: retorna 403")
    void cors_preflight_origenNoPermitido_retorna403() throws Exception {
        mockMvc.perform(options("/api/descargas")
                        .header("Origin", "http://malicioso.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("v3/api-docs: accesible sin autenticación")
    void apiDocs_sinAuth_noRetorna401() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(result ->
                        assertThat(result.getResponse().getStatus()).isNotEqualTo(401));
    }
}
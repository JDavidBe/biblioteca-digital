package com.biblioteca.auth;

import com.biblioteca.auth.application.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SecurityConfig - Pruebas unitarias")
class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    @DisplayName("corsConfigurationSource retorna un bean no nulo")
    void corsConfigurationSource_retornaBeanNoNulo() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertThat(source).isNotNull();
    }
}
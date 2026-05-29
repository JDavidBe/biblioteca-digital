package com.biblioteca.usuarios;

import com.biblioteca.usuarios.application.config.SecurityConfig;
import com.biblioteca.usuarios.infraestructure.security.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void corsConfigurationSource_noEsNull() {
        SecurityConfig config = new SecurityConfig(mock(JwtAuthFilter.class));
        assertThat(config.corsConfigurationSource()).isNotNull();
    }
}
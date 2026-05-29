package com.biblioteca.valoraciones;

import com.biblioteca.valoraciones.application.config.SecurityConfig;
import com.biblioteca.valoraciones.infraestructure.security.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void corsConfigurationSource_noEsNull() {
        assertThat(new SecurityConfig(mock(JwtAuthFilter.class)).corsConfigurationSource()).isNotNull();
    }
}
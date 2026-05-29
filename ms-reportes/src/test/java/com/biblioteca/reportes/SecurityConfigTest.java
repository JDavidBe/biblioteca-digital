package com.biblioteca.reportes;

import com.biblioteca.reportes.application.config.SecurityConfig;
import com.biblioteca.reportes.infraestructure.security.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.SecurityFilterChain;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void corsConfigurationSource_noEsNull() {
        JwtAuthFilter filter = mock(JwtAuthFilter.class);
        SecurityConfig config = new SecurityConfig(filter);
        assertThat(config.corsConfigurationSource()).isNotNull();
    }
}
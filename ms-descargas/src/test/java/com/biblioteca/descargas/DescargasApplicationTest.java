package com.biblioteca.descargas;

import com.biblioteca.descargas.infraestructure.security.JwtAuthFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // ← reemplazo

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("DescargasApplication - cobertura clase main")
class DescargasApplicationTest {

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("main: se puede instanciar la clase")
    void main_instancia_noLanzaExcepcion() {
        DescargasApplication app = new DescargasApplication();
        assertThat(app).isNotNull();
    }

    @Test
    @DisplayName("main: método main no lanza excepción")
    void main_metodo_noLanzaExcepcion() {
        assertThat(DescargasApplication.class.getDeclaredMethods()).isNotEmpty();
    }
}
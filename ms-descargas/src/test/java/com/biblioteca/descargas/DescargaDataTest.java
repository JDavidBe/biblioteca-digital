package com.biblioteca.descargas;

import com.biblioteca.descargas.infraestructure.driver_adapters.jpa_repository.DescargaData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DescargaData - entidad JPA")
class DescargaDataTest {

    @Test
    @DisplayName("getters y setters funcionan correctamente")
    void gettersSetters_funcionanCorrectamente() {
        DescargaData d = new DescargaData();
        LocalDateTime now = LocalDateTime.now();

        d.setId(1L);
        d.setRecursoId(5L);
        d.setTituloRecurso("Java Avanzado");
        d.setUsuarioId(2L);
        d.setUsuarioCorreo("user@test.com");
        d.setDescargadoEn(now);
        d.setIpOrigen("192.168.0.1");

        assertThat(d.getId()).isEqualTo(1L);
        assertThat(d.getRecursoId()).isEqualTo(5L);
        assertThat(d.getTituloRecurso()).isEqualTo("Java Avanzado");
        assertThat(d.getUsuarioId()).isEqualTo(2L);
        assertThat(d.getUsuarioCorreo()).isEqualTo("user@test.com");
        assertThat(d.getDescargadoEn()).isEqualTo(now);
        assertThat(d.getIpOrigen()).isEqualTo("192.168.0.1");
    }

    @Test
    @DisplayName("instancia vacía tiene todos los campos null")
    void instanciaVacia_camposNull() {
        DescargaData d = new DescargaData();
        assertThat(d.getId()).isNull();
        assertThat(d.getRecursoId()).isNull();
        assertThat(d.getTituloRecurso()).isNull();
        assertThat(d.getUsuarioId()).isNull();
        assertThat(d.getUsuarioCorreo()).isNull();
        assertThat(d.getDescargadoEn()).isNull();
        assertThat(d.getIpOrigen()).isNull();
    }
}
package com.biblioteca.descargas;

import com.biblioteca.descargas.domain.model.Descarga;
import com.biblioteca.descargas.infraestructure.driver_adapters.jpa_repository.DescargaData;
import com.biblioteca.descargas.infraestructure.mapper.DescargaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DescargaMapper - Pruebas unitarias")
class DescargaMapperTest {

    private DescargaMapper mapper;
    private LocalDateTime ahora;

    @BeforeEach
    void setUp() {
        mapper = new DescargaMapper();
        ahora = LocalDateTime.now();
    }

    @Test
    @DisplayName("toDomain: mapea todos los campos correctamente")
    void toDomain_dataCompleta_mapeaCorrectamente() {
        DescargaData data = new DescargaData();
        data.setId(1L);
        data.setRecursoId(5L);
        data.setTituloRecurso("Java Avanzado");
        data.setUsuarioId(2L);
        data.setUsuarioCorreo("u@t.com");
        data.setDescargadoEn(ahora);
        data.setIpOrigen("192.168.0.1");

        Descarga domain = mapper.toDomain(data);

        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getRecursoId()).isEqualTo(5L);
        assertThat(domain.getTituloRecurso()).isEqualTo("Java Avanzado");
        assertThat(domain.getUsuarioId()).isEqualTo(2L);
        assertThat(domain.getUsuarioCorreo()).isEqualTo("u@t.com");
        assertThat(domain.getDescargadoEn()).isEqualTo(ahora);
        assertThat(domain.getIpOrigen()).isEqualTo("192.168.0.1");
    }

    @Test
    @DisplayName("toData: mapea todos los campos correctamente")
    void toData_domainCompleto_mapeaCorrectamente() {
        Descarga domain = new Descarga(2L, 10L, "Python", 3L, "x@y.com", ahora, "10.0.0.1");

        DescargaData data = mapper.toData(domain);

        assertThat(data.getId()).isEqualTo(2L);
        assertThat(data.getRecursoId()).isEqualTo(10L);
        assertThat(data.getTituloRecurso()).isEqualTo("Python");
        assertThat(data.getUsuarioId()).isEqualTo(3L);
        assertThat(data.getUsuarioCorreo()).isEqualTo("x@y.com");
        assertThat(data.getDescargadoEn()).isEqualTo(ahora);
        assertThat(data.getIpOrigen()).isEqualTo("10.0.0.1");
    }

    @Test
    @DisplayName("round-trip: preserva todos los campos")
    void roundTrip_preservaDatos() {
        DescargaData original = new DescargaData();
        original.setId(3L);
        original.setRecursoId(7L);
        original.setTituloRecurso("Recurso Test");
        original.setUsuarioId(4L);
        original.setUsuarioCorreo("rt@test.com");
        original.setDescargadoEn(ahora);
        original.setIpOrigen("172.16.0.1");

        DescargaData resultado = mapper.toData(mapper.toDomain(original));

        assertThat(resultado.getId()).isEqualTo(original.getId());
        assertThat(resultado.getRecursoId()).isEqualTo(original.getRecursoId());
        assertThat(resultado.getTituloRecurso()).isEqualTo(original.getTituloRecurso());
        assertThat(resultado.getUsuarioId()).isEqualTo(original.getUsuarioId());
        assertThat(resultado.getIpOrigen()).isEqualTo(original.getIpOrigen());
    }

    @Test
    @DisplayName("toDomain: campos opcionales null permanecen null")
    void toDomain_camposOpcionalesNull_sonNull() {
        DescargaData data = new DescargaData();
        data.setId(1L);
        data.setRecursoId(5L);

        Descarga domain = mapper.toDomain(data);

        assertThat(domain.getTituloRecurso()).isNull();
        assertThat(domain.getUsuarioId()).isNull();
        assertThat(domain.getUsuarioCorreo()).isNull();
        assertThat(domain.getDescargadoEn()).isNull();
        assertThat(domain.getIpOrigen()).isNull();
    }
}

package com.biblioteca.recursos;

import com.biblioteca.recursos.domain.model.Recurso;
import com.biblioteca.recursos.infraestructure.driver_adapters.jpa_repository.RecursoData;
import com.biblioteca.recursos.infraestructure.mapper.RecursoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RecursoMapper - Pruebas unitarias")
class RecursoMapperTest {

    private RecursoMapper mapper;
    private LocalDateTime ahora;

    @BeforeEach
    void setUp() {
        mapper = new RecursoMapper();
        ahora = LocalDateTime.now();
    }

    @Test
    @DisplayName("toDomain: mapea todos los campos")
    void toDomain_dataCompleta_mapeaCorrectamente() {
        RecursoData data = new RecursoData();
        data.setId(1L);
        data.setTitulo("Java Básico");
        data.setDescripcion("Intro a Java");
        data.setArea("Programación");
        data.setGrado("10");
        data.setTipo("PDF");
        data.setNombreArchivo("java.pdf");
        data.setRutaArchivo("/uploads/java.pdf");
        data.setTamanoBytes(1024L);
        data.setSubidoPorId(2L);
        data.setSubidoPorCorreo("prof@test.com");
        data.setCreadoEn(ahora);
        data.setDisponible(true);

        Recurso domain = mapper.toDomain(data);

        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getTitulo()).isEqualTo("Java Básico");
        assertThat(domain.getDescripcion()).isEqualTo("Intro a Java");
        assertThat(domain.getArea()).isEqualTo("Programación");
        assertThat(domain.getGrado()).isEqualTo("10");
        assertThat(domain.getTipo()).isEqualTo("PDF");
        assertThat(domain.getNombreArchivo()).isEqualTo("java.pdf");
        assertThat(domain.getRutaArchivo()).isEqualTo("/uploads/java.pdf");
        assertThat(domain.getTamanoBytes()).isEqualTo(1024L);
        assertThat(domain.getSubidoPorId()).isEqualTo(2L);
        assertThat(domain.getSubidoPorCorreo()).isEqualTo("prof@test.com");
        assertThat(domain.getCreadoEn()).isEqualTo(ahora);
        assertThat(domain.getDisponible()).isTrue();
    }

    @Test
    @DisplayName("toData: mapea todos los campos")
    void toData_domainCompleto_mapeaCorrectamente() {
        Recurso domain = new Recurso(2L, "Python", "Desc", "Tech", "11", "PDF",
                "py.pdf", "/py.pdf", 2048L, 3L, "doc@test.com", ahora, true, null);

        RecursoData data = mapper.toData(domain);

        assertThat(data.getId()).isEqualTo(2L);
        assertThat(data.getTitulo()).isEqualTo("Python");
        assertThat(data.getTipo()).isEqualTo("PDF");
        assertThat(data.getDisponible()).isTrue();
    }

    @Test
    @DisplayName("toData: tipo null usa PDF por defecto")
    void toData_tipoNull_usaPDF() {
        Recurso domain = new Recurso();
        domain.setId(3L);
        domain.setTitulo("Test");
        domain.setTipo(null);
        domain.setDisponible(true);

        assertThat(mapper.toData(domain).getTipo()).isEqualTo("PDF");
    }

    @Test
    @DisplayName("toData: disponible null usa true por defecto")
    void toData_disponibleNull_usaTrue() {
        Recurso domain = new Recurso();
        domain.setTitulo("Test");
        domain.setTipo("PDF");
        domain.setDisponible(null);

        assertThat(mapper.toData(domain).getDisponible()).isTrue();
    }

    @Test
    @DisplayName("toData: disponible false se preserva")
    void toData_disponibleFalse_preserva() {
        Recurso domain = new Recurso();
        domain.setTitulo("Test");
        domain.setTipo("PDF");
        domain.setDisponible(false);

        assertThat(mapper.toData(domain).getDisponible()).isFalse();
    }

    @Test
    @DisplayName("round-trip: preserva datos clave")
    void roundTrip_preservaDatos() {
        RecursoData original = new RecursoData();
        original.setId(5L);
        original.setTitulo("Round Trip");
        original.setArea("Test");
        original.setTipo("EPUB");
        original.setDisponible(true);
        original.setCreadoEn(ahora);

        RecursoData resultado = mapper.toData(mapper.toDomain(original));

        assertThat(resultado.getId()).isEqualTo(original.getId());
        assertThat(resultado.getTitulo()).isEqualTo(original.getTitulo());
        assertThat(resultado.getTipo()).isEqualTo(original.getTipo());
    }
}
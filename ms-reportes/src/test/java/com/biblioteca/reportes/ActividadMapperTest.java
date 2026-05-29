package com.biblioteca.reportes;

import com.biblioteca.reportes.domain.model.RegistroActividad;
import com.biblioteca.reportes.infraestructure.driver_adapters.jpa_repository.ActividadData;
import com.biblioteca.reportes.infraestructure.mapper.ActividadMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ActividadMapper - Pruebas unitarias")
class ActividadMapperTest {

    private ActividadMapper mapper;
    private LocalDateTime ahora;

    @BeforeEach
    void setUp() {
        mapper = new ActividadMapper();
        ahora = LocalDateTime.now();
    }

    @Test
    @DisplayName("toDomain: mapea todos los campos correctamente")
    void toDomain_dataCompleta_mapeaCorrectamente() {
        ActividadData data = new ActividadData();
        data.setId(1L);
        data.setTipoEvento("DESCARGA");
        data.setEntidadId(5L);
        data.setEntidadTipo("RECURSO");
        data.setUsuarioId(2L);
        data.setUsuarioCorreo("u@t.com");
        data.setDetalle("Usuario descargó recurso");
        data.setOcurridoEn(ahora);

        RegistroActividad domain = mapper.toDomain(data);

        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getTipoEvento()).isEqualTo("DESCARGA");
        assertThat(domain.getEntidadId()).isEqualTo(5L);
        assertThat(domain.getEntidadTipo()).isEqualTo("RECURSO");
        assertThat(domain.getUsuarioId()).isEqualTo(2L);
        assertThat(domain.getUsuarioCorreo()).isEqualTo("u@t.com");
        assertThat(domain.getDetalle()).isEqualTo("Usuario descargó recurso");
        assertThat(domain.getOcurridoEn()).isEqualTo(ahora);
    }

    @Test
    @DisplayName("toData: mapea todos los campos correctamente")
    void toData_domainCompleto_mapeaCorrectamente() {
        RegistroActividad domain = new RegistroActividad(2L, "LOGIN", 3L, "USUARIO",
                4L, "x@y.com", "Sesión iniciada", ahora);

        ActividadData data = mapper.toData(domain);

        assertThat(data.getId()).isEqualTo(2L);
        assertThat(data.getTipoEvento()).isEqualTo("LOGIN");
        assertThat(data.getEntidadId()).isEqualTo(3L);
        assertThat(data.getEntidadTipo()).isEqualTo("USUARIO");
        assertThat(data.getUsuarioId()).isEqualTo(4L);
        assertThat(data.getUsuarioCorreo()).isEqualTo("x@y.com");
        assertThat(data.getDetalle()).isEqualTo("Sesión iniciada");
        assertThat(data.getOcurridoEn()).isEqualTo(ahora);
    }

    @Test
    @DisplayName("round-trip: toDomain → toData preserva todos los datos")
    void roundTrip_preservaDatos() {
        ActividadData original = new ActividadData();
        original.setId(5L);
        original.setTipoEvento("VALORACION");
        original.setEntidadId(10L);
        original.setEntidadTipo("RECURSO");
        original.setUsuarioId(7L);
        original.setUsuarioCorreo("rt@test.com");
        original.setDetalle("Calificó con 5 estrellas");
        original.setOcurridoEn(ahora);

        ActividadData resultado = mapper.toData(mapper.toDomain(original));

        assertThat(resultado.getId()).isEqualTo(original.getId());
        assertThat(resultado.getTipoEvento()).isEqualTo(original.getTipoEvento());
        assertThat(resultado.getEntidadId()).isEqualTo(original.getEntidadId());
        assertThat(resultado.getEntidadTipo()).isEqualTo(original.getEntidadTipo());
        assertThat(resultado.getUsuarioId()).isEqualTo(original.getUsuarioId());
        assertThat(resultado.getUsuarioCorreo()).isEqualTo(original.getUsuarioCorreo());
        assertThat(resultado.getDetalle()).isEqualTo(original.getDetalle());
        assertThat(resultado.getOcurridoEn()).isEqualTo(original.getOcurridoEn());
    }

    @Test
    @DisplayName("toDomain: campos opcionales null permanecen null")
    void toDomain_camposOpcionalesNull_permanecenNull() {
        ActividadData data = new ActividadData();
        data.setId(1L);
        data.setTipoEvento("EVENTO");

        RegistroActividad domain = mapper.toDomain(data);

        assertThat(domain.getEntidadId()).isNull();
        assertThat(domain.getEntidadTipo()).isNull();
        assertThat(domain.getUsuarioId()).isNull();
        assertThat(domain.getUsuarioCorreo()).isNull();
        assertThat(domain.getDetalle()).isNull();
        assertThat(domain.getOcurridoEn()).isNull();
    }

    @Test
    @DisplayName("toData: campos opcionales null permanecen null")
    void toData_camposOpcionalesNull_permanecenNull() {
        RegistroActividad domain = new RegistroActividad();
        domain.setId(1L);
        domain.setTipoEvento("EVENTO");

        ActividadData data = mapper.toData(domain);

        assertThat(data.getEntidadId()).isNull();
        assertThat(data.getEntidadTipo()).isNull();
        assertThat(data.getUsuarioId()).isNull();
        assertThat(data.getUsuarioCorreo()).isNull();
        assertThat(data.getDetalle()).isNull();
        assertThat(data.getOcurridoEn()).isNull();
    }
}
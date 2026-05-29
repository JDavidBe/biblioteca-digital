package com.biblioteca.valoraciones;

import com.biblioteca.valoraciones.domain.model.Valoracion;
import com.biblioteca.valoraciones.infraestructure.driver_adapters.jpa_repository.ValoracionData;
import com.biblioteca.valoraciones.infraestructure.mapper.ValoracionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ValoracionMapper - Pruebas unitarias")
class ValoracionMapperTest {

    private ValoracionMapper mapper;
    private LocalDateTime ahora;

    @BeforeEach
    void setUp() {
        mapper = new ValoracionMapper();
        ahora = LocalDateTime.now();
    }

    @Test
    @DisplayName("toDomain: mapea todos los campos correctamente")
    void toDomain_dataCompleta_mapeaCorrectamente() {
        ValoracionData data = new ValoracionData();
        data.setId(1L); data.setRecursoId(10L); data.setUsuarioId(2L);
        data.setUsuarioCorreo("u@t.com"); data.setPuntuacion(5);
        data.setComentario("Excelente"); data.setCreadoEn(ahora);

        Valoracion domain = mapper.toDomain(data);

        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getRecursoId()).isEqualTo(10L);
        assertThat(domain.getUsuarioId()).isEqualTo(2L);
        assertThat(domain.getUsuarioCorreo()).isEqualTo("u@t.com");
        assertThat(domain.getPuntuacion()).isEqualTo(5);
        assertThat(domain.getComentario()).isEqualTo("Excelente");
        assertThat(domain.getCreadoEn()).isEqualTo(ahora);
    }

    @Test
    @DisplayName("toData: mapea todos los campos correctamente")
    void toData_domainCompleto_mapeaCorrectamente() {
        Valoracion domain = new Valoracion(2L, 20L, 3L, "x@y.com", 4, "Bueno", ahora);

        ValoracionData data = mapper.toData(domain);

        assertThat(data.getId()).isEqualTo(2L);
        assertThat(data.getRecursoId()).isEqualTo(20L);
        assertThat(data.getUsuarioId()).isEqualTo(3L);
        assertThat(data.getUsuarioCorreo()).isEqualTo("x@y.com");
        assertThat(data.getPuntuacion()).isEqualTo(4);
        assertThat(data.getComentario()).isEqualTo("Bueno");
        assertThat(data.getCreadoEn()).isEqualTo(ahora);
    }

    @Test
    @DisplayName("toDomain: campos opcionales null se mapean como null")
    void toDomain_camposOpcionalesNull_permanecenNull() {
        ValoracionData data = new ValoracionData();
        data.setId(1L); data.setRecursoId(10L); data.setPuntuacion(4);

        Valoracion domain = mapper.toDomain(data);

        assertThat(domain.getUsuarioId()).isNull();
        assertThat(domain.getUsuarioCorreo()).isNull();
        assertThat(domain.getComentario()).isNull();
        assertThat(domain.getCreadoEn()).isNull();
    }

    @Test
    @DisplayName("toData: campos opcionales null se mapean como null")
    void toData_camposOpcionalesNull_permanecenNull() {
        Valoracion domain = new Valoracion();
        domain.setId(1L); domain.setRecursoId(10L); domain.setPuntuacion(3);

        ValoracionData data = mapper.toData(domain);

        assertThat(data.getUsuarioId()).isNull();
        assertThat(data.getUsuarioCorreo()).isNull();
        assertThat(data.getComentario()).isNull();
        assertThat(data.getCreadoEn()).isNull();
    }

    @Test
    @DisplayName("round-trip: preserva todos los datos")
    void roundTrip_preservaDatos() {
        ValoracionData original = new ValoracionData();
        original.setId(5L); original.setRecursoId(15L); original.setUsuarioId(7L);
        original.setPuntuacion(3); original.setComentario("Regular"); original.setCreadoEn(ahora);

        ValoracionData resultado = mapper.toData(mapper.toDomain(original));

        assertThat(resultado.getId()).isEqualTo(original.getId());
        assertThat(resultado.getRecursoId()).isEqualTo(original.getRecursoId());
        assertThat(resultado.getUsuarioId()).isEqualTo(original.getUsuarioId());
        assertThat(resultado.getPuntuacion()).isEqualTo(original.getPuntuacion());
        assertThat(resultado.getComentario()).isEqualTo(original.getComentario());
        assertThat(resultado.getCreadoEn()).isEqualTo(original.getCreadoEn());
    }
}
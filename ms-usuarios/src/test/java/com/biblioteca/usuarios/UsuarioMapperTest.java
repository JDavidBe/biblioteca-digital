package com.biblioteca.usuarios;

import com.biblioteca.usuarios.domain.model.Usuario;
import com.biblioteca.usuarios.infraestructure.driver_adapters.jpa_repository.UsuarioData;
import com.biblioteca.usuarios.infraestructure.mapper.UsuarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UsuarioMapper - Pruebas unitarias")
class UsuarioMapperTest {

    private UsuarioMapper mapper;
    private LocalDateTime ahora;

    @BeforeEach
    void setUp() {
        mapper = new UsuarioMapper();
        ahora = LocalDateTime.now();
    }

    @Test
    @DisplayName("toDomain: mapea todos los campos correctamente")
    void toDomain_dataCompleta_mapeaCorrectamente() {
        UsuarioData data = new UsuarioData();
        data.setId(1L); data.setNombre("Juan Pérez"); data.setCorreo("juan@test.com");
        data.setInstitucion("Universidad"); data.setGrado("10");
        data.setRol("ESTUDIANTE"); data.setActivo(true); data.setCreadoEn(ahora);

        Usuario domain = mapper.toDomain(data);

        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getNombre()).isEqualTo("Juan Pérez");
        assertThat(domain.getCorreo()).isEqualTo("juan@test.com");
        assertThat(domain.getInstitucion()).isEqualTo("Universidad");
        assertThat(domain.getGrado()).isEqualTo("10");
        assertThat(domain.getRol()).isEqualTo("ESTUDIANTE");
        assertThat(domain.getActivo()).isTrue();
        assertThat(domain.getCreadoEn()).isEqualTo(ahora);
    }

    @Test
    @DisplayName("toData: mapea todos los campos correctamente")
    void toData_domainCompleto_mapeaCorrectamente() {
        Usuario domain = new Usuario(2L, "Ana", "ana@test.com", "Col", "9", "DOCENTE", true, ahora);

        UsuarioData data = mapper.toData(domain);

        assertThat(data.getId()).isEqualTo(2L);
        assertThat(data.getNombre()).isEqualTo("Ana");
        assertThat(data.getCorreo()).isEqualTo("ana@test.com");
        assertThat(data.getInstitucion()).isEqualTo("Col");
        assertThat(data.getGrado()).isEqualTo("9");
        assertThat(data.getRol()).isEqualTo("DOCENTE");
        assertThat(data.getActivo()).isTrue();
        assertThat(data.getCreadoEn()).isEqualTo(ahora);
    }

    @Test
    @DisplayName("toData: rol null usa ESTUDIANTE por defecto")
    void toData_rolNull_usaEstudiante() {
        Usuario domain = new Usuario(3L, "Pedro", "p@b.com", null, null, null, true, null);
        assertThat(mapper.toData(domain).getRol()).isEqualTo("ESTUDIANTE");
    }

    @Test
    @DisplayName("toData: activo null usa true por defecto")
    void toData_activoNull_usaTrue() {
        Usuario domain = new Usuario(4L, "Luisa", "l@b.com", null, null, "ADMIN", null, null);
        assertThat(mapper.toData(domain).getActivo()).isTrue();
    }

    @Test
    @DisplayName("toData: activo false se preserva")
    void toData_activoFalse_preserva() {
        Usuario domain = new Usuario(5L, "Rosa", "r@b.com", null, null, "ESTUDIANTE", false, null);
        assertThat(mapper.toData(domain).getActivo()).isFalse();
    }

    @Test
    @DisplayName("toDomain: campos opcionales null permanecen null")
    void toDomain_camposOpcionalesNull_permanecenNull() {
        UsuarioData data = new UsuarioData();
        data.setId(1L); data.setNombre("Test"); data.setCorreo("t@t.com");
        data.setRol("ESTUDIANTE"); data.setActivo(true);

        Usuario domain = mapper.toDomain(data);

        assertThat(domain.getInstitucion()).isNull();
        assertThat(domain.getGrado()).isNull();
        assertThat(domain.getCreadoEn()).isNull();
    }

    @Test
    @DisplayName("round-trip: toDomain -> toData preserva datos")
    void roundTrip_preservaDatos() {
        UsuarioData original = new UsuarioData();
        original.setId(6L); original.setNombre("RoundTrip"); original.setCorreo("rt@test.com");
        original.setInstitucion("Inst"); original.setGrado("11");
        original.setRol("ADMIN"); original.setActivo(true); original.setCreadoEn(ahora);

        UsuarioData resultado = mapper.toData(mapper.toDomain(original));

        assertThat(resultado.getId()).isEqualTo(original.getId());
        assertThat(resultado.getNombre()).isEqualTo(original.getNombre());
        assertThat(resultado.getCorreo()).isEqualTo(original.getCorreo());
        assertThat(resultado.getRol()).isEqualTo(original.getRol());
        assertThat(resultado.getCreadoEn()).isEqualTo(original.getCreadoEn());
    }
}
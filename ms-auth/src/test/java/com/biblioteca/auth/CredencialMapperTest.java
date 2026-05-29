package com.biblioteca.auth;

import com.biblioteca.auth.domain.model.Credencial;
import com.biblioteca.auth.infraestructure.driver_adapters.jpa_repository.CredencialData;
import com.biblioteca.auth.infraestructure.mapper.CredencialMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CredencialMapper - Pruebas unitarias")
class CredencialMapperTest {

    private CredencialMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CredencialMapper();
    }

    @Test
    @DisplayName("toDomain: mapea todos los campos correctamente")
    void toDomain_dataCompleta_mapeaCorrectamente() {
        CredencialData data = new CredencialData();
        data.setId(1L);
        data.setCorreo("test@test.com");
        data.setPasswordHash("$hash$");
        data.setRol("ADMIN");
        data.setActivo(true);

        Credencial domain = mapper.toDomain(data);

        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getCorreo()).isEqualTo("test@test.com");
        assertThat(domain.getPasswordHash()).isEqualTo("$hash$");
        assertThat(domain.getRol()).isEqualTo("ADMIN");
        assertThat(domain.getActivo()).isTrue();
    }

    @Test
    @DisplayName("toData: mapea todos los campos correctamente")
    void toData_domainCompleto_mapeaCorrectamente() {
        Credencial domain = new Credencial(2L, "user@test.com", "$hash$", "ESTUDIANTE", true);

        CredencialData data = mapper.toData(domain);

        assertThat(data.getId()).isEqualTo(2L);
        assertThat(data.getCorreo()).isEqualTo("user@test.com");
        assertThat(data.getPasswordHash()).isEqualTo("$hash$");
        assertThat(data.getRol()).isEqualTo("ESTUDIANTE");
        assertThat(data.getActivo()).isTrue();
    }

    @Test
    @DisplayName("toData: activo null se convierte en true por defecto")
    void toData_activoNull_usaTrue() {
        Credencial domain = new Credencial(3L, "a@b.com", "$h$", "ESTUDIANTE", null);

        CredencialData data = mapper.toData(domain);

        assertThat(data.getActivo()).isTrue();
    }

    @Test
    @DisplayName("toData: activo false se preserva")
    void toData_activoFalse_preservaFalse() {
        Credencial domain = new Credencial(4L, "a@b.com", "$h$", "ESTUDIANTE", false);

        CredencialData data = mapper.toData(domain);

        assertThat(data.getActivo()).isFalse();
    }

    @Test
    @DisplayName("toDomain -> toData: round-trip preserva datos")
    void roundTrip_preservaDatos() {
        CredencialData original = new CredencialData();
        original.setId(5L);
        original.setCorreo("round@test.com");
        original.setPasswordHash("$rh$");
        original.setRol("DOCENTE");
        original.setActivo(true);

        Credencial domain = mapper.toDomain(original);
        CredencialData resultado = mapper.toData(domain);

        assertThat(resultado.getId()).isEqualTo(original.getId());
        assertThat(resultado.getCorreo()).isEqualTo(original.getCorreo());
        assertThat(resultado.getPasswordHash()).isEqualTo(original.getPasswordHash());
        assertThat(resultado.getRol()).isEqualTo(original.getRol());
        assertThat(resultado.getActivo()).isEqualTo(original.getActivo());
    }
}

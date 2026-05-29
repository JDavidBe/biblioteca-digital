package com.biblioteca.auth;

import com.biblioteca.auth.infraestructure.driver_adapters.jpa_repository.CredencialData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CredencialData - Pruebas unitarias")
class CredencialDataTest {

    @Test
    @DisplayName("getters y setters funcionan correctamente")
    void gettersSetters_funcionanCorrectamente() {
        CredencialData data = new CredencialData();
        data.setId(1L);
        data.setCorreo("user@test.com");
        data.setPasswordHash("$hash$");
        data.setRol("ESTUDIANTE");
        data.setActivo(true);

        assertThat(data.getId()).isEqualTo(1L);
        assertThat(data.getCorreo()).isEqualTo("user@test.com");
        assertThat(data.getPasswordHash()).isEqualTo("$hash$");
        assertThat(data.getRol()).isEqualTo("ESTUDIANTE");
        assertThat(data.getActivo()).isTrue();
    }

    @Test
    @DisplayName("valor por defecto de activo es true")
    void activoPorDefecto_esTrue() {
        CredencialData data = new CredencialData();
        assertThat(data.getActivo()).isTrue();
    }
}
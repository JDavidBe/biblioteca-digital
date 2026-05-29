package com.biblioteca.auth;

import com.biblioteca.auth.infraestructure.driver_adapters.jpa_repository.CredencialData;
import com.biblioteca.auth.infraestructure.driver_adapters.jpa_repository.CredencialJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CredencialJpaRepository - Pruebas de integración JPA")
class CredencialJpaRepositoryTest {

    @Autowired
    private CredencialJpaRepository repository;

    private CredencialData credencial;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        credencial = new CredencialData();
        credencial.setCorreo("user@test.com");
        credencial.setPasswordHash("$2a$12$hashEjemplo");
        credencial.setRol("ESTUDIANTE");
        credencial.setActivo(true);
        repository.save(credencial);
    }

    @Test
    @DisplayName("findByCorreo: retorna la entidad cuando el correo existe")
    void findByCorreo_existe_retornaEntidad() {
        Optional<CredencialData> resultado = repository.findByCorreo("user@test.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCorreo()).isEqualTo("user@test.com");
        assertThat(resultado.get().getRol()).isEqualTo("ESTUDIANTE");
    }

    @Test
    @DisplayName("findByCorreo: retorna vacío cuando el correo no existe")
    void findByCorreo_noExiste_retornaVacio() {
        Optional<CredencialData> resultado = repository.findByCorreo("noexiste@test.com");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("existsByCorreo: retorna true cuando el correo existe")
    void existsByCorreo_existe_retornaTrue() {
        assertThat(repository.existsByCorreo("user@test.com")).isTrue();
    }

    @Test
    @DisplayName("existsByCorreo: retorna false cuando el correo no existe")
    void existsByCorreo_noExiste_retornaFalse() {
        assertThat(repository.existsByCorreo("noexiste@test.com")).isFalse();
    }

    @Test
    @DisplayName("save: persiste y asigna ID automáticamente")
    void save_nuevaCredencial_asignaId() {
        CredencialData nueva = new CredencialData();
        nueva.setCorreo("nuevo@test.com");
        nueva.setPasswordHash("$2a$12$otroHash");
        nueva.setRol("ADMIN");
        nueva.setActivo(true);

        CredencialData guardada = repository.save(nueva);

        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getCorreo()).isEqualTo("nuevo@test.com");
    }

    @Test
    @DisplayName("delete: elimina la entidad correctamente")
    void delete_entidadExistente_eliminaCorrectamente() {
        Optional<CredencialData> antes = repository.findByCorreo("user@test.com");
        assertThat(antes).isPresent();

        repository.delete(antes.get());

        assertThat(repository.findByCorreo("user@test.com")).isEmpty();
        assertThat(repository.existsByCorreo("user@test.com")).isFalse();
    }
}
package com.biblioteca.auth;

import com.biblioteca.auth.domain.model.Credencial;
import com.biblioteca.auth.infraestructure.driver_adapters.jpa_repository.CredencialData;
import com.biblioteca.auth.infraestructure.driver_adapters.jpa_repository.CredencialGatewayImpl;
import com.biblioteca.auth.infraestructure.driver_adapters.jpa_repository.CredencialJpaRepository;
import com.biblioteca.auth.infraestructure.mapper.CredencialMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CredencialGatewayImpl - Pruebas unitarias")
class CredencialGatewayImplTest {

    @Mock
    private CredencialJpaRepository jpaRepository;

    @Mock
    private CredencialMapper mapper;

    @InjectMocks
    private CredencialGatewayImpl gateway;

    private CredencialData data;
    private Credencial domain;

    @BeforeEach
    void setUp() {
        data = new CredencialData();
        data.setId(1L);
        data.setCorreo("user@test.com");
        data.setPasswordHash("$hash$");
        data.setRol("ESTUDIANTE");
        data.setActivo(true);

        domain = new Credencial(1L, "user@test.com", "$hash$", "ESTUDIANTE", true);
    }


    @Test
    @DisplayName("buscarPorCorreo: retorna Credencial cuando existe")
    void buscarPorCorreo_existe_retornaCredencial() {
        when(jpaRepository.findByCorreo("user@test.com")).thenReturn(Optional.of(data));
        when(mapper.toDomain(data)).thenReturn(domain);

        Credencial resultado = gateway.buscarPorCorreo("user@test.com");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCorreo()).isEqualTo("user@test.com");
        verify(mapper).toDomain(data);
    }

    @Test
    @DisplayName("buscarPorCorreo: retorna null cuando no existe")
    void buscarPorCorreo_noExiste_retornaNull() {
        when(jpaRepository.findByCorreo("noexiste@test.com")).thenReturn(Optional.empty());

        Credencial resultado = gateway.buscarPorCorreo("noexiste@test.com");

        assertThat(resultado).isNull();
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("guardar: convierte, persiste y retorna el dominio mapeado")
    void guardar_credencialValida_guardaYRetorna() {
        when(mapper.toData(domain)).thenReturn(data);
        when(jpaRepository.save(data)).thenReturn(data);
        when(mapper.toDomain(data)).thenReturn(domain);

        Credencial resultado = gateway.guardar(domain);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(jpaRepository).save(data);
    }

    @Test
    @DisplayName("existePorCorreo: retorna true cuando el correo existe")
    void existePorCorreo_existe_retornaTrue() {
        when(jpaRepository.existsByCorreo("user@test.com")).thenReturn(true);

        assertThat(gateway.existePorCorreo("user@test.com")).isTrue();
    }

    @Test
    @DisplayName("existePorCorreo: retorna false cuando el correo no existe")
    void existePorCorreo_noExiste_retornaFalse() {
        when(jpaRepository.existsByCorreo("noexiste@test.com")).thenReturn(false);

        assertThat(gateway.existePorCorreo("noexiste@test.com")).isFalse();
    }

    @Test
    @DisplayName("eliminarPorCorreo: elimina cuando encuentra la entidad")
    void eliminarPorCorreo_existe_eliminaLaEntidad() {
        when(jpaRepository.findByCorreo("user@test.com")).thenReturn(Optional.of(data));

        gateway.eliminarPorCorreo("user@test.com");

        verify(jpaRepository).delete(data);
    }

    @Test
    @DisplayName("eliminarPorCorreo: no hace nada cuando no existe")
    void eliminarPorCorreo_noExiste_noLlamaDelete() {
        when(jpaRepository.findByCorreo("noexiste@test.com")).thenReturn(Optional.empty());

        gateway.eliminarPorCorreo("noexiste@test.com");

        verify(jpaRepository, never()).delete(any());
    }
}
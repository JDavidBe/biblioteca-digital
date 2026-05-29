package com.biblioteca.notificaciones;

import com.biblioteca.notificaciones.infraestructure.driver_adapters.jpa_repository.NotificacionData;
import com.biblioteca.notificaciones.infraestructure.driver_adapters.jpa_repository.NotificacionGatewayImpl;
import com.biblioteca.notificaciones.infraestructure.driver_adapters.jpa_repository.NotificacionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacionGatewayImpl - Pruebas unitarias")
class NotificacionGatewayImplTest {

    @Mock
    NotificacionJpaRepository repo;

    NotificacionGatewayImpl gateway;

    private NotificacionData sampleData() {
        NotificacionData d = new NotificacionData();
        d.setId(1L);
        d.setDestinatarioCorreo("user@test.com");
        d.setDestinatarioId(5L);
        d.setCanal("EMAIL");
        d.setTipo("GENERAL");
        d.setMensaje("Mensaje");
        d.setEnviada(false);
        d.setEnviadaSms(false);
        d.setCreadaEn(LocalDateTime.now());
        return d;
    }

    @BeforeEach
    void setUp() {
        gateway = new NotificacionGatewayImpl(repo);
    }

    @Test
    @DisplayName("guardar: delega en repo y retorna dominio mapeado")
    void guardar_delegaEnRepo() {
        when(repo.save(any())).thenReturn(sampleData());
        var result = gateway.guardar(new com.biblioteca.notificaciones.domain.model.Notificacion());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(repo).save(any());
    }

    @Test
    @DisplayName("buscarPorId: retorna dominio si existe")
    void buscarPorId_existe_retorna() {
        when(repo.findById(1L)).thenReturn(Optional.of(sampleData()));
        var result = gateway.buscarPorId(1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("buscarPorId: retorna null si no existe")
    void buscarPorId_noExiste_retornaNull() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThat(gateway.buscarPorId(99L)).isNull();
    }

    @Test
    @DisplayName("listarPorDestinatario: mapea lista correctamente")
    void listarPorDestinatario_retornaLista() {
        when(repo.findByDestinatarioId(5L)).thenReturn(List.of(sampleData()));
        var result = gateway.listarPorDestinatario(5L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDestinatarioId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("listarPendientes: retorna notificaciones no enviadas")
    void listarPendientes_retornaLista() {
        when(repo.findByEnviadaFalse()).thenReturn(List.of(sampleData()));
        assertThat(gateway.listarPendientes()).hasSize(1);
    }

    @Test
    @DisplayName("listarTodas: retorna todas las notificaciones")
    void listarTodas_retornaLista() {
        when(repo.findAll()).thenReturn(List.of(sampleData(), sampleData()));
        assertThat(gateway.listarTodas()).hasSize(2);
    }

    @Test
    @DisplayName("marcarComoEnviada: actualiza enviada=true y enviadaEn")
    void marcarComoEnviada_actualizaRegistro() {
        NotificacionData data = sampleData();
        when(repo.findById(1L)).thenReturn(Optional.of(data));
        when(repo.save(any())).thenReturn(data);

        gateway.marcarComoEnviada(1L);

        assertThat(data.getEnviada()).isTrue();
        assertThat(data.getEnviadaEn()).isNotNull();
        verify(repo).save(data);
    }

    @Test
    @DisplayName("marcarComoEnviada: no hace nada si no existe el id")
    void marcarComoEnviada_noExiste_noop() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        gateway.marcarComoEnviada(99L);
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("marcarSmsComotEnviado: actualiza enviadaSms=true")
    void marcarSmsComotEnviado_actualizaRegistro() {
        NotificacionData data = sampleData();
        when(repo.findById(1L)).thenReturn(Optional.of(data));
        when(repo.save(any())).thenReturn(data);

        gateway.marcarSmsComotEnviado(1L);

        assertThat(data.getEnviadaSms()).isTrue();
        assertThat(data.getEnviadaEn()).isNotNull();
        verify(repo).save(data);
    }

    @Test
    @DisplayName("marcarSmsComotEnviado: no sobreescribe enviadaEn si ya existe")
    void marcarSmsComotEnviado_noSobreescribeEnviadaEn() {
        NotificacionData data = sampleData();
        LocalDateTime yaExistente = LocalDateTime.of(2025, 1, 1, 10, 0);
        data.setEnviadaEn(yaExistente);
        when(repo.findById(1L)).thenReturn(Optional.of(data));
        when(repo.save(any())).thenReturn(data);

        gateway.marcarSmsComotEnviado(1L);

        assertThat(data.getEnviadaEn()).isEqualTo(yaExistente);
    }
}
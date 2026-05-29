package com.biblioteca.recursos;

import com.biblioteca.recursos.domain.model.Recurso;
import com.biblioteca.recursos.infraestructure.driver_adapters.jpa_repository.RecursoData;
import com.biblioteca.recursos.infraestructure.driver_adapters.jpa_repository.RecursoGatewayImpl;
import com.biblioteca.recursos.infraestructure.driver_adapters.jpa_repository.RecursoJpaRepository;
import com.biblioteca.recursos.infraestructure.mapper.RecursoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecursoGatewayImpl - Pruebas unitarias")
class RecursoGatewayImplTest {

    @Mock RecursoJpaRepository jpaRepository;
    RecursoMapper mapper = new RecursoMapper();
    RecursoGatewayImpl gateway;

    private RecursoData sampleData() {
        RecursoData d = new RecursoData();
        d.setId(1L); d.setTitulo("Java"); d.setArea("Tech");
        d.setTipo("PDF"); d.setDisponible(true);
        return d;
    }

    @BeforeEach
    void setUp() {
        gateway = new RecursoGatewayImpl(jpaRepository, mapper);
    }

    @Test
    @DisplayName("guardar: delega en repo y retorna dominio")
    void guardar_delegaEnRepo() {
        when(jpaRepository.save(any())).thenReturn(sampleData());
        Recurso r = new Recurso();
        r.setTitulo("Java"); r.setArea("Tech");
        assertThat(gateway.guardar(r)).isNotNull();
        verify(jpaRepository).save(any());
    }

    @Test
    @DisplayName("buscarPorId: retorna dominio si existe")
    void buscarPorId_existe_retorna() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(sampleData()));
        assertThat(gateway.buscarPorId(1L).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("buscarPorId: retorna null si no existe")
    void buscarPorId_noExiste_retornaNull() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(gateway.buscarPorId(99L)).isNull();
    }

    @Test
    @DisplayName("listarDisponibles: retorna lista mapeada")
    void listarDisponibles_retornaLista() {
        when(jpaRepository.findByDisponibleTrue()).thenReturn(List.of(sampleData()));
        assertThat(gateway.listarDisponibles()).hasSize(1);
    }

    @Test
    @DisplayName("buscar: retorna resultados del query")
    void buscar_retornaResultados() {
        when(jpaRepository.buscar("Tech", "10", "java")).thenReturn(List.of(sampleData()));
        assertThat(gateway.buscar("Tech", "10", "java")).hasSize(1);
    }

    @Test
    @DisplayName("listarPorUsuario: filtra por usuario")
    void listarPorUsuario_retornaLista() {
        when(jpaRepository.findBySubidoPorId(1L)).thenReturn(List.of(sampleData()));
        assertThat(gateway.listarPorUsuario(1L)).hasSize(1);
    }

    @Test
    @DisplayName("actualizar: actualiza campos no nulos")
    void actualizar_camposNoNulos_actualiza() {
        RecursoData data = sampleData();
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(data));
        when(jpaRepository.save(any())).thenReturn(data);

        Recurso cambios = new Recurso();
        cambios.setTitulo("Nuevo Titulo");
        cambios.setArea("Nueva Area");

        Recurso result = gateway.actualizar(1L, cambios);
        assertThat(result).isNotNull();
        assertThat(data.getTitulo()).isEqualTo("Nuevo Titulo");
    }

    @Test
    @DisplayName("actualizar: ignora campos null")
    void actualizar_camposNull_noSobreescribe() {
        RecursoData data = sampleData();
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(data));
        when(jpaRepository.save(any())).thenReturn(data);

        Recurso cambios = new Recurso();

        gateway.actualizar(1L, cambios);
        assertThat(data.getTitulo()).isEqualTo("Java");
    }

    @Test
    @DisplayName("eliminar: llama deleteById")
    void eliminar_llamaDeleteById() {
        gateway.eliminar(1L);
        verify(jpaRepository).deleteById(1L);
    }
}
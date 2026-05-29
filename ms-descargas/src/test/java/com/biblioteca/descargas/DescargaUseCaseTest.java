package com.biblioteca.descargas;

import com.biblioteca.descargas.domain.model.Descarga;
import com.biblioteca.descargas.domain.model.EstadisticaDescarga;
import com.biblioteca.descargas.domain.model.gateway.DescargaGateway;
import com.biblioteca.descargas.domain.usecase.DescargaUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DescargaUseCase - Pruebas unitarias")
class DescargaUseCaseTest {

    @Mock
    private DescargaGateway descargaGateway;

    @InjectMocks
    private DescargaUseCase descargaUseCase;

    private Descarga descargaBase;

    @BeforeEach
    void setUp() {
        descargaBase = new Descarga(1L, 5L, "Java Avanzado", 2L, "user@test.com",
                LocalDateTime.now(), "192.168.0.1");
    }

    @Test
    @DisplayName("registrarDescarga: asigna timestamp y delega en gateway")
    void registrarDescarga_datosValidos_registraConFecha() {
        Descarga nueva = new Descarga(null, 5L, "Java", 2L, "u@t.com", null, "10.0.0.1");
        when(descargaGateway.registrar(any())).thenReturn(descargaBase);

        Descarga resultado = descargaUseCase.registrarDescarga(nueva);

        assertThat(nueva.getDescargadoEn()).isNotNull();
        verify(descargaGateway).registrar(nueva);
    }

    @Test
    @DisplayName("registrarDescarga: retorna el objeto guardado por el gateway")
    void registrarDescarga_datosValidos_retornaObjetoGuardado() {
        Descarga nueva = new Descarga(null, 5L, "Java", 2L, "u@t.com", null, "10.0.0.1");
        when(descargaGateway.registrar(any())).thenAnswer(inv -> inv.getArgument(0));

        Descarga resultado = descargaUseCase.registrarDescarga(nueva);

        assertThat(resultado.getDescargadoEn()).isNotNull();
    }

    @Test
    @DisplayName("registrarDescarga: lanza excepción si recursoId es null")
    void registrarDescarga_recursoIdNull_lanzaExcepcion() {
        Descarga nueva = new Descarga(null, null, "Recurso", 2L, null, null, null);

        assertThatThrownBy(() -> descargaUseCase.registrarDescarga(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("recurso es obligatorio");
    }

    @Test
    @DisplayName("registrarDescarga: no llama al gateway si recursoId es null")
    void registrarDescarga_recursoIdNull_noLlamaGateway() {
        Descarga nueva = new Descarga();
        nueva.setRecursoId(null);

        assertThatThrownBy(() -> descargaUseCase.registrarDescarga(nueva))
                .isInstanceOf(RuntimeException.class);

        verify(descargaGateway, never()).registrar(any());
    }


    @Test
    @DisplayName("historialPorUsuario: delega en gateway y retorna lista")
    void historialPorUsuario_retornaLista() {
        when(descargaGateway.listarPorUsuario(2L)).thenReturn(List.of(descargaBase));

        List<Descarga> resultado = descargaUseCase.historialPorUsuario(2L);

        assertThat(resultado).hasSize(1);
        verify(descargaGateway).listarPorUsuario(2L);
    }

    @Test
    @DisplayName("historialPorUsuario: retorna lista vacía si no hay descargas")
    void historialPorUsuario_sinDescargas_retornaVacio() {
        when(descargaGateway.listarPorUsuario(99L)).thenReturn(List.of());

        assertThat(descargaUseCase.historialPorUsuario(99L)).isEmpty();
    }


    @Test
    @DisplayName("historialPorRecurso: delega en gateway y retorna lista")
    void historialPorRecurso_retornaLista() {
        when(descargaGateway.listarPorRecurso(5L)).thenReturn(List.of(descargaBase));

        List<Descarga> resultado = descargaUseCase.historialPorRecurso(5L);

        assertThat(resultado).hasSize(1);
        verify(descargaGateway).listarPorRecurso(5L);
    }

    @Test
    @DisplayName("historialPorRecurso: retorna lista vacía si no hay descargas")
    void historialPorRecurso_sinDescargas_retornaVacio() {
        when(descargaGateway.listarPorRecurso(99L)).thenReturn(List.of());

        assertThat(descargaUseCase.historialPorRecurso(99L)).isEmpty();
    }


    @Test
    @DisplayName("descargasRecientes: usa el límite proporcionado")
    void descargasRecientes_conLimite_usaLimiteProporcionado() {
        when(descargaGateway.listarRecientes(10)).thenReturn(List.of(descargaBase));

        List<Descarga> resultado = descargaUseCase.descargasRecientes(10);

        assertThat(resultado).hasSize(1);
        verify(descargaGateway).listarRecientes(10);
    }

    @Test
    @DisplayName("descargasRecientes: usa 20 por defecto cuando límite es null")
    void descargasRecientes_limiteNull_usa20PorDefecto() {
        when(descargaGateway.listarRecientes(20)).thenReturn(List.of());

        descargaUseCase.descargasRecientes(null);

        verify(descargaGateway).listarRecientes(20);
    }

    @Test
    @DisplayName("topMasDescargados: usa el límite proporcionado y retorna estadísticas")
    void topMasDescargados_conLimite_retornaEstadisticas() {
        EstadisticaDescarga estadistica = new EstadisticaDescarga(5L, "Java", 100L);
        when(descargaGateway.topRecursosMasDescargados(5)).thenReturn(List.of(estadistica));

        List<EstadisticaDescarga> resultado = descargaUseCase.topMasDescargados(5);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRecursoId()).isEqualTo(5L);
        assertThat(resultado.get(0).getTotalDescargas()).isEqualTo(100L);
        verify(descargaGateway).topRecursosMasDescargados(5);
    }

    @Test
    @DisplayName("topMasDescargados: usa 10 por defecto cuando límite es null")
    void topMasDescargados_limiteNull_usa10PorDefecto() {
        when(descargaGateway.topRecursosMasDescargados(10)).thenReturn(List.of());

        descargaUseCase.topMasDescargados(null);

        verify(descargaGateway).topRecursosMasDescargados(10);
    }

    @Test
    @DisplayName("topMasDescargados: retorna lista vacía cuando no hay datos")
    void topMasDescargados_sinDatos_retornaVacio() {
        when(descargaGateway.topRecursosMasDescargados(3)).thenReturn(List.of());

        assertThat(descargaUseCase.topMasDescargados(3)).isEmpty();
    }


    @Test
    @DisplayName("contarPorRecurso: retorna el conteo correcto del gateway")
    void contarPorRecurso_retornaConteo() {
        when(descargaGateway.contarPorRecurso(5L)).thenReturn(42L);

        assertThat(descargaUseCase.contarPorRecurso(5L)).isEqualTo(42L);
        verify(descargaGateway).contarPorRecurso(5L);
    }

    @Test
    @DisplayName("contarPorRecurso: retorna 0 cuando no hay descargas")
    void contarPorRecurso_sinDescargas_retornaCero() {
        when(descargaGateway.contarPorRecurso(99L)).thenReturn(0L);

        assertThat(descargaUseCase.contarPorRecurso(99L)).isZero();
    }

    @Test
    @DisplayName("contarPorUsuario: retorna el conteo correcto del gateway")
    void contarPorUsuario_retornaConteo() {
        when(descargaGateway.contarPorUsuario(2L)).thenReturn(7L);

        assertThat(descargaUseCase.contarPorUsuario(2L)).isEqualTo(7L);
        verify(descargaGateway).contarPorUsuario(2L);
    }

    @Test
    @DisplayName("contarPorUsuario: retorna 0 cuando no hay descargas")
    void contarPorUsuario_sinDescargas_retornaCero() {
        when(descargaGateway.contarPorUsuario(99L)).thenReturn(0L);

        assertThat(descargaUseCase.contarPorUsuario(99L)).isZero();
    }
}
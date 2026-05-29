package com.biblioteca.valoraciones;

import com.biblioteca.valoraciones.domain.model.ResumenValoracion;
import com.biblioteca.valoraciones.domain.model.Valoracion;
import com.biblioteca.valoraciones.domain.model.gateway.ValoracionGateway;
import com.biblioteca.valoraciones.domain.usecase.ValoracionUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValoracionUseCase - Pruebas unitarias")
class ValoracionUseCaseTest {

    @Mock
    private ValoracionGateway valoracionGateway;

    @InjectMocks
    private ValoracionUseCase valoracionUseCase;

    private Valoracion valoracionBase;

    @BeforeEach
    void setUp() {
        valoracionBase = new Valoracion(1L, 10L, 2L, "user@test.com", 4, "Buen recurso", LocalDateTime.now());
    }

    @Test
    @DisplayName("calificar: guarda con timestamp cuando datos son válidos")
    void calificar_datosValidos_guardaConTimestamp() {
        Valoracion nueva = new Valoracion(null, 10L, 2L, "u@t.com", 5, "Excelente", null);
        when(valoracionGateway.buscarPorRecursoYUsuario(10L, 2L)).thenReturn(Optional.empty());
        when(valoracionGateway.guardar(any())).thenReturn(valoracionBase);

        valoracionUseCase.calificar(nueva);

        assertThat(nueva.getCreadoEn()).isNotNull();
        assertThat(nueva.getCreadoEn()).isBeforeOrEqualTo(LocalDateTime.now());
        verify(valoracionGateway).guardar(nueva);
    }

    @Test
    @DisplayName("calificar: lanza excepción si recursoId es null")
    void calificar_recursoIdNull_lanzaExcepcion() {
        Valoracion nueva = new Valoracion(null, null, 2L, null, 3, null, null);
        assertThatThrownBy(() -> valoracionUseCase.calificar(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("recurso es obligatorio");
        verifyNoInteractions(valoracionGateway);
    }

    @Test
    @DisplayName("calificar: lanza excepción si puntuación es null")
    void calificar_puntuacionNull_lanzaExcepcion() {
        Valoracion nueva = new Valoracion(null, 10L, 2L, null, null, null, null);
        assertThatThrownBy(() -> valoracionUseCase.calificar(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("puntuación debe estar entre 1 y 5");
    }

    @Test
    @DisplayName("calificar: lanza excepción si puntuación es 0")
    void calificar_puntuacionCero_lanzaExcepcion() {
        Valoracion nueva = new Valoracion(null, 10L, 2L, null, 0, null, null);
        assertThatThrownBy(() -> valoracionUseCase.calificar(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("puntuación debe estar entre 1 y 5");
    }

    @Test
    @DisplayName("calificar: lanza excepción si puntuación es 6")
    void calificar_puntuacionSeis_lanzaExcepcion() {
        Valoracion nueva = new Valoracion(null, 10L, 2L, null, 6, null, null);
        assertThatThrownBy(() -> valoracionUseCase.calificar(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("puntuación debe estar entre 1 y 5");
    }

    @Test
    @DisplayName("calificar: lanza excepción si ya calificó el recurso")
    void calificar_yaCalificado_lanzaExcepcion() {
        Valoracion nueva = new Valoracion(null, 10L, 2L, null, 4, null, null);
        when(valoracionGateway.buscarPorRecursoYUsuario(10L, 2L)).thenReturn(Optional.of(valoracionBase));

        assertThatThrownBy(() -> valoracionUseCase.calificar(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya calificaste este recurso");
        verify(valoracionGateway, never()).guardar(any());
    }

    @Test
    @DisplayName("calificar: acepta puntuación mínima (1)")
    void calificar_puntuacionMinima_guarda() {
        Valoracion nueva = new Valoracion(null, 10L, 2L, null, 1, null, null);
        when(valoracionGateway.buscarPorRecursoYUsuario(10L, 2L)).thenReturn(Optional.empty());
        when(valoracionGateway.guardar(any())).thenReturn(nueva);

        assertThatCode(() -> valoracionUseCase.calificar(nueva)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("calificar: acepta puntuación máxima (5)")
    void calificar_puntuacionMaxima_guarda() {
        Valoracion nueva = new Valoracion(null, 10L, 2L, null, 5, null, null);
        when(valoracionGateway.buscarPorRecursoYUsuario(10L, 2L)).thenReturn(Optional.empty());
        when(valoracionGateway.guardar(any())).thenReturn(nueva);

        assertThatCode(() -> valoracionUseCase.calificar(nueva)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("obtenerPorRecurso: delega en gateway y retorna lista")
    void obtenerPorRecurso_retornaLista() {
        when(valoracionGateway.listarPorRecurso(10L)).thenReturn(List.of(valoracionBase));
        assertThat(valoracionUseCase.obtenerPorRecurso(10L)).hasSize(1);
        verify(valoracionGateway).listarPorRecurso(10L);
    }

    @Test
    @DisplayName("obtenerPorRecurso: retorna lista vacía")
    void obtenerPorRecurso_sinResultados_retornaVacia() {
        when(valoracionGateway.listarPorRecurso(99L)).thenReturn(List.of());
        assertThat(valoracionUseCase.obtenerPorRecurso(99L)).isEmpty();
    }

    @Test
    @DisplayName("obtenerPorUsuario: delega en gateway y retorna lista")
    void obtenerPorUsuario_retornaLista() {
        when(valoracionGateway.listarPorUsuario(2L)).thenReturn(List.of(valoracionBase));
        assertThat(valoracionUseCase.obtenerPorUsuario(2L)).hasSize(1);
        verify(valoracionGateway).listarPorUsuario(2L);
    }

    @Test
    @DisplayName("obtenerPorUsuario: retorna lista vacía")
    void obtenerPorUsuario_sinResultados_retornaVacia() {
        when(valoracionGateway.listarPorUsuario(99L)).thenReturn(List.of());
        assertThat(valoracionUseCase.obtenerPorUsuario(99L)).isEmpty();
    }

    @Test
    @DisplayName("resumenPorRecurso: calcula resumen correctamente")
    void resumenPorRecurso_conDatos_calculaResumen() {
        when(valoracionGateway.calcularPromedio(10L)).thenReturn(4.2);
        when(valoracionGateway.contarPorRecurso(10L)).thenReturn(5L);

        ResumenValoracion resumen = valoracionUseCase.resumenPorRecurso(10L);

        assertThat(resumen.getRecursoId()).isEqualTo(10L);
        assertThat(resumen.getPromedio()).isEqualTo(4.2);
        assertThat(resumen.getTotalValoraciones()).isEqualTo(5L);
    }

    @Test
    @DisplayName("resumenPorRecurso: promedio null devuelve 0.0")
    void resumenPorRecurso_promedioNull_devuelveCero() {
        when(valoracionGateway.calcularPromedio(10L)).thenReturn(null);
        when(valoracionGateway.contarPorRecurso(10L)).thenReturn(0L);

        assertThat(valoracionUseCase.resumenPorRecurso(10L).getPromedio()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("eliminarValoracion: delega en gateway")
    void eliminarValoracion_llamaGateway() {
        valoracionUseCase.eliminarValoracion(1L);
        verify(valoracionGateway).eliminar(1L);
    }
}
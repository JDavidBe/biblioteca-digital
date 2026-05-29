package com.biblioteca.reportes;

import com.biblioteca.reportes.domain.model.RegistroActividad;
import com.biblioteca.reportes.domain.model.ResumenGeneral;
import com.biblioteca.reportes.domain.model.gateway.ActividadGateway;
import com.biblioteca.reportes.domain.usecase.ReporteUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReporteUseCase - Pruebas unitarias")
class ReporteUseCaseTest {

    @Mock
    private ActividadGateway actividadGateway;

    @InjectMocks
    private ReporteUseCase reporteUseCase;

    private RegistroActividad actividadBase;

    @BeforeEach
    void setUp() {
        actividadBase = new RegistroActividad(1L, "DESCARGA", 5L, "RECURSO",
                2L, "user@test.com", "Descargó recurso", LocalDateTime.now());
    }


    @Test
    @DisplayName("registrarEvento: registra actividad y asigna fecha actual")
    void registrarEvento_datosValidos_registraConFecha() {
        RegistroActividad nueva = new RegistroActividad(null, "DESCARGA", 5L, "RECURSO",
                2L, "u@t.com", "Desc", null);
        when(actividadGateway.registrar(any())).thenReturn(actividadBase);

        RegistroActividad resultado = reporteUseCase.registrarEvento(nueva);

        assertThat(nueva.getOcurridoEn()).isNotNull();
        verify(actividadGateway).registrar(nueva);
        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("registrarEvento: asigna timestamp justo antes de delegar al gateway")
    void registrarEvento_asignaTimestampAntesDeGuardar() {
        RegistroActividad a = new RegistroActividad();
        a.setTipoEvento("LOGIN");
        when(actividadGateway.registrar(any())).thenAnswer(inv -> inv.getArgument(0));

        RegistroActividad resultado = reporteUseCase.registrarEvento(a);

        assertThat(resultado.getOcurridoEn()).isNotNull();
        assertThat(resultado.getOcurridoEn()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("registrarEvento: lanza excepción si tipoEvento es null")
    void registrarEvento_tipoEventoNull_lanzaExcepcion() {
        RegistroActividad nueva = new RegistroActividad(null, null, 5L, "RECURSO",
                2L, null, null, null);

        assertThatThrownBy(() -> reporteUseCase.registrarEvento(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("tipo de evento es obligatorio");

        verifyNoInteractions(actividadGateway);
    }

    @Test
    @DisplayName("registrarEvento: lanza excepción si tipoEvento está en blanco")
    void registrarEvento_tipoEventoBlanco_lanzaExcepcion() {
        RegistroActividad nueva = new RegistroActividad(null, "   ", 5L, "RECURSO",
                2L, null, null, null);

        assertThatThrownBy(() -> reporteUseCase.registrarEvento(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("tipo de evento es obligatorio");

        verifyNoInteractions(actividadGateway);
    }

    @Test
    @DisplayName("registrarEvento: lanza excepción si tipoEvento es string vacío")
    void registrarEvento_tipoEventoVacio_lanzaExcepcion() {
        RegistroActividad nueva = new RegistroActividad();
        nueva.setTipoEvento("");

        assertThatThrownBy(() -> reporteUseCase.registrarEvento(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("tipo de evento es obligatorio");
    }


    @Test
    @DisplayName("actividadPorTipo: delega en gateway y retorna lista")
    void actividadPorTipo_retornaLista() {
        when(actividadGateway.listarPorTipo("DESCARGA")).thenReturn(List.of(actividadBase));

        List<RegistroActividad> resultado = reporteUseCase.actividadPorTipo("DESCARGA");

        assertThat(resultado).hasSize(1);
        verify(actividadGateway).listarPorTipo("DESCARGA");
    }

    @Test
    @DisplayName("actividadPorTipo: retorna lista vacía cuando no hay coincidencias")
    void actividadPorTipo_sinCoincidencias_retornaListaVacia() {
        when(actividadGateway.listarPorTipo("INEXISTENTE")).thenReturn(List.of());

        assertThat(reporteUseCase.actividadPorTipo("INEXISTENTE")).isEmpty();
    }


    @Test
    @DisplayName("actividadPorUsuario: delega en gateway y retorna lista")
    void actividadPorUsuario_retornaLista() {
        when(actividadGateway.listarPorUsuario(2L)).thenReturn(List.of(actividadBase));

        List<RegistroActividad> resultado = reporteUseCase.actividadPorUsuario(2L);

        assertThat(resultado).hasSize(1);
        verify(actividadGateway).listarPorUsuario(2L);
    }

    @Test
    @DisplayName("actividadPorUsuario: retorna lista vacía si usuario sin actividad")
    void actividadPorUsuario_sinActividad_retornaListaVacia() {
        when(actividadGateway.listarPorUsuario(999L)).thenReturn(List.of());

        assertThat(reporteUseCase.actividadPorUsuario(999L)).isEmpty();
    }

    @Test
    @DisplayName("actividadEnPeriodo: usa las fechas proporcionadas")
    void actividadEnPeriodo_conFechas_usaFechasProporcionadas() {
        LocalDateTime desde = LocalDateTime.now().minusDays(7);
        LocalDateTime hasta = LocalDateTime.now();
        when(actividadGateway.listarEntreFechas(desde, hasta)).thenReturn(List.of(actividadBase));

        List<RegistroActividad> resultado = reporteUseCase.actividadEnPeriodo(desde, hasta);

        assertThat(resultado).hasSize(1);
        verify(actividadGateway).listarEntreFechas(desde, hasta);
    }

    @Test
    @DisplayName("actividadEnPeriodo: usa defaults cuando ambas fechas son null")
    void actividadEnPeriodo_ambasFechasNull_usaDefaults() {
        when(actividadGateway.listarEntreFechas(any(), any())).thenReturn(List.of());

        reporteUseCase.actividadEnPeriodo(null, null);

        ArgumentCaptor<LocalDateTime> desdeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> hastaCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(actividadGateway).listarEntreFechas(desdeCaptor.capture(), hastaCaptor.capture());

        assertThat(desdeCaptor.getValue()).isBeforeOrEqualTo(LocalDateTime.now().minusDays(29));
        assertThat(hastaCaptor.getValue()).isAfterOrEqualTo(LocalDateTime.now().minusSeconds(2));
    }

    @Test
    @DisplayName("actividadEnPeriodo: desde null usa hace 1 mes, hasta se respeta")
    void actividadEnPeriodo_desdeNull_usaHaceUnMes() {
        LocalDateTime hasta = LocalDateTime.now();
        when(actividadGateway.listarEntreFechas(any(), eq(hasta))).thenReturn(List.of());

        reporteUseCase.actividadEnPeriodo(null, hasta);

        ArgumentCaptor<LocalDateTime> desdeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(actividadGateway).listarEntreFechas(desdeCaptor.capture(), eq(hasta));
        assertThat(desdeCaptor.getValue()).isBeforeOrEqualTo(LocalDateTime.now().minusDays(29));
    }

    @Test
    @DisplayName("actividadEnPeriodo: hasta null usa ahora, desde se respeta")
    void actividadEnPeriodo_hastaNull_usaAhora() {
        LocalDateTime desde = LocalDateTime.now().minusDays(3);
        when(actividadGateway.listarEntreFechas(eq(desde), any())).thenReturn(List.of());

        reporteUseCase.actividadEnPeriodo(desde, null);

        ArgumentCaptor<LocalDateTime> hastaCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(actividadGateway).listarEntreFechas(eq(desde), hastaCaptor.capture());
        assertThat(hastaCaptor.getValue()).isAfterOrEqualTo(LocalDateTime.now().minusSeconds(2));
    }

    @Test
    @DisplayName("actividadReciente: usa el límite proporcionado")
    void actividadReciente_conLimite_usaLimiteProporcionado() {
        when(actividadGateway.listarRecientes(25)).thenReturn(List.of(actividadBase));

        List<RegistroActividad> resultado = reporteUseCase.actividadReciente(25);

        assertThat(resultado).hasSize(1);
        verify(actividadGateway).listarRecientes(25);
    }

    @Test
    @DisplayName("actividadReciente: usa 50 por defecto si límite es null")
    void actividadReciente_limiteNull_usa50PorDefecto() {
        when(actividadGateway.listarRecientes(50)).thenReturn(List.of());

        reporteUseCase.actividadReciente(null);

        verify(actividadGateway).listarRecientes(50);
    }

    @Test
    @DisplayName("actividadReciente: retorna múltiples registros")
    void actividadReciente_variosRegistros_retornaTodos() {
        RegistroActividad segunda = new RegistroActividad(2L, "LOGIN", null, null,
                3L, "b@t.com", null, LocalDateTime.now());
        when(actividadGateway.listarRecientes(10)).thenReturn(List.of(actividadBase, segunda));

        assertThat(reporteUseCase.actividadReciente(10)).hasSize(2);
    }

    @DisplayName("resumenGeneral: delega en gateway y retorna todos los campos")
    void resumenGeneral_retornaResumenCompleto() {
        ResumenGeneral resumen = new ResumenGeneral(100L, 5L, 20L, 80L, "DESCARGA");
        when(actividadGateway.obtenerResumen()).thenReturn(resumen);

        ResumenGeneral resultado = reporteUseCase.resumenGeneral();

        assertThat(resultado.getTotalEventos()).isEqualTo(100L);
        assertThat(resultado.getEventosHoy()).isEqualTo(5L);
        assertThat(resultado.getEventosSemana()).isEqualTo(20L);
        assertThat(resultado.getEventosMes()).isEqualTo(80L);
        assertThat(resultado.getEventoMasFrecuente()).isEqualTo("DESCARGA");
        verify(actividadGateway).obtenerResumen();
    }

    @Test
    @DisplayName("resumenGeneral: retorna la misma instancia del gateway")
    void resumenGeneral_retornaMismaInstancia() {
        ResumenGeneral resumen = new ResumenGeneral();
        when(actividadGateway.obtenerResumen()).thenReturn(resumen);

        assertThat(reporteUseCase.resumenGeneral()).isSameAs(resumen);
    }
}
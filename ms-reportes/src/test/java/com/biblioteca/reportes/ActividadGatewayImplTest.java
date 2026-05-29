package com.biblioteca.reportes.infraestructure.driver_adapters.jpa_repository;

import com.biblioteca.reportes.domain.model.RegistroActividad;
import com.biblioteca.reportes.domain.model.ResumenGeneral;
import com.biblioteca.reportes.infraestructure.mapper.ActividadMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ActividadGatewayImpl.class, ActividadMapper.class})
@DisplayName("ActividadGatewayImpl - Pruebas de integración con H2")
class ActividadGatewayImplTest {

    @Autowired
    ActividadGatewayImpl actividadGateway;

    @Autowired
    ActividadJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }

    private RegistroActividad actividad(String tipo, Long usuarioId) {
        RegistroActividad a = new RegistroActividad();
        a.setTipoEvento(tipo);
        a.setEntidadId(1L);
        a.setEntidadTipo("RECURSO");
        a.setUsuarioId(usuarioId);
        a.setUsuarioCorreo("u@t.com");
        a.setDetalle("detalle");
        a.setOcurridoEn(LocalDateTime.now());
        return a;
    }

    @Test
    @DisplayName("registrar: guarda y retorna actividad con id generado")
    void registrar_guardaConId() {
        RegistroActividad resultado = actividadGateway.registrar(actividad("DESCARGA", 1L));
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getTipoEvento()).isEqualTo("DESCARGA");
    }

    @Test
    @DisplayName("listarPorTipo: retorna solo del tipo indicado")
    void listarPorTipo_filtraCorrectamente() {
        actividadGateway.registrar(actividad("DESCARGA", 1L));
        actividadGateway.registrar(actividad("LOGIN", 2L));

        List<RegistroActividad> resultado = actividadGateway.listarPorTipo("DESCARGA");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTipoEvento()).isEqualTo("DESCARGA");
    }

    @Test
    @DisplayName("listarPorUsuario: retorna solo del usuario indicado")
    void listarPorUsuario_filtraCorrectamente() {
        actividadGateway.registrar(actividad("DESCARGA", 1L));
        actividadGateway.registrar(actividad("LOGIN", 2L));

        List<RegistroActividad> resultado = actividadGateway.listarPorUsuario(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsuarioId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("listarEntreFechas: retorna actividades dentro del periodo")
    void listarEntreFechas_retornaEnPeriodo() {
        actividadGateway.registrar(actividad("DESCARGA", 1L));

        LocalDateTime desde = LocalDateTime.now().minusMinutes(1);
        LocalDateTime hasta = LocalDateTime.now().plusMinutes(1);

        List<RegistroActividad> resultado = actividadGateway.listarEntreFechas(desde, hasta);

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("listarEntreFechas: retorna vacío si está fuera del periodo")
    void listarEntreFechas_fueraDelPeriodo_retornaVacio() {
        actividadGateway.registrar(actividad("DESCARGA", 1L));

        LocalDateTime desde = LocalDateTime.now().plusHours(1);
        LocalDateTime hasta = LocalDateTime.now().plusHours(2);

        List<RegistroActividad> resultado = actividadGateway.listarEntreFechas(desde, hasta);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("listarRecientes: respeta el límite indicado")
    void listarRecientes_respetaLimite() {
        actividadGateway.registrar(actividad("DESCARGA", 1L));
        actividadGateway.registrar(actividad("LOGIN", 2L));
        actividadGateway.registrar(actividad("VALORACION", 3L));

        List<RegistroActividad> resultado = actividadGateway.listarRecientes(2);

        assertThat(resultado).hasSize(2);
    }

    @Test
    @DisplayName("listarRecientes: retorna todos si límite es mayor que total")
    void listarRecientes_limiteMayorQueTotal_retornaTodos() {
        actividadGateway.registrar(actividad("DESCARGA", 1L));
        actividadGateway.registrar(actividad("LOGIN", 2L));

        List<RegistroActividad> resultado = actividadGateway.listarRecientes(50);

        assertThat(resultado).hasSize(2);
    }

    @Test
    @DisplayName("obtenerResumen: totalEventos cuenta todos los registros")
    void obtenerResumen_totalCorrecto() {
        actividadGateway.registrar(actividad("DESCARGA", 1L));
        actividadGateway.registrar(actividad("DESCARGA", 2L));
        actividadGateway.registrar(actividad("LOGIN", 3L));

        ResumenGeneral resumen = actividadGateway.obtenerResumen();

        assertThat(resumen.getTotalEventos()).isEqualTo(3L);
        assertThat(resumen.getEventosHoy()).isEqualTo(3L);
        assertThat(resumen.getEventosSemana()).isEqualTo(3L);
        assertThat(resumen.getEventosMes()).isEqualTo(3L);
        assertThat(resumen.getEventoMasFrecuente()).isEqualTo("DESCARGA");
    }

    @Test
    @DisplayName("obtenerResumen: eventoMasFrecuente es N/A si no hay registros")
    void obtenerResumen_sinRegistros_masFrecuenteEsNA() {
        ResumenGeneral resumen = actividadGateway.obtenerResumen();

        assertThat(resumen.getTotalEventos()).isEqualTo(0L);
        assertThat(resumen.getEventoMasFrecuente()).isEqualTo("N/A");
    }
}
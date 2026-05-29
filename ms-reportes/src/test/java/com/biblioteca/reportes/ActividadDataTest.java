package com.biblioteca.reportes;

import com.biblioteca.reportes.infraestructure.driver_adapters.jpa_repository.ActividadData;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class ActividadDataTest {

    @Test
    void gettersSetters_funcionanCorrectamente() {
        ActividadData data = new ActividadData();
        LocalDateTime ahora = LocalDateTime.now();

        data.setTipoEvento("DESCARGA");
        data.setEntidadId(1L);
        data.setEntidadTipo("RECURSO");
        data.setUsuarioId(2L);
        data.setUsuarioCorreo("u@t.com");
        data.setDetalle("detalle");
        data.setOcurridoEn(ahora);

        assertThat(data.getTipoEvento()).isEqualTo("DESCARGA");
        assertThat(data.getEntidadId()).isEqualTo(1L);
        assertThat(data.getEntidadTipo()).isEqualTo("RECURSO");
        assertThat(data.getUsuarioId()).isEqualTo(2L);
        assertThat(data.getUsuarioCorreo()).isEqualTo("u@t.com");
        assertThat(data.getDetalle()).isEqualTo("detalle");
        assertThat(data.getOcurridoEn()).isEqualTo(ahora);
    }
}
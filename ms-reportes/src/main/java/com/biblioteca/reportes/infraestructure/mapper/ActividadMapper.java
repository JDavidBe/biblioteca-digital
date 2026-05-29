package com.biblioteca.reportes.infraestructure.mapper;

import com.biblioteca.reportes.domain.model.RegistroActividad;
import com.biblioteca.reportes.infraestructure.driver_adapters.jpa_repository.ActividadData;
import org.springframework.stereotype.Component;

@Component
public class ActividadMapper {

    public RegistroActividad toDomain(ActividadData data) {
        RegistroActividad r = new RegistroActividad();
        r.setId(data.getId());
        r.setTipoEvento(data.getTipoEvento());
        r.setEntidadId(data.getEntidadId());
        r.setEntidadTipo(data.getEntidadTipo());
        r.setUsuarioId(data.getUsuarioId());
        r.setUsuarioCorreo(data.getUsuarioCorreo());
        r.setDetalle(data.getDetalle());
        r.setOcurridoEn(data.getOcurridoEn());
        return r;
    }

    public ActividadData toData(RegistroActividad domain) {
        ActividadData d = new ActividadData();
        d.setId(domain.getId());
        d.setTipoEvento(domain.getTipoEvento());
        d.setEntidadId(domain.getEntidadId());
        d.setEntidadTipo(domain.getEntidadTipo());
        d.setUsuarioId(domain.getUsuarioId());
        d.setUsuarioCorreo(domain.getUsuarioCorreo());
        d.setDetalle(domain.getDetalle());
        d.setOcurridoEn(domain.getOcurridoEn());
        return d;
    }
}

package com.biblioteca.valoraciones.infraestructure.mapper;

import com.biblioteca.valoraciones.domain.model.Valoracion;
import com.biblioteca.valoraciones.infraestructure.driver_adapters.jpa_repository.ValoracionData;
import org.springframework.stereotype.Component;

@Component
public class ValoracionMapper {

    public Valoracion toDomain(ValoracionData data) {
        Valoracion v = new Valoracion();
        v.setId(data.getId());
        v.setRecursoId(data.getRecursoId());
        v.setUsuarioId(data.getUsuarioId());
        v.setUsuarioCorreo(data.getUsuarioCorreo());
        v.setPuntuacion(data.getPuntuacion());
        v.setComentario(data.getComentario());
        v.setCreadoEn(data.getCreadoEn());
        return v;
    }

    public ValoracionData toData(Valoracion domain) {
        ValoracionData d = new ValoracionData();
        d.setId(domain.getId());
        d.setRecursoId(domain.getRecursoId());
        d.setUsuarioId(domain.getUsuarioId());
        d.setUsuarioCorreo(domain.getUsuarioCorreo());
        d.setPuntuacion(domain.getPuntuacion());
        d.setComentario(domain.getComentario());
        d.setCreadoEn(domain.getCreadoEn());
        return d;
    }
}

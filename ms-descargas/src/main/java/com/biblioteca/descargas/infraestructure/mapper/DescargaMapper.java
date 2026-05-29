package com.biblioteca.descargas.infraestructure.mapper;

import com.biblioteca.descargas.domain.model.Descarga;
import com.biblioteca.descargas.infraestructure.driver_adapters.jpa_repository.DescargaData;
import org.springframework.stereotype.Component;

@Component
public class DescargaMapper {

    public Descarga toDomain(DescargaData data) {
        Descarga d = new Descarga();
        d.setId(data.getId());
        d.setRecursoId(data.getRecursoId());
        d.setTituloRecurso(data.getTituloRecurso());
        d.setUsuarioId(data.getUsuarioId());
        d.setUsuarioCorreo(data.getUsuarioCorreo());
        d.setDescargadoEn(data.getDescargadoEn());
        d.setIpOrigen(data.getIpOrigen());
        return d;
    }

    public DescargaData toData(Descarga domain) {
        DescargaData d = new DescargaData();
        d.setId(domain.getId());
        d.setRecursoId(domain.getRecursoId());
        d.setTituloRecurso(domain.getTituloRecurso());
        d.setUsuarioId(domain.getUsuarioId());
        d.setUsuarioCorreo(domain.getUsuarioCorreo());
        d.setDescargadoEn(domain.getDescargadoEn());
        d.setIpOrigen(domain.getIpOrigen());
        return d;
    }
}

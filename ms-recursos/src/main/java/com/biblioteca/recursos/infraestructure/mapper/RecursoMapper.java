package com.biblioteca.recursos.infraestructure.mapper;
import com.biblioteca.recursos.domain.model.Recurso;
import com.biblioteca.recursos.infraestructure.driver_adapters.jpa_repository.RecursoData;
import org.springframework.stereotype.Component;
@Component
public class RecursoMapper {
    public Recurso toDomain(RecursoData data) {
        Recurso r = new Recurso();
        r.setId(data.getId());
        r.setTitulo(data.getTitulo());
        r.setDescripcion(data.getDescripcion());
        r.setArea(data.getArea());
        r.setGrado(data.getGrado());
        r.setTipo(data.getTipo());
        r.setNombreArchivo(data.getNombreArchivo());
        r.setRutaArchivo(data.getRutaArchivo());
        r.setTamanoBytes(data.getTamanoBytes());
        r.setSubidoPorId(data.getSubidoPorId());
        r.setSubidoPorCorreo(data.getSubidoPorCorreo());
        r.setCreadoEn(data.getCreadoEn());
        r.setDisponible(data.getDisponible());
        r.setContenido(data.getContenido());
        return r;
    }
    public RecursoData toData(Recurso domain) {
        RecursoData d = new RecursoData();
        d.setId(domain.getId());
        d.setTitulo(domain.getTitulo());
        d.setDescripcion(domain.getDescripcion());
        d.setArea(domain.getArea());
        d.setGrado(domain.getGrado());
        d.setTipo(domain.getTipo() != null ? domain.getTipo() : "PDF");
        d.setNombreArchivo(domain.getNombreArchivo());
        d.setRutaArchivo(domain.getRutaArchivo());
        d.setTamanoBytes(domain.getTamanoBytes());
        d.setSubidoPorId(domain.getSubidoPorId());
        d.setSubidoPorCorreo(domain.getSubidoPorCorreo());
        d.setCreadoEn(domain.getCreadoEn());
        d.setDisponible(domain.getDisponible() != null ? domain.getDisponible() : true);
        d.setContenido(domain.getContenido());
        return d;
    }
}

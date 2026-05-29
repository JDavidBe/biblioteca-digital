package com.biblioteca.usuarios.infraestructure.mapper;

import com.biblioteca.usuarios.domain.model.Usuario;
import com.biblioteca.usuarios.infraestructure.driver_adapters.jpa_repository.UsuarioData;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioData data) {
        Usuario u = new Usuario();
        u.setId(data.getId());
        u.setNombre(data.getNombre());
        u.setCorreo(data.getCorreo());
        u.setInstitucion(data.getInstitucion());
        u.setGrado(data.getGrado());
        u.setRol(data.getRol());
        u.setActivo(data.getActivo());
        u.setCreadoEn(data.getCreadoEn());
        return u;
    }

    public UsuarioData toData(Usuario domain) {
        UsuarioData d = new UsuarioData();
        d.setId(domain.getId());
        d.setNombre(domain.getNombre());
        d.setCorreo(domain.getCorreo());
        d.setInstitucion(domain.getInstitucion());
        d.setGrado(domain.getGrado());
        d.setRol(domain.getRol() != null ? domain.getRol() : "ESTUDIANTE");
        d.setActivo(domain.getActivo() != null ? domain.getActivo() : true);
        d.setCreadoEn(domain.getCreadoEn());
        return d;
    }
}

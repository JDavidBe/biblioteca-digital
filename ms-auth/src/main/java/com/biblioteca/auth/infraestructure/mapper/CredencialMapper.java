package com.biblioteca.auth.infraestructure.mapper;

import com.biblioteca.auth.domain.model.Credencial;
import com.biblioteca.auth.infraestructure.driver_adapters.jpa_repository.CredencialData;
import org.springframework.stereotype.Component;

@Component
public class CredencialMapper {

    public Credencial toDomain(CredencialData data) {
        Credencial c = new Credencial();
        c.setId(data.getId());
        c.setCorreo(data.getCorreo());
        c.setPasswordHash(data.getPasswordHash());
        c.setRol(data.getRol());
        c.setActivo(data.getActivo());
        return c;
    }

    public CredencialData toData(Credencial domain) {
        CredencialData d = new CredencialData();
        d.setId(domain.getId());
        d.setCorreo(domain.getCorreo());
        d.setPasswordHash(domain.getPasswordHash());
        d.setRol(domain.getRol());
        d.setActivo(domain.getActivo() != null ? domain.getActivo() : true);
        return d;
    }
}

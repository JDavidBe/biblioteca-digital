package com.biblioteca.notificaciones.infraestructure.driver_adapters.jpa_repository;

import com.biblioteca.notificaciones.domain.model.Notificacion;

public class NotificacionMapper {

    public static Notificacion toDomain(NotificacionData data) {
        if (data == null) return null;
        Notificacion n = new Notificacion();
        n.setId(data.getId());
        n.setDestinatarioCorreo(data.getDestinatarioCorreo());
        n.setDestinatarioTelefono(data.getDestinatarioTelefono());
        n.setDestinatarioId(data.getDestinatarioId());
        n.setCanal(data.getCanal());
        n.setTipo(data.getTipo());
        n.setAsunto(data.getAsunto());
        n.setMensaje(data.getMensaje());
        n.setEnviada(data.getEnviada());
        n.setEnviadaSms(data.getEnviadaSms());
        n.setCreadaEn(data.getCreadaEn());
        n.setEnviadaEn(data.getEnviadaEn());
        return n;
    }

    public static NotificacionData toData(Notificacion domain) {
        if (domain == null) return null;
        NotificacionData data = new NotificacionData();
        data.setId(domain.getId());
        data.setDestinatarioCorreo(domain.getDestinatarioCorreo());
        data.setDestinatarioTelefono(domain.getDestinatarioTelefono());
        data.setDestinatarioId(domain.getDestinatarioId());
        data.setCanal(domain.getCanal() != null ? domain.getCanal() : "EMAIL");
        data.setTipo(domain.getTipo() != null ? domain.getTipo() : "GENERAL");
        data.setAsunto(domain.getAsunto());
        data.setMensaje(domain.getMensaje());
        data.setEnviada(domain.getEnviada() != null ? domain.getEnviada() : false);
        data.setEnviadaSms(domain.getEnviadaSms() != null ? domain.getEnviadaSms() : false);
        data.setCreadaEn(domain.getCreadaEn());
        data.setEnviadaEn(domain.getEnviadaEn());
        return data;
    }
}

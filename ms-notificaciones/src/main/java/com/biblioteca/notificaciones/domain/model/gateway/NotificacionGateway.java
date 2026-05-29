package com.biblioteca.notificaciones.domain.model.gateway;

import com.biblioteca.notificaciones.domain.model.Notificacion;
import java.util.List;

public interface NotificacionGateway {
    Notificacion guardar(Notificacion notificacion);
    Notificacion buscarPorId(Long id);
    List<Notificacion> listarPorDestinatario(Long destinatarioId);
    List<Notificacion> listarPendientes();
    List<Notificacion> listarTodas();
    void marcarComoEnviada(Long id);
    void marcarSmsComotEnviado(Long id);
}

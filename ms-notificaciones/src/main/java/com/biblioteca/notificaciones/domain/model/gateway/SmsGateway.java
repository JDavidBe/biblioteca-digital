package com.biblioteca.notificaciones.domain.model.gateway;

public interface SmsGateway {
    void enviar(String destinatario, String mensaje);
}

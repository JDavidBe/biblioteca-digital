package com.biblioteca.notificaciones.domain.model.gateway;

public interface EmailGateway {
    void enviar(String destinatario, String asunto, String cuerpo);
}

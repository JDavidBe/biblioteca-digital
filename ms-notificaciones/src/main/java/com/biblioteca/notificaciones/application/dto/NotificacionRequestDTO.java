package com.biblioteca.notificaciones.application.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NotificacionRequestDTO {
    @Email
    private String destinatarioCorreo;
    private String destinatarioTelefono;
    private Long destinatarioId;
    private String canal;   // EMAIL (default), SMS, AMBOS
    private String tipo;
    private String asunto;
    private String mensaje;
}

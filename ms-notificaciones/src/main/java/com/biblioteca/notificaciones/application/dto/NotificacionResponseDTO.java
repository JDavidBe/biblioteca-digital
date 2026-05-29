package com.biblioteca.notificaciones.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class NotificacionResponseDTO {
    private Long id;
    private String destinatarioCorreo;
    private String destinatarioTelefono;
    private Long destinatarioId;
    private String canal;
    private String tipo;
    private String asunto;
    private String mensaje;
    private Boolean enviada;
    private Boolean enviadaSms;
    private LocalDateTime creadaEn;
    private LocalDateTime enviadaEn;
}

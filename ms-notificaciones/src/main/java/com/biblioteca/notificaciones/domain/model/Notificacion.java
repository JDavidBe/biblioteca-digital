package com.biblioteca.notificaciones.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Notificacion {
    private Long id;
    private String destinatarioCorreo;
    private String destinatarioTelefono;
    private Long destinatarioId;
    private String canal;           // EMAIL, SMS, AMBOS
    private String tipo;            // BIENVENIDA, NUEVA_DESCARGA, NUEVO_RECURSO, VALORACION, GENERAL
    private String asunto;
    private String mensaje;
    private Boolean enviada;        // correo enviado
    private Boolean enviadaSms;     // sms enviado
    private LocalDateTime creadaEn;
    private LocalDateTime enviadaEn;
}

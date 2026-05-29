package com.biblioteca.notificaciones.infraestructure.driver_adapters.jpa_repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter @Setter
public class NotificacionData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "destinatario_correo")
    private String destinatarioCorreo;

    @Column(name = "destinatario_telefono", length = 20)
    private String destinatarioTelefono;

    @Column(name = "destinatario_id")
    private Long destinatarioId;

    @Column(nullable = false, length = 10)
    private String canal = "EMAIL";   // EMAIL, SMS, AMBOS

    @Column(nullable = false)
    private String tipo;

    @Column
    private String asunto;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    @Column(nullable = false)
    private Boolean enviada = false;

    @Column(name = "enviada_sms", nullable = false)
    private Boolean enviadaSms = false;

    @Column(name = "creada_en")
    private LocalDateTime creadaEn;

    @Column(name = "enviada_en")
    private LocalDateTime enviadaEn;
}

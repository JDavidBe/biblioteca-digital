package com.biblioteca.reportes.infraestructure.driver_adapters.jpa_repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "actividades")
@Getter @Setter
public class ActividadData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(name = "entidad_tipo")
    private String entidadTipo;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_correo")
    private String usuarioCorreo;

    @Column(columnDefinition = "TEXT")
    private String detalle;

    @Column(name = "ocurrido_en")
    private LocalDateTime ocurridoEn;
}

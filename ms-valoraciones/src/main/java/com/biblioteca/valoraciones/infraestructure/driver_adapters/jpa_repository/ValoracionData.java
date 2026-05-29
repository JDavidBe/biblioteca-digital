package com.biblioteca.valoraciones.infraestructure.driver_adapters.jpa_repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "valoraciones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"recurso_id", "usuario_id"})
})
@Getter @Setter
public class ValoracionData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recurso_id", nullable = false)
    private Long recursoId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_correo")
    private String usuarioCorreo;

    @Column(nullable = false)
    private Integer puntuacion;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;
}

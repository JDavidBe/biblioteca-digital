package com.biblioteca.descargas.infraestructure.driver_adapters.jpa_repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "descargas")
@Getter @Setter
public class DescargaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recurso_id", nullable = false)
    private Long recursoId;

    @Column(name = "titulo_recurso")
    private String tituloRecurso;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_correo")
    private String usuarioCorreo;

    @Column(name = "descargado_en")
    private LocalDateTime descargadoEn;

    @Column(name = "ip_origen")
    private String ipOrigen;
}

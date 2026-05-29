package com.biblioteca.recursos.infraestructure.driver_adapters.jpa_repository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Entity
@Table(name = "recursos")
@Getter @Setter
public class RecursoData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    @Column(nullable = false)
    private String area;
    private String grado;
    @Column(nullable = false)
    private String tipo = "PDF";
    @Column(name = "nombre_archivo")
    private String nombreArchivo;
    @Column(name = "ruta_archivo")
    private String rutaArchivo;
    @Column(name = "tamano_bytes")
    private Long tamanoBytes;
    @Column(name = "subido_por_id")
    private Long subidoPorId;
    @Column(name = "subido_por_correo")
    private String subidoPorCorreo;
    @Column(name = "creado_en")
    private LocalDateTime creadoEn;
    @Column(nullable = false)
    private Boolean disponible = true;
    @Column(name = "contenido", columnDefinition = "BYTEA")
    private byte[] contenido;
}

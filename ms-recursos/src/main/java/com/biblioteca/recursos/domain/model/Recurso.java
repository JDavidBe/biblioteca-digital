package com.biblioteca.recursos.domain.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Recurso {
    private Long id;
    private String titulo;
    private String descripcion;
    private String area;
    private String grado;
    private String tipo;
    private String nombreArchivo;
    private String rutaArchivo;
    private Long tamanoBytes;
    private Long subidoPorId;
    private String subidoPorCorreo;
    private LocalDateTime creadoEn;
    private Boolean disponible;
    private byte[] contenido;
}

package com.biblioteca.recursos.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class RecursoResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String area;
    private String grado;
    private String tipo;
    private String nombreArchivo;
    private Long tamanoBytes;
    private String subidoPorCorreo;
    private LocalDateTime creadoEn;
    private Boolean disponible;
}

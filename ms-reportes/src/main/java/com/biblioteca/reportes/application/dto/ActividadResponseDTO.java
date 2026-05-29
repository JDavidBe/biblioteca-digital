package com.biblioteca.reportes.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ActividadResponseDTO {
    private Long id;
    private String tipoEvento;
    private Long entidadId;
    private String entidadTipo;
    private Long usuarioId;
    private String usuarioCorreo;
    private String detalle;
    private LocalDateTime ocurridoEn;
}

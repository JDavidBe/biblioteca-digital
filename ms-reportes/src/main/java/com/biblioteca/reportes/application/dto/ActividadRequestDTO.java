package com.biblioteca.reportes.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ActividadRequestDTO {
    @NotBlank private String tipoEvento;
    private Long entidadId;
    private String entidadTipo;
    private Long usuarioId;
    private String usuarioCorreo;
    private String detalle;
}

package com.biblioteca.descargas.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DescargaRequestDTO {
    @NotNull private Long recursoId;
    private String tituloRecurso;
    private Long usuarioId;
    private String usuarioCorreo;
    private String ipOrigen;
}

package com.biblioteca.valoraciones.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ValoracionRequestDTO {
    @NotNull private Long recursoId;
    private Long usuarioId;
    private String usuarioCorreo;
    @NotNull @Min(1) @Max(5) private Integer puntuacion;
    private String comentario;
}

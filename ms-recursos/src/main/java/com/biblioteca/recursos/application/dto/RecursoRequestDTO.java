package com.biblioteca.recursos.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RecursoRequestDTO {
    @NotBlank private String titulo;
    private String descripcion;
    @NotBlank private String area;
    private String grado;
    private String tipo;
    private Long subidoPorId;
    private String subidoPorCorreo;
}

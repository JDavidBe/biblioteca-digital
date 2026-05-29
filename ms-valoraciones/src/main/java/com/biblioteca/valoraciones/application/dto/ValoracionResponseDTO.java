package com.biblioteca.valoraciones.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ValoracionResponseDTO {
    private Long id;
    private Long recursoId;
    private Long usuarioId;
    private String usuarioCorreo;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime creadoEn;
}

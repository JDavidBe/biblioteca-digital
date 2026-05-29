package com.biblioteca.descargas.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class DescargaResponseDTO {
    private Long id;
    private Long recursoId;
    private String tituloRecurso;
    private Long usuarioId;
    private String usuarioCorreo;
    private LocalDateTime descargadoEn;
}

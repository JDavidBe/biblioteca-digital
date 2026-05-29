package com.biblioteca.descargas.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class EstadisticaDescargaDTO {
    private Long recursoId;
    private String tituloRecurso;
    private Long totalDescargas;
}

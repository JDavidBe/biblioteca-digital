package com.biblioteca.descargas.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class EstadisticaDescarga {
    private Long recursoId;
    private String tituloRecurso;
    private Long totalDescargas;
}

package com.biblioteca.valoraciones.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ResumenValoracion {
    private Long recursoId;
    private Double promedio;
    private Long totalValoraciones;
}

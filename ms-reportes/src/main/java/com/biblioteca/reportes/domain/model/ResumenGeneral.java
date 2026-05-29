package com.biblioteca.reportes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ResumenGeneral {
    private Long totalEventos;
    private Long eventosHoy;
    private Long eventosSemana;
    private Long eventosMes;
    private String eventoMasFrecuente;
}

package com.biblioteca.reportes.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ResumenGeneralDTO {
    private Long totalEventos;
    private Long eventosHoy;
    private Long eventosSemana;
    private Long eventosMes;
    private String eventoMasFrecuente;
}

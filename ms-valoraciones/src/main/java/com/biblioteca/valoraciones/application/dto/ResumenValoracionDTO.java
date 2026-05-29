package com.biblioteca.valoraciones.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ResumenValoracionDTO {
    private Long recursoId;
    private Double promedio;
    private Long totalValoraciones;

    @JsonProperty("total")
    public Long getTotal() {
        return totalValoraciones;
    }
}

package com.biblioteca.usuarios.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponseDTO<T> {
    private Boolean exito;
    private String mensaje;
    private T datos;
}

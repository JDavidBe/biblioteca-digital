package com.biblioteca.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidacionTokenResponseDTO {
    private Boolean valido;
    private String correo;
}

package com.biblioteca.usuarios.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsuarioUpdateDTO {
    private String nombre;
    private String institucion;
    private String grado;
    private String rol;
}

package com.biblioteca.usuarios.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String institucion;
    private String grado;
    private String rol;
    private Boolean activo;
    private LocalDateTime creadoEn;
}

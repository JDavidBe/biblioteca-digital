package com.biblioteca.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Credencial {
    private Long id;
    private String correo;
    private String passwordHash;
    private String rol;
    private Boolean activo;
}

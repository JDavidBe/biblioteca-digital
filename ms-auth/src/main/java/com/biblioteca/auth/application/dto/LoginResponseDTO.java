package com.biblioteca.auth.application.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private Long id;
    private String token;
    private String tipo;
    private String correo;
    private String rol;
}

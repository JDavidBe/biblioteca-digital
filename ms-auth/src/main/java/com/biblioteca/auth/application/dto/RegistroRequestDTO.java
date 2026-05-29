package com.biblioteca.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequestDTO {
    @NotBlank @Email
    private String correo;
    @NotBlank @Size(min = 6)
    private String password;
    private String rol;
}

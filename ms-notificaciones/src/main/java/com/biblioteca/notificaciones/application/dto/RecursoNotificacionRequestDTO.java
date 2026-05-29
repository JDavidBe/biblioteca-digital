package com.biblioteca.notificaciones.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RecursoNotificacionRequestDTO {
    @Email
    private String correo;
    private String telefono;
    private Long usuarioId;
    @NotBlank
    private String tituloRecurso;
}

package com.biblioteca.auth.domain.model.gateway;
import com.biblioteca.auth.domain.model.Credencial;
public interface TokenGateway {
    String generarToken(Credencial credencial);
    String extraerCorreo(String token);
    String extraerRol(String token);
    Boolean validarToken(String token);
}

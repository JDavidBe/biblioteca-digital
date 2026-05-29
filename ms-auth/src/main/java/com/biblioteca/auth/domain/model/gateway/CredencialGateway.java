package com.biblioteca.auth.domain.model.gateway;
import com.biblioteca.auth.domain.model.Credencial;
public interface CredencialGateway {
    Credencial buscarPorCorreo(String correo);
    Credencial guardar(Credencial credencial);
    Boolean existePorCorreo(String correo);
    void eliminarPorCorreo(String correo);
}

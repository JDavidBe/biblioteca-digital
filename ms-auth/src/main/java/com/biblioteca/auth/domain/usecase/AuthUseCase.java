package com.biblioteca.auth.domain.usecase;
import com.biblioteca.auth.domain.model.Credencial;
import com.biblioteca.auth.domain.model.gateway.CredencialGateway;
import com.biblioteca.auth.domain.model.gateway.EncriptadorGateway;
import com.biblioteca.auth.domain.model.gateway.TokenGateway;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class AuthUseCase {
    private final CredencialGateway credencialGateway;
    private final EncriptadorGateway encriptadorGateway;
    private final TokenGateway tokenGateway;
    public String iniciarSesion(String correo, String password) {
        return generarToken(iniciarSesionCompleto(correo, password));
    }
    public Credencial iniciarSesionCompleto(String correo, String password) {
        if (correo == null || correo.isBlank())
            throw new RuntimeException("El correo no puede estar vacío");
        if (password == null || password.isBlank())
            throw new RuntimeException("La contraseña no puede estar vacía");
        Credencial credencial = credencialGateway.buscarPorCorreo(correo);
        if (credencial == null)
            throw new RuntimeException("No existe una cuenta con el correo: " + correo);
        if (!credencial.getActivo())
            throw new RuntimeException("La cuenta está desactivada");
        if (!encriptadorGateway.verificar(password, credencial.getPasswordHash()))
            throw new RuntimeException("Contraseña incorrecta");
        return credencial;
    }
    public String generarToken(Credencial credencial) {
        return tokenGateway.generarToken(credencial);
    }
    public Credencial registrar(Credencial credencial) {
        if (credencial.getCorreo() == null || credencial.getCorreo().isBlank())
            throw new RuntimeException("El correo no puede estar vacío");
        if (credencial.getPasswordHash() == null || credencial.getPasswordHash().isBlank())
            throw new RuntimeException("La contraseña no puede estar vacía");
        if (credencialGateway.existePorCorreo(credencial.getCorreo()))
            throw new RuntimeException("Ya existe una cuenta con el correo: " + credencial.getCorreo());
        credencial.setPasswordHash(encriptadorGateway.encriptar(credencial.getPasswordHash()));
        credencial.setActivo(true);
        if (credencial.getRol() == null || credencial.getRol().isBlank())
            credencial.setRol("ESTUDIANTE");
        return credencialGateway.guardar(credencial);
    }
    public Boolean validarToken(String token) {
        return tokenGateway.validarToken(token);
    }
    public String extraerCorreo(String token) {
        return tokenGateway.extraerCorreo(token);
    }
    public String extraerRol(String token) {
        return tokenGateway.extraerRol(token);
    }
    public void eliminarCredencial(String correo) {
        if (!credencialGateway.existePorCorreo(correo))
            throw new RuntimeException("No existe credencial con el correo: " + correo);
        credencialGateway.eliminarPorCorreo(correo);
    }
}

package com.biblioteca.usuarios.domain.usecase;

import com.biblioteca.usuarios.domain.model.Usuario;
import com.biblioteca.usuarios.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioGateway usuarioGateway;

    public Usuario crearUsuario(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank())
            throw new RuntimeException("El nombre es obligatorio");
        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank())
            throw new RuntimeException("El correo es obligatorio");
        if (usuarioGateway.existePorCorreo(usuario.getCorreo()))
            throw new RuntimeException("Ya existe un usuario con el correo: " + usuario.getCorreo());

        usuario.setActivo(true);
        usuario.setCreadoEn(LocalDateTime.now());
        if (usuario.getRol() == null || usuario.getRol().isBlank())
            usuario.setRol("ESTUDIANTE");

        return usuarioGateway.guardar(usuario);
    }

    public Usuario obtenerPorId(Long id) {
        Usuario usuario = usuarioGateway.buscarPorId(id);
        if (usuario == null)
            throw new RuntimeException("No existe usuario con id: " + id);
        return usuario;
    }

    public Usuario obtenerPorCorreo(String correo) {
        Usuario usuario = usuarioGateway.buscarPorCorreo(correo);
        if (usuario == null)
            throw new RuntimeException("No existe usuario con correo: " + correo);
        return usuario;
    }

    public List<Usuario> listarTodos() {
        return usuarioGateway.listarTodos();
    }

    public List<Usuario> listarPorRol(String rol) {
        return usuarioGateway.listarPorRol(rol);
    }

    public Usuario modificarUsuario(Long id, Usuario usuario) {
        if (usuarioGateway.buscarPorId(id) == null)
            throw new RuntimeException("No existe usuario con id: " + id);
        return usuarioGateway.actualizar(id, usuario);
    }

    public void desactivarUsuario(Long id) {
        if (usuarioGateway.buscarPorId(id) == null)
            throw new RuntimeException("No existe usuario con id: " + id);
        usuarioGateway.desactivar(id);
    }
    public void eliminarUsuario(Long id) {
        if (usuarioGateway.buscarPorId(id) == null)
            throw new RuntimeException("No existe usuario con id: " + id);
        usuarioGateway.eliminar(id);
    }
}

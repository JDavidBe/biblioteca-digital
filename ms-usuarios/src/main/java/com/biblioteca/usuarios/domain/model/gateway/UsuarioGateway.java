package com.biblioteca.usuarios.domain.model.gateway;
import com.biblioteca.usuarios.domain.model.Usuario;
import java.util.List;
public interface UsuarioGateway {
    Usuario guardar(Usuario usuario);
    Usuario buscarPorId(Long id);
    Usuario buscarPorCorreo(String correo);
    List<Usuario> listarTodos();
    List<Usuario> listarPorRol(String rol);
    Usuario actualizar(Long id, Usuario usuario);
    void desactivar(Long id);
    void eliminar(Long id);
    Boolean existePorCorreo(String correo);
}

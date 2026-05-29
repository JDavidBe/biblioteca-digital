package com.biblioteca.usuarios.infraestructure.driver_adapters.jpa_repository;

import com.biblioteca.usuarios.domain.model.Usuario;
import com.biblioteca.usuarios.domain.model.gateway.UsuarioGateway;
import com.biblioteca.usuarios.infraestructure.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UsuarioGatewayImpl implements UsuarioGateway {

    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioMapper mapper;

    @Override
    public Usuario guardar(Usuario usuario) {
        return mapper.toDomain(jpaRepository.save(mapper.toData(usuario)));
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain).orElse(null);
    }

    @Override
    public Usuario buscarPorCorreo(String correo) {
        return jpaRepository.findByCorreo(correo).map(mapper::toDomain).orElse(null);
    }

    @Override
    public List<Usuario> listarTodos() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Usuario> listarPorRol(String rol) {
        return jpaRepository.findByRol(rol).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuario) {
        UsuarioData data = jpaRepository.findById(id).orElseThrow();
        if (usuario.getCorreo() != null) data.setCorreo(usuario.getCorreo());
        if (usuario.getNombre() != null) data.setNombre(usuario.getNombre());
        if (usuario.getInstitucion() != null) data.setInstitucion(usuario.getInstitucion());
        if (usuario.getGrado() != null) data.setGrado(usuario.getGrado());
        if (usuario.getRol() != null) data.setRol(usuario.getRol());
        return mapper.toDomain(jpaRepository.save(data));
    }

    @Override
    public void desactivar(Long id) {
        UsuarioData data = jpaRepository.findById(id).orElseThrow();
        data.setActivo(false);
        jpaRepository.save(data);
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Boolean existePorCorreo(String correo) {
        return jpaRepository.existsByCorreo(correo);
    }
}
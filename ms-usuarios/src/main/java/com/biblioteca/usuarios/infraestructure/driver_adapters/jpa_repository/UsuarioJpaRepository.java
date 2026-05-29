package com.biblioteca.usuarios.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioData, Long> {
    Optional<UsuarioData> findByCorreo(String correo);
    List<UsuarioData> findByRol(String rol);
    Boolean existsByCorreo(String correo);
}

package com.biblioteca.auth.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredencialJpaRepository extends JpaRepository<CredencialData, Long> {
    Optional<CredencialData> findByCorreo(String correo);
    Boolean existsByCorreo(String correo);
}

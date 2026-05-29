package com.biblioteca.notificaciones.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionJpaRepository extends JpaRepository<NotificacionData, Long> {
    List<NotificacionData> findByDestinatarioId(Long destinatarioId);
    List<NotificacionData> findByEnviadaFalse();
}

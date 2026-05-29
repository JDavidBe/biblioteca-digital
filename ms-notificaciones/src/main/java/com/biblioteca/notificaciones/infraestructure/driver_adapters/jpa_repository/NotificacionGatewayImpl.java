package com.biblioteca.notificaciones.infraestructure.driver_adapters.jpa_repository;

import com.biblioteca.notificaciones.domain.model.Notificacion;
import com.biblioteca.notificaciones.domain.model.gateway.NotificacionGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificacionGatewayImpl implements NotificacionGateway {

    private final NotificacionJpaRepository repo;

    @Override
    public Notificacion guardar(Notificacion notificacion) {
        return NotificacionMapper.toDomain(repo.save(NotificacionMapper.toData(notificacion)));
    }

    @Override
    public Notificacion buscarPorId(Long id) {
        return repo.findById(id).map(NotificacionMapper::toDomain).orElse(null);
    }

    @Override
    public List<Notificacion> listarPorDestinatario(Long destinatarioId) {
        return repo.findByDestinatarioId(destinatarioId).stream()
                .map(NotificacionMapper::toDomain).toList();
    }

    @Override
    public List<Notificacion> listarPendientes() {
        return repo.findByEnviadaFalse().stream()
                .map(NotificacionMapper::toDomain).toList();
    }

    @Override
    public List<Notificacion> listarTodas() {
        return repo.findAll().stream()
                .map(NotificacionMapper::toDomain).toList();
    }

    @Override
    public void marcarComoEnviada(Long id) {
        repo.findById(id).ifPresent(data -> {
            data.setEnviada(true);
            data.setEnviadaEn(LocalDateTime.now());
            repo.save(data);
        });
    }

    @Override
    public void marcarSmsComotEnviado(Long id) {
        repo.findById(id).ifPresent(data -> {
            data.setEnviadaSms(true);
            if (data.getEnviadaEn() == null) data.setEnviadaEn(LocalDateTime.now());
            repo.save(data);
        });
    }
}

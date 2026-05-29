package com.biblioteca.descargas.domain.usecase;

import com.biblioteca.descargas.domain.model.Descarga;
import com.biblioteca.descargas.domain.model.EstadisticaDescarga;
import com.biblioteca.descargas.domain.model.gateway.DescargaGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class DescargaUseCase {

    private final DescargaGateway descargaGateway;

    public Descarga registrarDescarga(Descarga descarga) {
        if (descarga.getRecursoId() == null)
            throw new RuntimeException("El recurso es obligatorio");
        descarga.setDescargadoEn(LocalDateTime.now());
        return descargaGateway.registrar(descarga);
    }

    public List<Descarga> historialPorUsuario(Long usuarioId) {
        return descargaGateway.listarPorUsuario(usuarioId);
    }

    public List<Descarga> historialPorRecurso(Long recursoId) {
        return descargaGateway.listarPorRecurso(recursoId);
    }

    public List<Descarga> descargasRecientes(Integer limite) {
        return descargaGateway.listarRecientes(limite != null ? limite : 20);
    }

    public List<EstadisticaDescarga> topMasDescargados(Integer limite) {
        return descargaGateway.topRecursosMasDescargados(limite != null ? limite : 10);
    }

    public Long contarPorRecurso(Long recursoId) {
        return descargaGateway.contarPorRecurso(recursoId);
    }

    public Long contarPorUsuario(Long usuarioId) {
        return descargaGateway.contarPorUsuario(usuarioId);
    }
}

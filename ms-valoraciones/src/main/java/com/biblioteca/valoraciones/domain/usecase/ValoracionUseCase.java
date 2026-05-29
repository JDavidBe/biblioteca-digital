package com.biblioteca.valoraciones.domain.usecase;

import com.biblioteca.valoraciones.domain.model.ResumenValoracion;
import com.biblioteca.valoraciones.domain.model.Valoracion;
import com.biblioteca.valoraciones.domain.model.gateway.ValoracionGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ValoracionUseCase {

    private final ValoracionGateway valoracionGateway;

    public Valoracion calificar(Valoracion valoracion) {
        if (valoracion.getRecursoId() == null)
            throw new RuntimeException("El recurso es obligatorio");
        if (valoracion.getPuntuacion() == null || valoracion.getPuntuacion() < 1 || valoracion.getPuntuacion() > 5)
            throw new RuntimeException("La puntuación debe estar entre 1 y 5");

        Optional<Valoracion> existente = valoracionGateway.buscarPorRecursoYUsuario(
                valoracion.getRecursoId(), valoracion.getUsuarioId());
        if (existente.isPresent())
            throw new RuntimeException("Ya calificaste este recurso");

        valoracion.setCreadoEn(LocalDateTime.now());
        return valoracionGateway.guardar(valoracion);
    }

    public List<Valoracion> obtenerPorRecurso(Long recursoId) {
        return valoracionGateway.listarPorRecurso(recursoId);
    }

    public List<Valoracion> obtenerPorUsuario(Long usuarioId) {
        return valoracionGateway.listarPorUsuario(usuarioId);
    }

    public ResumenValoracion resumenPorRecurso(Long recursoId) {
        Double promedio = valoracionGateway.calcularPromedio(recursoId);
        Long total = valoracionGateway.contarPorRecurso(recursoId);
        return new ResumenValoracion(recursoId, promedio != null ? promedio : 0.0, total);
    }

    public void eliminarValoracion(Long id) {
        valoracionGateway.eliminar(id);
    }
}

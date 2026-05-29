package com.biblioteca.valoraciones.domain.model.gateway;

import com.biblioteca.valoraciones.domain.model.Valoracion;

import java.util.List;
import java.util.Optional;

public interface ValoracionGateway {
    Valoracion guardar(Valoracion valoracion);
    List<Valoracion> listarPorRecurso(Long recursoId);
    List<Valoracion> listarPorUsuario(Long usuarioId);
    Optional<Valoracion> buscarPorRecursoYUsuario(Long recursoId, Long usuarioId);
    Double calcularPromedio(Long recursoId);
    Long contarPorRecurso(Long recursoId);
    void eliminar(Long id);
}

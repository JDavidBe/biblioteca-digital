package com.biblioteca.recursos.domain.model.gateway;

import com.biblioteca.recursos.domain.model.Recurso;

import java.util.List;

public interface RecursoGateway {
    Recurso guardar(Recurso recurso);
    Recurso buscarPorId(Long id);
    List<Recurso> listarDisponibles();
    List<Recurso> buscar(String area, String grado, String q);
    List<Recurso> listarPorUsuario(Long usuarioId);
    Recurso actualizar(Long id, Recurso recurso);
    void eliminar(Long id);
}

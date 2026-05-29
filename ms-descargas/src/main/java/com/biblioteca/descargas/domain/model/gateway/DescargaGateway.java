package com.biblioteca.descargas.domain.model.gateway;

import com.biblioteca.descargas.domain.model.Descarga;
import com.biblioteca.descargas.domain.model.EstadisticaDescarga;

import java.util.List;

public interface DescargaGateway {
    Descarga registrar(Descarga descarga);
    List<Descarga> listarPorUsuario(Long usuarioId);
    List<Descarga> listarPorRecurso(Long recursoId);
    List<Descarga> listarRecientes(Integer limite);
    List<EstadisticaDescarga> topRecursosMasDescargados(Integer limite);
    Long contarPorRecurso(Long recursoId);
    Long contarPorUsuario(Long usuarioId);
}

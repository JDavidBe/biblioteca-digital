package com.biblioteca.descargas.infraestructure.driver_adapters.jpa_repository;

import com.biblioteca.descargas.domain.model.Descarga;
import com.biblioteca.descargas.domain.model.EstadisticaDescarga;
import com.biblioteca.descargas.domain.model.gateway.DescargaGateway;
import com.biblioteca.descargas.infraestructure.mapper.DescargaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DescargaGatewayImpl implements DescargaGateway {

    private final DescargaJpaRepository jpaRepository;
    private final DescargaMapper mapper;

    @Override
    public Descarga registrar(Descarga descarga) {
        return mapper.toDomain(jpaRepository.save(mapper.toData(descarga)));
    }

    @Override
    public List<Descarga> listarPorUsuario(Long usuarioId) {
        return jpaRepository.findByUsuarioIdOrderByDescargadoEnDesc(usuarioId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Descarga> listarPorRecurso(Long recursoId) {
        return jpaRepository.findByRecursoIdOrderByDescargadoEnDesc(recursoId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Descarga> listarRecientes(Integer limite) {
        return jpaRepository.findRecientes(PageRequest.of(0, limite))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<EstadisticaDescarga> topRecursosMasDescargados(Integer limite) {
        return jpaRepository.findTopDescargados(PageRequest.of(0, limite))
                .stream().map(row -> new EstadisticaDescarga(
                        (Long) row[0], (String) row[1], (Long) row[2])).toList();
    }

    @Override
    public Long contarPorRecurso(Long recursoId) {
        return jpaRepository.countByRecursoId(recursoId);
    }

    @Override
    public Long contarPorUsuario(Long usuarioId) {
        return jpaRepository.countByUsuarioId(usuarioId);
    }
}

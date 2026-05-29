package com.biblioteca.recursos.infraestructure.driver_adapters.jpa_repository;

import com.biblioteca.recursos.domain.model.Recurso;
import com.biblioteca.recursos.domain.model.gateway.RecursoGateway;
import com.biblioteca.recursos.infraestructure.mapper.RecursoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecursoGatewayImpl implements RecursoGateway {

    private final RecursoJpaRepository jpaRepository;
    private final RecursoMapper mapper;

    @Override
    public Recurso guardar(Recurso recurso) {
        return mapper.toDomain(jpaRepository.save(mapper.toData(recurso)));
    }

    @Override
    public Recurso buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain).orElse(null);
    }

    @Override
    public List<Recurso> listarDisponibles() {
        return jpaRepository.findByDisponibleTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Recurso> buscar(String area, String grado, String q) {
        return jpaRepository.buscar(area, grado, q).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Recurso> listarPorUsuario(Long usuarioId) {
        return jpaRepository.findBySubidoPorId(usuarioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Recurso actualizar(Long id, Recurso recurso) {
        RecursoData data = jpaRepository.findById(id).orElseThrow();
        if (recurso.getTitulo() != null) data.setTitulo(recurso.getTitulo());
        if (recurso.getDescripcion() != null) data.setDescripcion(recurso.getDescripcion());
        if (recurso.getArea() != null) data.setArea(recurso.getArea());
        if (recurso.getGrado() != null) data.setGrado(recurso.getGrado());
        return mapper.toDomain(jpaRepository.save(data));
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}

package com.biblioteca.valoraciones.infraestructure.driver_adapters.jpa_repository;

import com.biblioteca.valoraciones.domain.model.Valoracion;
import com.biblioteca.valoraciones.domain.model.gateway.ValoracionGateway;
import com.biblioteca.valoraciones.infraestructure.mapper.ValoracionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ValoracionGatewayImpl implements ValoracionGateway {

    private final ValoracionJpaRepository jpaRepository;
    private final ValoracionMapper mapper;

    @Override
    public Valoracion guardar(Valoracion valoracion) {
        return mapper.toDomain(jpaRepository.save(mapper.toData(valoracion)));
    }

    @Override
    public List<Valoracion> listarPorRecurso(Long recursoId) {
        return jpaRepository.findByRecursoId(recursoId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Valoracion> listarPorUsuario(Long usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Valoracion> buscarPorRecursoYUsuario(Long recursoId, Long usuarioId) {
        return jpaRepository.findByRecursoIdAndUsuarioId(recursoId, usuarioId).map(mapper::toDomain);
    }

    @Override
    public Double calcularPromedio(Long recursoId) {
        return jpaRepository.calcularPromedio(recursoId);
    }

    @Override
    public Long contarPorRecurso(Long recursoId) {
        return jpaRepository.countByRecursoId(recursoId);
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}

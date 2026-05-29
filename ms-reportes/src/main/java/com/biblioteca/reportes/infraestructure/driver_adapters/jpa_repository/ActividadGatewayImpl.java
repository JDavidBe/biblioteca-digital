package com.biblioteca.reportes.infraestructure.driver_adapters.jpa_repository;

import com.biblioteca.reportes.domain.model.RegistroActividad;
import com.biblioteca.reportes.domain.model.ResumenGeneral;
import com.biblioteca.reportes.domain.model.gateway.ActividadGateway;
import com.biblioteca.reportes.infraestructure.mapper.ActividadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ActividadGatewayImpl implements ActividadGateway {

    private final ActividadJpaRepository jpaRepository;
    private final ActividadMapper mapper;

    @Override
    public RegistroActividad registrar(RegistroActividad actividad) {
        return mapper.toDomain(jpaRepository.save(mapper.toData(actividad)));
    }

    @Override
    public List<RegistroActividad> listarPorTipo(String tipoEvento) {
        return jpaRepository.findByTipoEventoOrderByOcurridoEnDesc(tipoEvento)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RegistroActividad> listarPorUsuario(Long usuarioId) {
        return jpaRepository.findByUsuarioIdOrderByOcurridoEnDesc(usuarioId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RegistroActividad> listarEntreFechas(LocalDateTime desde, LocalDateTime hasta) {
        return jpaRepository.findByOcurridoEnBetweenOrderByOcurridoEnDesc(desde, hasta)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RegistroActividad> listarRecientes(Integer limite) {
        return jpaRepository.findRecientes(PageRequest.of(0, limite))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public ResumenGeneral obtenerResumen() {
        long total = jpaRepository.count();
        long hoy = jpaRepository.countByOcurridoEnAfter(LocalDate.now().atStartOfDay());
        long semana = jpaRepository.countByOcurridoEnAfter(LocalDateTime.now().minusDays(7));
        long mes = jpaRepository.countByOcurridoEnAfter(LocalDateTime.now().minusDays(30));
        List<String> frecuentes = jpaRepository.findEventoMasFrecuente(PageRequest.of(0, 1));
        String masFrecuente = frecuentes.isEmpty() ? "N/A" : frecuentes.get(0);
        return new ResumenGeneral(total, hoy, semana, mes, masFrecuente);
    }
}

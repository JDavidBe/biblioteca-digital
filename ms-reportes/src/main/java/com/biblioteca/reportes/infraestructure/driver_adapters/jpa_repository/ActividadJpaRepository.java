package com.biblioteca.reportes.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ActividadJpaRepository extends JpaRepository<ActividadData, Long> {
    List<ActividadData> findByTipoEventoOrderByOcurridoEnDesc(String tipoEvento);
    List<ActividadData> findByUsuarioIdOrderByOcurridoEnDesc(Long usuarioId);

    List<ActividadData> findByOcurridoEnBetweenOrderByOcurridoEnDesc(
            LocalDateTime desde, LocalDateTime hasta);

    @Query("SELECT a FROM ActividadData a ORDER BY a.ocurridoEn DESC")
    List<ActividadData> findRecientes(Pageable pageable);

    Long countByOcurridoEnAfter(LocalDateTime fecha);

    @Query("SELECT a.tipoEvento FROM ActividadData a GROUP BY a.tipoEvento ORDER BY COUNT(a) DESC")
    List<String> findEventoMasFrecuente(Pageable pageable);
}

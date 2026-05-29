package com.biblioteca.valoraciones.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ValoracionJpaRepository extends JpaRepository<ValoracionData, Long> {
    List<ValoracionData> findByRecursoId(Long recursoId);
    List<ValoracionData> findByUsuarioId(Long usuarioId);
    Optional<ValoracionData> findByRecursoIdAndUsuarioId(Long recursoId, Long usuarioId);

    @Query("SELECT AVG(v.puntuacion) FROM ValoracionData v WHERE v.recursoId = :recursoId")
    Double calcularPromedio(@Param("recursoId") Long recursoId);

    Long countByRecursoId(Long recursoId);
}

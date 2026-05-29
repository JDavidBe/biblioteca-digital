package com.biblioteca.recursos.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecursoJpaRepository extends JpaRepository<RecursoData, Long> {

    List<RecursoData> findByDisponibleTrue();

    List<RecursoData> findBySubidoPorId(Long subidoPorId);

    @Query("SELECT r FROM RecursoData r WHERE r.disponible = true " +
            "AND (:area IS NULL OR :area = '' OR LOWER(r.area) = LOWER(:area)) " +
            "AND (:grado IS NULL OR :grado = '' OR LOWER(r.grado) = LOWER(:grado)) " +
            "AND (:q IS NULL OR :q = '' OR LOWER(r.titulo) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "     OR LOWER(r.descripcion) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<RecursoData> buscar(@Param("area") String area,
                             @Param("grado") String grado,
                             @Param("q") String q);
}


package com.biblioteca.descargas.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DescargaJpaRepository extends JpaRepository<DescargaData, Long> {
    List<DescargaData> findByUsuarioIdOrderByDescargadoEnDesc(Long usuarioId);
    List<DescargaData> findByRecursoIdOrderByDescargadoEnDesc(Long recursoId);
    Long countByRecursoId(Long recursoId);
    Long countByUsuarioId(Long usuarioId);

    @Query("SELECT d FROM DescargaData d ORDER BY d.descargadoEn DESC")
    List<DescargaData> findRecientes(Pageable pageable);

    @Query("SELECT d.recursoId, d.tituloRecurso, COUNT(d) as total " +
           "FROM DescargaData d GROUP BY d.recursoId, d.tituloRecurso ORDER BY total DESC")
    List<Object[]> findTopDescargados(Pageable pageable);
}

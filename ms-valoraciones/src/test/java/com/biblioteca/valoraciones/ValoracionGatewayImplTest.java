package com.biblioteca.valoraciones.infraestructure.driver_adapters.jpa_repository;

import com.biblioteca.valoraciones.domain.model.ResumenValoracion;
import com.biblioteca.valoraciones.domain.model.Valoracion;
import com.biblioteca.valoraciones.infraestructure.mapper.ValoracionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ValoracionGatewayImpl.class, ValoracionMapper.class})
@DisplayName("ValoracionGatewayImpl - Pruebas de integración con H2")
class ValoracionGatewayImplTest {

    @Autowired
    ValoracionGatewayImpl valoracionGateway;

    @Autowired
    ValoracionJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }

    private Valoracion valoracion(Long recursoId, Long usuarioId, int puntuacion) {
        Valoracion v = new Valoracion();
        v.setRecursoId(recursoId);
        v.setUsuarioId(usuarioId);
        v.setUsuarioCorreo("u@test.com");
        v.setPuntuacion(puntuacion);
        v.setComentario("comentario");
        v.setCreadoEn(LocalDateTime.now());
        return v;
    }

    @Test
    @DisplayName("guardar: persiste y retorna con id generado")
    void guardar_persisteConId() {
        Valoracion resultado = valoracionGateway.guardar(valoracion(1L, 1L, 4));
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getPuntuacion()).isEqualTo(4);
    }

    @Test
    @DisplayName("listarPorRecurso: retorna solo las del recurso indicado")
    void listarPorRecurso_filtraCorrectamente() {
        valoracionGateway.guardar(valoracion(1L, 1L, 4));
        valoracionGateway.guardar(valoracion(2L, 2L, 3));

        List<Valoracion> resultado = valoracionGateway.listarPorRecurso(1L);
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRecursoId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("listarPorRecurso: retorna vacío si no hay registros")
    void listarPorRecurso_sinRegistros_retornaVacio() {
        assertThat(valoracionGateway.listarPorRecurso(99L)).isEmpty();
    }

    @Test
    @DisplayName("listarPorUsuario: retorna solo las del usuario indicado")
    void listarPorUsuario_filtraCorrectamente() {
        valoracionGateway.guardar(valoracion(1L, 1L, 4));
        valoracionGateway.guardar(valoracion(2L, 2L, 3));

        List<Valoracion> resultado = valoracionGateway.listarPorUsuario(1L);
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsuarioId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("buscarPorRecursoYUsuario: retorna presente si existe")
    void buscarPorRecursoYUsuario_existe_retornaPresente() {
        valoracionGateway.guardar(valoracion(1L, 1L, 5));
        Optional<Valoracion> resultado = valoracionGateway.buscarPorRecursoYUsuario(1L, 1L);
        assertThat(resultado).isPresent();
    }

    @Test
    @DisplayName("buscarPorRecursoYUsuario: retorna vacío si no existe")
    void buscarPorRecursoYUsuario_noExiste_retornaVacio() {
        assertThat(valoracionGateway.buscarPorRecursoYUsuario(99L, 99L)).isEmpty();
    }

    @Test
    @DisplayName("calcularPromedio: retorna promedio correcto")
    void calcularPromedio_retornaPromedioCorrecto() {
        valoracionGateway.guardar(valoracion(1L, 1L, 4));
        valoracionGateway.guardar(valoracion(1L, 2L, 2));

        Double promedio = valoracionGateway.calcularPromedio(1L);
        assertThat(promedio).isEqualTo(3.0);
    }

    @Test
    @DisplayName("calcularPromedio: retorna null si no hay valoraciones")
    void calcularPromedio_sinDatos_retornaNull() {
        assertThat(valoracionGateway.calcularPromedio(99L)).isNull();
    }

    @Test
    @DisplayName("contarPorRecurso: retorna conteo correcto")
    void contarPorRecurso_retornaConteo() {
        valoracionGateway.guardar(valoracion(1L, 1L, 5));
        valoracionGateway.guardar(valoracion(1L, 2L, 3));
        assertThat(valoracionGateway.contarPorRecurso(1L)).isEqualTo(2L);
    }

    @Test
    @DisplayName("contarPorRecurso: retorna 0 si no hay valoraciones")
    void contarPorRecurso_sinDatos_retornaCero() {
        assertThat(valoracionGateway.contarPorRecurso(99L)).isEqualTo(0L);
    }

    @Test
    @DisplayName("eliminar: borra el registro")
    void eliminar_borraRegistro() {
        Valoracion guardada = valoracionGateway.guardar(valoracion(1L, 1L, 4));
        valoracionGateway.eliminar(guardada.getId());
        assertThat(jpaRepository.findById(guardada.getId())).isEmpty();
    }
}
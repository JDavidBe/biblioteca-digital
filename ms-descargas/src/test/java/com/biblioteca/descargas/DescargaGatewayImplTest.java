package com.biblioteca.descargas;

import com.biblioteca.descargas.domain.model.Descarga;
import com.biblioteca.descargas.domain.model.EstadisticaDescarga;
import com.biblioteca.descargas.infraestructure.driver_adapters.jpa_repository.DescargaData;
import com.biblioteca.descargas.infraestructure.driver_adapters.jpa_repository.DescargaGatewayImpl;
import com.biblioteca.descargas.infraestructure.driver_adapters.jpa_repository.DescargaJpaRepository;
import com.biblioteca.descargas.infraestructure.mapper.DescargaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DescargaGatewayImpl - Pruebas unitarias")
class DescargaGatewayImplTest {

    @Mock
    private DescargaJpaRepository jpaRepository;

    @Mock
    private DescargaMapper mapper;

    @InjectMocks
    private DescargaGatewayImpl gateway;

    private DescargaData data;
    private Descarga domain;

    @BeforeEach
    void setUp() {
        data = new DescargaData();
        data.setId(1L);
        data.setRecursoId(5L);
        data.setTituloRecurso("Java Avanzado");
        data.setUsuarioId(2L);
        data.setUsuarioCorreo("user@test.com");
        data.setDescargadoEn(LocalDateTime.now());
        data.setIpOrigen("192.168.0.1");

        domain = new Descarga(1L, 5L, "Java Avanzado", 2L, "user@test.com",
                LocalDateTime.now(), "192.168.0.1");
    }


    @Test
    @DisplayName("registrar: convierte, persiste y retorna dominio mapeado")
    void registrar_descargaValida_guardaYRetorna() {
        when(mapper.toData(domain)).thenReturn(data);
        when(jpaRepository.save(data)).thenReturn(data);
        when(mapper.toDomain(data)).thenReturn(domain);

        Descarga resultado = gateway.registrar(domain);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(jpaRepository).save(data);
        verify(mapper).toData(domain);
        verify(mapper).toDomain(data);
    }

    @Test
    @DisplayName("listarPorUsuario: retorna lista de descargas mapeadas")
    void listarPorUsuario_conDescargas_retornaLista() {
        when(jpaRepository.findByUsuarioIdOrderByDescargadoEnDesc(2L)).thenReturn(List.of(data));
        when(mapper.toDomain(data)).thenReturn(domain);

        List<Descarga> resultado = gateway.listarPorUsuario(2L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsuarioId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("listarPorUsuario: retorna lista vacía si no hay descargas")
    void listarPorUsuario_sinDescargas_retornaVacio() {
        when(jpaRepository.findByUsuarioIdOrderByDescargadoEnDesc(99L)).thenReturn(List.of());

        assertThat(gateway.listarPorUsuario(99L)).isEmpty();
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("listarPorRecurso: retorna lista de descargas mapeadas")
    void listarPorRecurso_conDescargas_retornaLista() {
        when(jpaRepository.findByRecursoIdOrderByDescargadoEnDesc(5L)).thenReturn(List.of(data));
        when(mapper.toDomain(data)).thenReturn(domain);

        List<Descarga> resultado = gateway.listarPorRecurso(5L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRecursoId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("listarPorRecurso: retorna lista vacía si no hay descargas")
    void listarPorRecurso_sinDescargas_retornaVacio() {
        when(jpaRepository.findByRecursoIdOrderByDescargadoEnDesc(99L)).thenReturn(List.of());

        assertThat(gateway.listarPorRecurso(99L)).isEmpty();
    }

    @Test
    @DisplayName("listarRecientes: pasa PageRequest con el límite correcto")
    void listarRecientes_conLimite_usaPageRequest() {
        when(jpaRepository.findRecientes(PageRequest.of(0, 10))).thenReturn(List.of(data));
        when(mapper.toDomain(data)).thenReturn(domain);

        List<Descarga> resultado = gateway.listarRecientes(10);

        assertThat(resultado).hasSize(1);
        verify(jpaRepository).findRecientes(PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("listarRecientes: retorna lista vacía si no hay resultados")
    void listarRecientes_sinResultados_retornaVacio() {
        when(jpaRepository.findRecientes(any())).thenReturn(List.of());

        assertThat(gateway.listarRecientes(5)).isEmpty();
    }

    @Test
    @DisplayName("topRecursosMasDescargados: mapea rows a EstadisticaDescarga correctamente")
    void topRecursosMasDescargados_conDatos_mapeaCorrectamente() {
        List<Object[]> rows = new java.util.ArrayList<>();
        rows.add(new Object[]{5L, "Java Avanzado", 100L});
        when(jpaRepository.findTopDescargados(PageRequest.of(0, 5))).thenReturn(rows);

        List<EstadisticaDescarga> resultado = gateway.topRecursosMasDescargados(5);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRecursoId()).isEqualTo(5L);
        assertThat(resultado.get(0).getTituloRecurso()).isEqualTo("Java Avanzado");
        assertThat(resultado.get(0).getTotalDescargas()).isEqualTo(100L);
    }

    @Test
    @DisplayName("topRecursosMasDescargados: retorna lista vacía si no hay datos")
    void topRecursosMasDescargados_sinDatos_retornaVacio() {
        when(jpaRepository.findTopDescargados(any())).thenReturn(List.of());

        assertThat(gateway.topRecursosMasDescargados(10)).isEmpty();
    }


    @Test
    @DisplayName("contarPorRecurso: delega en jpaRepository y retorna conteo")
    void contarPorRecurso_retornaConteo() {
        when(jpaRepository.countByRecursoId(5L)).thenReturn(42L);

        assertThat(gateway.contarPorRecurso(5L)).isEqualTo(42L);
        verify(jpaRepository).countByRecursoId(5L);
    }

    @Test
    @DisplayName("contarPorRecurso: retorna 0 cuando no hay descargas")
    void contarPorRecurso_sinDescargas_retornaCero() {
        when(jpaRepository.countByRecursoId(99L)).thenReturn(0L);

        assertThat(gateway.contarPorRecurso(99L)).isZero();
    }

    @Test
    @DisplayName("contarPorUsuario: delega en jpaRepository y retorna conteo")
    void contarPorUsuario_retornaConteo() {
        when(jpaRepository.countByUsuarioId(2L)).thenReturn(7L);

        assertThat(gateway.contarPorUsuario(2L)).isEqualTo(7L);
        verify(jpaRepository).countByUsuarioId(2L);
    }

    @Test
    @DisplayName("contarPorUsuario: retorna 0 cuando no hay descargas")
    void contarPorUsuario_sinDescargas_retornaCero() {
        when(jpaRepository.countByUsuarioId(99L)).thenReturn(0L);

        assertThat(gateway.contarPorUsuario(99L)).isZero();
    }
}
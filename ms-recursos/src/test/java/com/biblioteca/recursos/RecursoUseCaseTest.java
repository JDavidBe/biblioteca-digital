package com.biblioteca.recursos;

import com.biblioteca.recursos.domain.model.Recurso;
import com.biblioteca.recursos.domain.model.gateway.AlmacenamientoGateway;
import com.biblioteca.recursos.domain.model.gateway.RecursoGateway;
import com.biblioteca.recursos.domain.usecase.RecursoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecursoUseCase - Pruebas unitarias")
class RecursoUseCaseTest {

    @Mock private RecursoGateway recursoGateway;
    @Mock private AlmacenamientoGateway almacenamientoGateway;
    @Mock private MultipartFile archivo;
    @InjectMocks private RecursoUseCase recursoUseCase;

    private Recurso recursoBase;

    @BeforeEach
    void setUp() {
        recursoBase = new Recurso(1L, "Java Avanzado", "Desc", "Programación", "11",
                "PDF", "java.pdf", "/uploads/java.pdf", 1024L, 1L, "prof@test.com",
                LocalDateTime.now(), true, null);
    }

    @Test
    @DisplayName("publicarRecurso: guarda con valores por defecto")
    void publicarRecurso_datosValidos_guardaCorrectamente() {
        Recurso nuevo = new Recurso();
        nuevo.setTitulo("Matemáticas");
        nuevo.setArea("Ciencias");

        when(archivo.isEmpty()).thenReturn(false);
        when(recursoGateway.guardar(any())).thenReturn(recursoBase);

        recursoUseCase.publicarRecurso(nuevo, archivo);

        assertThat(nuevo.getTipo()).isEqualTo("PDF");
        assertThat(nuevo.getDisponible()).isTrue();
        assertThat(nuevo.getCreadoEn()).isNotNull();
    }

    @Test
    @DisplayName("publicarRecurso: preserva tipo cuando se especifica")
    void publicarRecurso_conTipoEspecifico_preservaTipo() {
        Recurso nuevo = new Recurso();
        nuevo.setTitulo("Video Ciencias");
        nuevo.setArea("Ciencias");
        nuevo.setTipo("VIDEO");

        when(archivo.isEmpty()).thenReturn(false);
        when(recursoGateway.guardar(any())).thenReturn(recursoBase);

        recursoUseCase.publicarRecurso(nuevo, archivo);

        assertThat(nuevo.getTipo()).isEqualTo("VIDEO");
    }

    @Test
    @DisplayName("publicarRecurso: lanza excepción si título es null")
    void publicarRecurso_tituloNull_lanzaExcepcion() {
        Recurso nuevo = new Recurso();
        nuevo.setTitulo(null);
        nuevo.setArea("Ciencias");

        assertThatThrownBy(() -> recursoUseCase.publicarRecurso(nuevo, archivo))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("título");
    }

    @Test
    @DisplayName("publicarRecurso: lanza excepción si título vacío")
    void publicarRecurso_tituloVacio_lanzaExcepcion() {
        Recurso nuevo = new Recurso();
        nuevo.setTitulo("");
        nuevo.setArea("Ciencias");

        assertThatThrownBy(() -> recursoUseCase.publicarRecurso(nuevo, archivo))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("título");
    }

    @Test
    @DisplayName("publicarRecurso: lanza excepción si área vacía")
    void publicarRecurso_areaVacia_lanzaExcepcion() {
        Recurso nuevo = new Recurso();
        nuevo.setTitulo("Libro");
        nuevo.setArea("");

        assertThatThrownBy(() -> recursoUseCase.publicarRecurso(nuevo, archivo))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("área");
    }

    @Test
    @DisplayName("publicarRecurso: lanza excepción si archivo null")
    void publicarRecurso_archivoNull_lanzaExcepcion() {
        Recurso nuevo = new Recurso();
        nuevo.setTitulo("Libro");
        nuevo.setArea("Arte");

        assertThatThrownBy(() -> recursoUseCase.publicarRecurso(nuevo, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Debe adjuntar un archivo");
    }

    @Test
    @DisplayName("publicarRecurso: lanza excepción si archivo vacío")
    void publicarRecurso_archivoVacio_lanzaExcepcion() {
        Recurso nuevo = new Recurso();
        nuevo.setTitulo("Libro");
        nuevo.setArea("Arte");
        when(archivo.isEmpty()).thenReturn(true);

        assertThatThrownBy(() -> recursoUseCase.publicarRecurso(nuevo, archivo))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Debe adjuntar un archivo");
    }

    @Test
    @DisplayName("obtenerPorId: retorna recurso cuando existe")
    void obtenerPorId_existente_retornaRecurso() {
        when(recursoGateway.buscarPorId(1L)).thenReturn(recursoBase);
        assertThat(recursoUseCase.obtenerPorId(1L)).isEqualTo(recursoBase);
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepción si no existe")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(recursoGateway.buscarPorId(99L)).thenReturn(null);
        assertThatThrownBy(() -> recursoUseCase.obtenerPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe recurso con id: 99");
    }

    @Test
    @DisplayName("buscarRecursos: delega en gateway")
    void buscarRecursos_delegaEnGateway() {
        when(recursoGateway.buscar("Ciencias", "10", "java")).thenReturn(List.of(recursoBase));
        assertThat(recursoUseCase.buscarRecursos("Ciencias", "10", "java")).hasSize(1);
    }

    @Test
    @DisplayName("listarTodos: retorna recursos disponibles")
    void listarTodos_retornaDisponibles() {
        when(recursoGateway.listarDisponibles()).thenReturn(List.of(recursoBase));
        assertThat(recursoUseCase.listarTodos()).hasSize(1);
    }

    @Test
    @DisplayName("listarPorUsuario: filtra por usuario")
    void listarPorUsuario_delegaEnGateway() {
        when(recursoGateway.listarPorUsuario(1L)).thenReturn(List.of(recursoBase));
        assertThat(recursoUseCase.listarPorUsuario(1L)).hasSize(1);
    }

    @Test
    @DisplayName("descargarArchivo: retorna contenido del recurso")
    void descargarArchivo_retornaBytes() {
        byte[] contenido = {1, 2, 3};
        recursoBase.setContenido(contenido);
        when(recursoGateway.buscarPorId(1L)).thenReturn(recursoBase);

        assertThat(recursoUseCase.descargarArchivo(1L)).isEqualTo(contenido);
    }

    @Test
    @DisplayName("editarRecurso: actualiza cuando existe")
    void editarRecurso_existente_actualiza() {
        when(recursoGateway.buscarPorId(1L)).thenReturn(recursoBase);
        when(recursoGateway.actualizar(eq(1L), any())).thenReturn(recursoBase);

        assertThat(recursoUseCase.editarRecurso(1L, recursoBase)).isNotNull();
    }

    @Test
    @DisplayName("editarRecurso: lanza excepción si no existe")
    void editarRecurso_noExiste_lanzaExcepcion() {
        when(recursoGateway.buscarPorId(99L)).thenReturn(null);
        assertThatThrownBy(() -> recursoUseCase.editarRecurso(99L, recursoBase))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe recurso con id: 99");
    }

    @Test
    @DisplayName("eliminarRecurso: elimina cuando existe")
    void eliminarRecurso_existente_elimina() {
        when(recursoGateway.buscarPorId(1L)).thenReturn(recursoBase);
        recursoUseCase.eliminarRecurso(1L);
        verify(recursoGateway).eliminar(1L);
    }

    @Test
    @DisplayName("eliminarRecurso: lanza excepción si no existe")
    void eliminarRecurso_noExiste_lanzaExcepcion() {
        when(recursoGateway.buscarPorId(99L)).thenReturn(null);
        assertThatThrownBy(() -> recursoUseCase.eliminarRecurso(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe recurso con id: 99");
    }
}
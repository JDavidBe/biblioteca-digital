package com.biblioteca.usuarios;

import com.biblioteca.usuarios.domain.model.Usuario;
import com.biblioteca.usuarios.domain.model.gateway.UsuarioGateway;
import com.biblioteca.usuarios.domain.usecase.UsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioUseCase - Pruebas unitarias")
class UsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuarioBase;

    @BeforeEach
    void setUp() {
        usuarioBase = new Usuario(1L, "Juan Pérez", "juan@test.com",
                "Universidad Test", "10", "ESTUDIANTE", true, LocalDateTime.now());
    }

    @Test
    @DisplayName("crearUsuario: guarda con valores por defecto cuando rol es null")
    void crearUsuario_rolNull_asignaEstudiante() {
        Usuario nuevo = new Usuario(null, "Ana García", "ana@test.com", "Colegio X", "8", null, null, null);
        when(usuarioGateway.existePorCorreo("ana@test.com")).thenReturn(false);
        when(usuarioGateway.guardar(any())).thenReturn(usuarioBase);

        usuarioUseCase.crearUsuario(nuevo);

        assertThat(nuevo.getActivo()).isTrue();
        assertThat(nuevo.getCreadoEn()).isNotNull();
        assertThat(nuevo.getRol()).isEqualTo("ESTUDIANTE");
        verify(usuarioGateway).guardar(nuevo);
    }

    @Test
    @DisplayName("crearUsuario: preserva rol cuando es especificado")
    void crearUsuario_conRolEspecifico_preservaRol() {
        Usuario nuevo = new Usuario(null, "Prof. López", "prof@test.com", "Inst X", "N/A", "DOCENTE", null, null);
        when(usuarioGateway.existePorCorreo("prof@test.com")).thenReturn(false);
        when(usuarioGateway.guardar(any())).thenReturn(nuevo);

        usuarioUseCase.crearUsuario(nuevo);

        assertThat(nuevo.getRol()).isEqualTo("DOCENTE");
    }

    @Test
    @DisplayName("crearUsuario: lanza excepción si nombre es null")
    void crearUsuario_nombreNull_lanzaExcepcion() {
        Usuario nuevo = new Usuario(null, null, "a@b.com", null, null, null, null, null);
        assertThatThrownBy(() -> usuarioUseCase.crearUsuario(nuevo))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nombre es obligatorio");
        verifyNoInteractions(usuarioGateway);
    }

    @Test
    @DisplayName("crearUsuario: lanza excepción si nombre está en blanco")
    void crearUsuario_nombreBlanco_lanzaExcepcion() {
        Usuario nuevo = new Usuario(null, "  ", "a@b.com", null, null, null, null, null);
        assertThatThrownBy(() -> usuarioUseCase.crearUsuario(nuevo))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nombre es obligatorio");
    }

    @Test
    @DisplayName("crearUsuario: lanza excepción si correo es null")
    void crearUsuario_correoNull_lanzaExcepcion() {
        Usuario nuevo = new Usuario(null, "Pedro", null, null, null, null, null, null);
        assertThatThrownBy(() -> usuarioUseCase.crearUsuario(nuevo))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("correo es obligatorio");
    }

    @Test
    @DisplayName("crearUsuario: lanza excepción si correo vacío")
    void crearUsuario_correoVacio_lanzaExcepcion() {
        Usuario nuevo = new Usuario(null, "Pedro", "", null, null, null, null, null);
        assertThatThrownBy(() -> usuarioUseCase.crearUsuario(nuevo))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("correo es obligatorio");
    }

    @Test
    @DisplayName("crearUsuario: lanza excepción si correo ya existe")
    void crearUsuario_correoExistente_lanzaExcepcion() {
        Usuario nuevo = new Usuario(null, "Pedro", "juan@test.com", null, null, null, null, null);
        when(usuarioGateway.existePorCorreo("juan@test.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioUseCase.crearUsuario(nuevo))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un usuario con el correo");
        verify(usuarioGateway, never()).guardar(any());
    }

    @Test
    @DisplayName("crearUsuario: rol en blanco también asigna ESTUDIANTE")
    void crearUsuario_rolBlanco_asignaEstudiante() {
        Usuario nuevo = new Usuario(null, "Mario", "mario@test.com", null, null, "  ", null, null);
        when(usuarioGateway.existePorCorreo("mario@test.com")).thenReturn(false);
        when(usuarioGateway.guardar(any())).thenReturn(usuarioBase);

        usuarioUseCase.crearUsuario(nuevo);

        assertThat(nuevo.getRol()).isEqualTo("ESTUDIANTE");
    }

    @Test
    @DisplayName("obtenerPorId: retorna usuario existente")
    void obtenerPorId_existente_retornaUsuario() {
        when(usuarioGateway.buscarPorId(1L)).thenReturn(usuarioBase);
        assertThat(usuarioUseCase.obtenerPorId(1L)).isEqualTo(usuarioBase);
        verify(usuarioGateway).buscarPorId(1L);
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepción si no existe")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(usuarioGateway.buscarPorId(99L)).thenReturn(null);
        assertThatThrownBy(() -> usuarioUseCase.obtenerPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe usuario con id: 99");
    }

    @Test
    @DisplayName("obtenerPorCorreo: retorna usuario existente")
    void obtenerPorCorreo_existente_retornaUsuario() {
        when(usuarioGateway.buscarPorCorreo("juan@test.com")).thenReturn(usuarioBase);
        assertThat(usuarioUseCase.obtenerPorCorreo("juan@test.com")).isEqualTo(usuarioBase);
    }

    @Test
    @DisplayName("obtenerPorCorreo: lanza excepción si no existe")
    void obtenerPorCorreo_noExiste_lanzaExcepcion() {
        when(usuarioGateway.buscarPorCorreo("nope@test.com")).thenReturn(null);
        assertThatThrownBy(() -> usuarioUseCase.obtenerPorCorreo("nope@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe usuario con correo");
    }

    @Test
    @DisplayName("listarTodos: delega en gateway y retorna lista")
    void listarTodos_retornaLista() {
        when(usuarioGateway.listarTodos()).thenReturn(List.of(usuarioBase));
        assertThat(usuarioUseCase.listarTodos()).hasSize(1);
        verify(usuarioGateway).listarTodos();
    }

    @Test
    @DisplayName("listarTodos: retorna lista vacía")
    void listarTodos_sinUsuarios_retornaVacia() {
        when(usuarioGateway.listarTodos()).thenReturn(List.of());
        assertThat(usuarioUseCase.listarTodos()).isEmpty();
    }

    @Test
    @DisplayName("listarPorRol: filtra por rol correctamente")
    void listarPorRol_retornaFiltrados() {
        when(usuarioGateway.listarPorRol("ESTUDIANTE")).thenReturn(List.of(usuarioBase));
        assertThat(usuarioUseCase.listarPorRol("ESTUDIANTE")).hasSize(1);
        verify(usuarioGateway).listarPorRol("ESTUDIANTE");
    }

    @Test
    @DisplayName("listarPorRol: retorna vacío si no hay coincidencias")
    void listarPorRol_sinCoincidencias_retornaVacia() {
        when(usuarioGateway.listarPorRol("ADMIN")).thenReturn(List.of());
        assertThat(usuarioUseCase.listarPorRol("ADMIN")).isEmpty();
    }

    @Test
    @DisplayName("modificarUsuario: actualiza cuando existe")
    void modificarUsuario_existente_actualiza() {
        when(usuarioGateway.buscarPorId(1L)).thenReturn(usuarioBase);
        when(usuarioGateway.actualizar(eq(1L), any())).thenReturn(usuarioBase);

        assertThat(usuarioUseCase.modificarUsuario(1L, usuarioBase)).isNotNull();
        verify(usuarioGateway).actualizar(eq(1L), any());
    }

    @Test
    @DisplayName("modificarUsuario: lanza excepción si no existe")
    void modificarUsuario_noExiste_lanzaExcepcion() {
        when(usuarioGateway.buscarPorId(99L)).thenReturn(null);
        assertThatThrownBy(() -> usuarioUseCase.modificarUsuario(99L, usuarioBase))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe usuario con id: 99");
        verify(usuarioGateway, never()).actualizar(anyLong(), any());
    }

    @Test
    @DisplayName("desactivarUsuario: desactiva cuando existe")
    void desactivarUsuario_existente_desactiva() {
        when(usuarioGateway.buscarPorId(1L)).thenReturn(usuarioBase);
        usuarioUseCase.desactivarUsuario(1L);
        verify(usuarioGateway).desactivar(1L);
    }

    @Test
    @DisplayName("desactivarUsuario: lanza excepción si no existe")
    void desactivarUsuario_noExiste_lanzaExcepcion() {
        when(usuarioGateway.buscarPorId(99L)).thenReturn(null);
        assertThatThrownBy(() -> usuarioUseCase.desactivarUsuario(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe usuario con id: 99");
        verify(usuarioGateway, never()).desactivar(anyLong());
    }

    @Test
    @DisplayName("eliminarUsuario: elimina cuando existe")
    void eliminarUsuario_existente_elimina() {
        when(usuarioGateway.buscarPorId(1L)).thenReturn(usuarioBase);
        usuarioUseCase.eliminarUsuario(1L);
        verify(usuarioGateway).eliminar(1L);
    }

    @Test
    @DisplayName("eliminarUsuario: lanza excepción si no existe")
    void eliminarUsuario_noExiste_lanzaExcepcion() {
        when(usuarioGateway.buscarPorId(99L)).thenReturn(null);
        assertThatThrownBy(() -> usuarioUseCase.eliminarUsuario(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe usuario con id: 99");
        verify(usuarioGateway, never()).eliminar(anyLong());
    }
}
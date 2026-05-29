package com.biblioteca.usuarios.infraestructure.driver_adapters.jpa_repository;

import com.biblioteca.usuarios.domain.model.Usuario;
import com.biblioteca.usuarios.infraestructure.mapper.UsuarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({UsuarioGatewayImpl.class, UsuarioMapper.class})
@DisplayName("UsuarioGatewayImpl - Pruebas de integración con H2")
class UsuarioGatewayImplTest {

    @Autowired
    UsuarioGatewayImpl usuarioGateway;

    @Autowired
    UsuarioJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }

    private Usuario usuario(String nombre, String correo, String rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setInstitucion("IED Test");
        u.setGrado("10");
        u.setRol(rol);
        u.setActivo(true);
        u.setCreadoEn(LocalDateTime.now());
        return u;
    }

    @Test
    @DisplayName("guardar: persiste y retorna usuario con id generado")
    void guardar_persisteConId() {
        Usuario resultado = usuarioGateway.guardar(usuario("Ana", "ana@test.com", "ESTUDIANTE"));
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Ana");
    }

    @Test
    @DisplayName("buscarPorId: retorna usuario existente")
    void buscarPorId_existente_retorna() {
        Usuario guardado = usuarioGateway.guardar(usuario("Juan", "juan@test.com", "DOCENTE"));
        assertThat(usuarioGateway.buscarPorId(guardado.getId())).isNotNull();
    }

    @Test
    @DisplayName("buscarPorId: retorna null si no existe")
    void buscarPorId_noExiste_retornaNull() {
        assertThat(usuarioGateway.buscarPorId(999L)).isNull();
    }

    @Test
    @DisplayName("buscarPorCorreo: retorna usuario existente")
    void buscarPorCorreo_existente_retorna() {
        usuarioGateway.guardar(usuario("Maria", "maria@test.com", "ESTUDIANTE"));
        assertThat(usuarioGateway.buscarPorCorreo("maria@test.com")).isNotNull();
        assertThat(usuarioGateway.buscarPorCorreo("maria@test.com").getCorreo()).isEqualTo("maria@test.com");
    }

    @Test
    @DisplayName("buscarPorCorreo: retorna null si no existe")
    void buscarPorCorreo_noExiste_retornaNull() {
        assertThat(usuarioGateway.buscarPorCorreo("nope@test.com")).isNull();
    }

    @Test
    @DisplayName("listarTodos: retorna todos los usuarios")
    void listarTodos_retornaTodos() {
        usuarioGateway.guardar(usuario("U1", "u1@test.com", "ESTUDIANTE"));
        usuarioGateway.guardar(usuario("U2", "u2@test.com", "DOCENTE"));
        assertThat(usuarioGateway.listarTodos()).hasSize(2);
    }

    @Test
    @DisplayName("listarPorRol: filtra correctamente")
    void listarPorRol_filtraCorrectamente() {
        usuarioGateway.guardar(usuario("E1", "e1@test.com", "ESTUDIANTE"));
        usuarioGateway.guardar(usuario("D1", "d1@test.com", "DOCENTE"));

        List<Usuario> estudiantes = usuarioGateway.listarPorRol("ESTUDIANTE");
        assertThat(estudiantes).hasSize(1);
        assertThat(estudiantes.get(0).getRol()).isEqualTo("ESTUDIANTE");
    }

    @Test
    @DisplayName("actualizar: modifica campos correctamente")
    void actualizar_modificaCampos() {
        Usuario guardado = usuarioGateway.guardar(usuario("Carlos", "carlos@test.com", "ESTUDIANTE"));
        Usuario cambios = new Usuario();
        cambios.setNombre("Carlos Actualizado");
        cambios.setInstitucion("Nueva IED");

        Usuario resultado = usuarioGateway.actualizar(guardado.getId(), cambios);

        assertThat(resultado.getNombre()).isEqualTo("Carlos Actualizado");
        assertThat(resultado.getInstitucion()).isEqualTo("Nueva IED");
    }

    @Test
    @DisplayName("desactivar: pone activo en false")
    void desactivar_poneFalse() {
        Usuario guardado = usuarioGateway.guardar(usuario("Rosa", "rosa@test.com", "ESTUDIANTE"));
        usuarioGateway.desactivar(guardado.getId());

        UsuarioData data = jpaRepository.findById(guardado.getId()).orElseThrow();
        assertThat(data.getActivo()).isFalse();
    }

    @Test
    @DisplayName("eliminar: borra el registro")
    void eliminar_borraRegistro() {
        Usuario guardado = usuarioGateway.guardar(usuario("Luis", "luis@test.com", "ADMIN"));
        usuarioGateway.eliminar(guardado.getId());
        assertThat(jpaRepository.findById(guardado.getId())).isEmpty();
    }

    @Test
    @DisplayName("existePorCorreo: retorna true si existe")
    void existePorCorreo_existe_retornaTrue() {
        usuarioGateway.guardar(usuario("Pedro", "pedro@test.com", "ESTUDIANTE"));
        assertThat(usuarioGateway.existePorCorreo("pedro@test.com")).isTrue();
    }

    @Test
    @DisplayName("existePorCorreo: retorna false si no existe")
    void existePorCorreo_noExiste_retornaFalse() {
        assertThat(usuarioGateway.existePorCorreo("nope@test.com")).isFalse();
    }
}
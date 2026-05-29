package com.biblioteca.auth;

import com.biblioteca.auth.domain.model.Credencial;
import com.biblioteca.auth.domain.model.gateway.CredencialGateway;
import com.biblioteca.auth.domain.model.gateway.EncriptadorGateway;
import com.biblioteca.auth.domain.model.gateway.TokenGateway;
import com.biblioteca.auth.domain.usecase.AuthUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthUseCase - Pruebas unitarias")
class AuthUseCaseTest {

    @Mock
    private CredencialGateway credencialGateway;
    @Mock
    private EncriptadorGateway encriptadorGateway;
    @Mock
    private TokenGateway tokenGateway;

    @InjectMocks
    private AuthUseCase authUseCase;

    private Credencial credencialActiva;

    @BeforeEach
    void setUp() {
        credencialActiva = new Credencial(1L, "user@test.com", "$hash$", "ESTUDIANTE", true);
    }


    @Test
    @DisplayName("iniciarSesion: retorna token cuando credenciales son correctas")
    void iniciarSesion_credencialesCorrectas_retornaToken() {
        when(credencialGateway.buscarPorCorreo("user@test.com")).thenReturn(credencialActiva);
        when(encriptadorGateway.verificar("pass123", "$hash$")).thenReturn(true);
        when(tokenGateway.generarToken(credencialActiva)).thenReturn("jwt-token");

        String token = authUseCase.iniciarSesion("user@test.com", "pass123");

        assertThat(token).isEqualTo("jwt-token");
        verify(tokenGateway).generarToken(credencialActiva);
    }

    @Test
    @DisplayName("iniciarSesion: lanza excepción si correo es cadena vacía")
    void iniciarSesion_correoVacio_lanzaExcepcion() {
        assertThatThrownBy(() -> authUseCase.iniciarSesion("", "pass123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("correo no puede estar vacío");
    }

    @Test
    @DisplayName("iniciarSesion: lanza excepción si correo es null")
    void iniciarSesion_correoNull_lanzaExcepcion() {
        assertThatThrownBy(() -> authUseCase.iniciarSesion(null, "pass123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("correo no puede estar vacío");
    }

    @Test
    @DisplayName("iniciarSesion: lanza excepción si correo solo espacios")
    void iniciarSesion_correoSoloEspacios_lanzaExcepcion() {
        assertThatThrownBy(() -> authUseCase.iniciarSesion("   ", "pass123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("correo no puede estar vacío");
    }

    @Test
    @DisplayName("iniciarSesion: lanza excepción si password es cadena vacía")
    void iniciarSesion_passwordVacio_lanzaExcepcion() {
        assertThatThrownBy(() -> authUseCase.iniciarSesion("user@test.com", ""))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("contraseña no puede estar vacía");
    }

    @Test
    @DisplayName("iniciarSesion: lanza excepción si password es null")
    void iniciarSesion_passwordNull_lanzaExcepcion() {
        assertThatThrownBy(() -> authUseCase.iniciarSesion("user@test.com", null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("contraseña no puede estar vacía");
    }

    @Test
    @DisplayName("iniciarSesion: lanza excepción si correo no existe en el sistema")
    void iniciarSesion_correoNoExiste_lanzaExcepcion() {
        when(credencialGateway.buscarPorCorreo("noexiste@test.com")).thenReturn(null);

        assertThatThrownBy(() -> authUseCase.iniciarSesion("noexiste@test.com", "pass123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe una cuenta con el correo");
    }

    @Test
    @DisplayName("iniciarSesion: lanza excepción si la cuenta está desactivada")
    void iniciarSesion_cuentaDesactivada_lanzaExcepcion() {
        Credencial inactiva = new Credencial(1L, "user@test.com", "$hash$", "ESTUDIANTE", false);
        when(credencialGateway.buscarPorCorreo("user@test.com")).thenReturn(inactiva);

        assertThatThrownBy(() -> authUseCase.iniciarSesion("user@test.com", "pass123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("desactivada");
    }

    @Test
    @DisplayName("iniciarSesion: lanza excepción si contraseña es incorrecta")
    void iniciarSesion_passwordIncorrecto_lanzaExcepcion() {
        when(credencialGateway.buscarPorCorreo("user@test.com")).thenReturn(credencialActiva);
        when(encriptadorGateway.verificar("wrongpass", "$hash$")).thenReturn(false);

        assertThatThrownBy(() -> authUseCase.iniciarSesion("user@test.com", "wrongpass"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Contraseña incorrecta");
    }


    @Test
    @DisplayName("iniciarSesionCompleto: retorna credencial cuando todo es correcto")
    void iniciarSesionCompleto_credencialesCorrectas_retornaCredencial() {
        when(credencialGateway.buscarPorCorreo("user@test.com")).thenReturn(credencialActiva);
        when(encriptadorGateway.verificar("pass123", "$hash$")).thenReturn(true);

        Credencial resultado = authUseCase.iniciarSesionCompleto("user@test.com", "pass123");

        assertThat(resultado).isEqualTo(credencialActiva);
    }


    @Test
    @DisplayName("generarToken: delega correctamente en tokenGateway")
    void generarToken_delegaEnGateway() {
        when(tokenGateway.generarToken(credencialActiva)).thenReturn("mi-jwt");

        String token = authUseCase.generarToken(credencialActiva);

        assertThat(token).isEqualTo("mi-jwt");
        verify(tokenGateway).generarToken(credencialActiva);
    }


    @Test
    @DisplayName("registrar: guarda credencial con rol y activo por defecto")
    void registrar_datosValidos_guardaConDefectos() {
        Credencial nueva = new Credencial(null, "nuevo@test.com", "plainpass", null, null);
        Credencial guardada = new Credencial(2L, "nuevo@test.com", "$encryptedHash$", "ESTUDIANTE", true);

        when(credencialGateway.existePorCorreo("nuevo@test.com")).thenReturn(false);
        when(encriptadorGateway.encriptar("plainpass")).thenReturn("$encryptedHash$");
        when(credencialGateway.guardar(any())).thenReturn(guardada);

        Credencial resultado = authUseCase.registrar(nueva);

        assertThat(resultado.getRol()).isEqualTo("ESTUDIANTE");
        assertThat(resultado.getActivo()).isTrue();
        verify(encriptadorGateway).encriptar("plainpass");
        verify(credencialGateway).guardar(nueva);
    }

    @Test
    @DisplayName("registrar: preserva el rol cuando ya viene especificado")
    void registrar_conRolEspecifico_preservaRol() {
        Credencial nueva = new Credencial(null, "admin@test.com", "pass", "ADMIN", null);
        Credencial guardada = new Credencial(3L, "admin@test.com", "$hash$", "ADMIN", true);

        when(credencialGateway.existePorCorreo("admin@test.com")).thenReturn(false);
        when(encriptadorGateway.encriptar("pass")).thenReturn("$hash$");
        when(credencialGateway.guardar(any())).thenReturn(guardada);

        Credencial resultado = authUseCase.registrar(nueva);

        assertThat(resultado.getRol()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("registrar: asigna rol ESTUDIANTE si el rol es cadena vacía")
    void registrar_rolVacio_asignaEstudiante() {
        Credencial nueva = new Credencial(null, "x@test.com", "pass", "", null);
        Credencial guardada = new Credencial(4L, "x@test.com", "$hash$", "ESTUDIANTE", true);

        when(credencialGateway.existePorCorreo("x@test.com")).thenReturn(false);
        when(encriptadorGateway.encriptar("pass")).thenReturn("$hash$");
        when(credencialGateway.guardar(any())).thenReturn(guardada);

        Credencial resultado = authUseCase.registrar(nueva);

        assertThat(resultado.getRol()).isEqualTo("ESTUDIANTE");
    }

    @Test
    @DisplayName("registrar: lanza excepción si correo es cadena vacía")
    void registrar_correoVacio_lanzaExcepcion() {
        Credencial nueva = new Credencial(null, "", "pass", null, null);
        assertThatThrownBy(() -> authUseCase.registrar(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("correo no puede estar vacío");
    }

    @Test
    @DisplayName("registrar: lanza excepción si correo es null")
    void registrar_correoNull_lanzaExcepcion() {
        Credencial nueva = new Credencial(null, null, "pass", null, null);
        assertThatThrownBy(() -> authUseCase.registrar(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("correo no puede estar vacío");
    }

    @Test
    @DisplayName("registrar: lanza excepción si password es cadena vacía")
    void registrar_passwordVacio_lanzaExcepcion() {
        Credencial nueva = new Credencial(null, "a@b.com", "", null, null);
        assertThatThrownBy(() -> authUseCase.registrar(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("contraseña no puede estar vacía");
    }

    @Test
    @DisplayName("registrar: lanza excepción si password es null")
    void registrar_passwordNull_lanzaExcepcion() {
        Credencial nueva = new Credencial(null, "a@b.com", null, null, null);
        assertThatThrownBy(() -> authUseCase.registrar(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("contraseña no puede estar vacía");
    }

    @Test
    @DisplayName("registrar: lanza excepción si el correo ya existe en el sistema")
    void registrar_correoExistente_lanzaExcepcion() {
        Credencial nueva = new Credencial(null, "user@test.com", "pass", null, null);
        when(credencialGateway.existePorCorreo("user@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authUseCase.registrar(nueva))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe una cuenta con el correo");
    }

    @Test
    @DisplayName("validarToken: retorna true para token válido")
    void validarToken_tokenValido_retornaTrue() {
        when(tokenGateway.validarToken("token123")).thenReturn(true);
        assertThat(authUseCase.validarToken("token123")).isTrue();
    }

    @Test
    @DisplayName("validarToken: retorna false para token inválido")
    void validarToken_tokenInvalido_retornaFalse() {
        when(tokenGateway.validarToken("bad-token")).thenReturn(false);
        assertThat(authUseCase.validarToken("bad-token")).isFalse();
    }


    @Test
    @DisplayName("extraerCorreo: delega correctamente en tokenGateway")
    void extraerCorreo_tokenValido_retornaCorreo() {
        when(tokenGateway.extraerCorreo("token123")).thenReturn("user@test.com");
        assertThat(authUseCase.extraerCorreo("token123")).isEqualTo("user@test.com");
    }


    @Test
    @DisplayName("extraerRol: delega correctamente en tokenGateway")
    void extraerRol_tokenValido_retornaRol() {
        when(tokenGateway.extraerRol("token123")).thenReturn("ESTUDIANTE");
        assertThat(authUseCase.extraerRol("token123")).isEqualTo("ESTUDIANTE");
    }


    @Test
    @DisplayName("eliminarCredencial: elimina cuando el correo existe")
    void eliminarCredencial_correoExistente_elimina() {
        when(credencialGateway.existePorCorreo("user@test.com")).thenReturn(true);

        authUseCase.eliminarCredencial("user@test.com");

        verify(credencialGateway).eliminarPorCorreo("user@test.com");
    }

    @Test
    @DisplayName("eliminarCredencial: lanza excepción si el correo no existe")
    void eliminarCredencial_correoNoExistente_lanzaExcepcion() {
        when(credencialGateway.existePorCorreo("noexiste@test.com")).thenReturn(false);

        assertThatThrownBy(() -> authUseCase.eliminarCredencial("noexiste@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe credencial con el correo");
    }
}
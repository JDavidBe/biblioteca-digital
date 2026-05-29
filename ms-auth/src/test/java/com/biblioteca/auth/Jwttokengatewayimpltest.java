package com.biblioteca.auth;

import com.biblioteca.auth.domain.model.Credencial;
import com.biblioteca.auth.infraestructure.security_encrypter.JwtTokenGatewayImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtTokenGatewayImpl - Pruebas unitarias")
class JwtTokenGatewayImplTest {

    private JwtTokenGatewayImpl jwtGateway;

    private static final String SECRET = "TestSecretKeyForJWTBibliotecaDigital2025XYZ!!";

    @BeforeEach
    void setUp() {
        jwtGateway = new JwtTokenGatewayImpl();
        ReflectionTestUtils.setField(jwtGateway, "secret", SECRET);
        ReflectionTestUtils.setField(jwtGateway, "expirationMs", 3600000L);
    }


    @Test
    @DisplayName("generarToken: genera un token no nulo y no vacío")
    void generarToken_credencialValida_retornaTokenNoVacio() {
        Credencial credencial = new Credencial(1L, "user@test.com", "$hash$", "ESTUDIANTE", true);

        String token = jwtGateway.generarToken(credencial);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    @DisplayName("generarToken: tokens distintos para distintos usuarios")
    void generarToken_distintoCorreo_generaTokensDiferentes() {
        Credencial c1 = new Credencial(1L, "user1@test.com", "$h$", "ESTUDIANTE", true);
        Credencial c2 = new Credencial(2L, "user2@test.com", "$h$", "ADMIN", true);

        String t1 = jwtGateway.generarToken(c1);
        String t2 = jwtGateway.generarToken(c2);

        assertThat(t1).isNotEqualTo(t2);
    }


    @Test
    @DisplayName("extraerCorreo: extrae el correo del token generado")
    void extraerCorreo_tokenValido_retornaCorreoCorrecto() {
        Credencial credencial = new Credencial(1L, "user@test.com", "$hash$", "ESTUDIANTE", true);
        String token = jwtGateway.generarToken(credencial);

        String correo = jwtGateway.extraerCorreo(token);

        assertThat(correo).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("extraerRol: extrae el rol del token generado")
    void extraerRol_tokenValido_retornaRolCorrecto() {
        Credencial credencial = new Credencial(1L, "user@test.com", "$hash$", "ADMIN", true);
        String token = jwtGateway.generarToken(credencial);

        String rol = jwtGateway.extraerRol(token);

        assertThat(rol).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("extraerRol: extrae rol ESTUDIANTE correctamente")
    void extraerRol_rolEstudiante_retornaEstudiante() {
        Credencial credencial = new Credencial(2L, "est@test.com", "$h$", "ESTUDIANTE", true);
        String token = jwtGateway.generarToken(credencial);

        assertThat(jwtGateway.extraerRol(token)).isEqualTo("ESTUDIANTE");
    }


    @Test
    @DisplayName("validarToken: retorna true para token válido recién generado")
    void validarToken_tokenValido_retornaTrue() {
        Credencial credencial = new Credencial(1L, "user@test.com", "$hash$", "ESTUDIANTE", true);
        String token = jwtGateway.generarToken(credencial);

        assertThat(jwtGateway.validarToken(token)).isTrue();
    }

    @Test
    @DisplayName("validarToken: retorna false para token corrupto")
    void validarToken_tokenCorrupto_retornaFalse() {
        assertThat(jwtGateway.validarToken("esto.no.es.un.token")).isFalse();
    }

    @Test
    @DisplayName("validarToken: retorna false para token con firma incorrecta")
    void validarToken_firmaIncorrecta_retornaFalse() {
        // Token válido estructuralmente pero firmado con otra clave
        String tokenFirmaAjena = "eyJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiJ1c2VyQHRlc3QuY29tIn0" +
                ".FIRMA_INVALIDA_QUE_NO_CORRESPONDE";

        assertThat(jwtGateway.validarToken(tokenFirmaAjena)).isFalse();
    }

    @Test
    @DisplayName("validarToken: retorna false para token expirado")
    void validarToken_tokenExpirado_retornaFalse() {
        // Generar con expiración en el pasado
        JwtTokenGatewayImpl expiredGateway = new JwtTokenGatewayImpl();
        ReflectionTestUtils.setField(expiredGateway, "secret", SECRET);
        ReflectionTestUtils.setField(expiredGateway, "expirationMs", -1000L); // ya expiró

        Credencial credencial = new Credencial(1L, "user@test.com", "$h$", "ESTUDIANTE", true);
        String expiredToken = expiredGateway.generarToken(credencial);

        assertThat(jwtGateway.validarToken(expiredToken)).isFalse();
    }

    @Test
    @DisplayName("validarToken: retorna false para string vacío")
    void validarToken_stringVacio_retornaFalse() {
        assertThat(jwtGateway.validarToken("")).isFalse();
    }
}
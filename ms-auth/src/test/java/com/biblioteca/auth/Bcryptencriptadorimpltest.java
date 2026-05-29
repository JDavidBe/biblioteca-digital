package com.biblioteca.auth;

import com.biblioteca.auth.infraestructure.security_encrypter.BcryptEncriptadorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BcryptEncriptadorImpl - Pruebas unitarias")
class BcryptEncriptadorImplTest {

    private BcryptEncriptadorImpl encriptador;

    @BeforeEach
    void setUp() {
        encriptador = new BcryptEncriptadorImpl();
    }

    @Test
    @DisplayName("encriptar: retorna hash no nulo y no vacío")
    void encriptar_textoValido_retornaHashNoVacio() {
        String hash = encriptador.encriptar("miPassword123");

        assertThat(hash).isNotBlank();
    }

    @Test
    @DisplayName("encriptar: el hash comienza con el prefijo BCrypt")
    void encriptar_textoValido_hashTienePrefijoBcrypt() {
        String hash = encriptador.encriptar("miPassword123");

        assertThat(hash).startsWith("$2");
    }

    @Test
    @DisplayName("encriptar: dos llamadas con el mismo texto producen hashes distintos (salt aleatorio)")
    void encriptar_mismoTexto_generaHashesDiferentes() {
        String hash1 = encriptador.encriptar("mismaPassword");
        String hash2 = encriptador.encriptar("mismaPassword");

        assertThat(hash1).isNotEqualTo(hash2);
    }


    @Test
    @DisplayName("verificar: retorna true cuando el texto coincide con el hash")
    void verificar_textoCorrespondiente_retornaTrue() {
        String password = "miPassword123";
        String hash = encriptador.encriptar(password);

        assertThat(encriptador.verificar(password, hash)).isTrue();
    }

    @Test
    @DisplayName("verificar: retorna false cuando el texto no coincide con el hash")
    void verificar_textoIncorrecto_retornaFalse() {
        String hash = encriptador.encriptar("passwordOriginal");

        assertThat(encriptador.verificar("passwordDistinta", hash)).isFalse();
    }

    @Test
    @DisplayName("verificar: retorna false con texto vacío")
    void verificar_textoVacio_retornaFalse() {
        String hash = encriptador.encriptar("password123");

        assertThat(encriptador.verificar("", hash)).isFalse();
    }

    @Test
    @DisplayName("verificar: round-trip encriptar+verificar siempre es consistente")
    void verificar_roundTrip_esConsistente() {
        String[] passwords = {"abc123", "P@ssw0rd!", "contraseñaÑoño", "12345678"};

        for (String pw : passwords) {
            String hash = encriptador.encriptar(pw);
            assertThat(encriptador.verificar(pw, hash))
                    .as("El password '%s' debería verificarse correctamente", pw)
                    .isTrue();
        }
    }
}
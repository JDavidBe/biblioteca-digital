package com.biblioteca.usuarios;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatCode;

class UsuariosApplicationTest {

    @Test
    void instancia_noLanzaExcepcion() {
        assertThatCode(UsuariosApplication::new).doesNotThrowAnyException();
    }
}
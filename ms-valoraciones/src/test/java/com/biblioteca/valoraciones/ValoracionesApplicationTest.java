
package com.biblioteca.valoraciones;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatCode;

class ValoracionesApplicationTest {

    @Test
    void instancia_noLanzaExcepcion() {
        assertThatCode(ValoracionesApplication::new).doesNotThrowAnyException();
    }
}
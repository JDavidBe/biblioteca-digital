package com.biblioteca.reportes;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatCode;

class ReportesApplicationTest {

    @Test
    void instancia_noLanzaExcepcion() {
        assertThatCode(ReportesApplication::new).doesNotThrowAnyException();
    }
}
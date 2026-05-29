package com.biblioteca.reportes;

import com.biblioteca.reportes.application.config.UseCaseConfig;
import com.biblioteca.reportes.domain.model.gateway.ActividadGateway;
import com.biblioteca.reportes.domain.usecase.ReporteUseCase;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UseCaseConfigTest {

    @Test
    void reporteUseCase_creaInstanciaCorrectamente() {
        ActividadGateway gateway = mock(ActividadGateway.class);
        UseCaseConfig config = new UseCaseConfig();
        ReporteUseCase useCase = config.reporteUseCase(gateway);
        assertThat(useCase).isNotNull();
    }
}
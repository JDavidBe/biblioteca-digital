package com.biblioteca.valoraciones;

import com.biblioteca.valoraciones.application.config.UseCaseConfig;
import com.biblioteca.valoraciones.domain.model.gateway.ValoracionGateway;
import com.biblioteca.valoraciones.domain.usecase.ValoracionUseCase;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UseCaseConfigTest {

    @Test
    void valoracionUseCase_creaInstanciaCorrectamente() {
        ValoracionUseCase useCase = new UseCaseConfig().valoracionUseCase(mock(ValoracionGateway.class));
        assertThat(useCase).isNotNull();
    }
}
package com.biblioteca.usuarios;

import com.biblioteca.usuarios.application.config.UseCaseConfig;
import com.biblioteca.usuarios.domain.model.gateway.UsuarioGateway;
import com.biblioteca.usuarios.domain.usecase.UsuarioUseCase;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UseCaseConfigTest {

    @Test
    void usuarioUseCase_creaInstanciaCorrectamente() {
        UseCaseConfig config = new UseCaseConfig();
        UsuarioUseCase useCase = config.usuarioUseCase(mock(UsuarioGateway.class));
        assertThat(useCase).isNotNull();
    }
}
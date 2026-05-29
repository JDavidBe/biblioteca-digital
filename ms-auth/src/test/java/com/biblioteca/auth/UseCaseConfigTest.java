package com.biblioteca.auth;

import com.biblioteca.auth.application.config.UseCaseConfig;
import com.biblioteca.auth.domain.model.gateway.CredencialGateway;
import com.biblioteca.auth.domain.model.gateway.EncriptadorGateway;
import com.biblioteca.auth.domain.model.gateway.TokenGateway;
import com.biblioteca.auth.domain.usecase.AuthUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("UseCaseConfig - Pruebas unitarias")
class UseCaseConfigTest {

    @Mock private CredencialGateway credencialGateway;
    @Mock private EncriptadorGateway encriptadorGateway;
    @Mock private TokenGateway tokenGateway;

    @Test
    @DisplayName("authUseCase retorna instancia no nula con las dependencias correctas")
    void authUseCase_retornaInstanciaNoNula() {
        UseCaseConfig config = new UseCaseConfig();
        AuthUseCase useCase = config.authUseCase(credencialGateway, encriptadorGateway, tokenGateway);
        assertThat(useCase).isNotNull();
    }
}
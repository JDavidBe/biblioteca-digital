package com.biblioteca.auth.application.config;

import com.biblioteca.auth.domain.model.gateway.CredencialGateway;
import com.biblioteca.auth.domain.model.gateway.EncriptadorGateway;
import com.biblioteca.auth.domain.model.gateway.TokenGateway;
import com.biblioteca.auth.domain.usecase.AuthUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public AuthUseCase authUseCase(CredencialGateway credencialGateway,
                                   EncriptadorGateway encriptadorGateway,
                                   TokenGateway tokenGateway) {
        return new AuthUseCase(credencialGateway, encriptadorGateway, tokenGateway);
    }
}

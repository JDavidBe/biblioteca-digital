package com.biblioteca.usuarios.application.config;

import com.biblioteca.usuarios.domain.model.gateway.UsuarioGateway;
import com.biblioteca.usuarios.domain.usecase.UsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public UsuarioUseCase usuarioUseCase(UsuarioGateway usuarioGateway) {
        return new UsuarioUseCase(usuarioGateway);
    }
}

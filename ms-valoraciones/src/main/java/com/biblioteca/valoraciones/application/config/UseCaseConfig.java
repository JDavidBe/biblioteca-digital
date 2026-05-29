package com.biblioteca.valoraciones.application.config;

import com.biblioteca.valoraciones.domain.model.gateway.ValoracionGateway;
import com.biblioteca.valoraciones.domain.usecase.ValoracionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ValoracionUseCase valoracionUseCase(ValoracionGateway valoracionGateway) {
        return new ValoracionUseCase(valoracionGateway);
    }
}

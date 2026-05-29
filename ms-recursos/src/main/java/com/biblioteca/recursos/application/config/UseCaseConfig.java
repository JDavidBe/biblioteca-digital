package com.biblioteca.recursos.application.config;

import com.biblioteca.recursos.domain.model.gateway.AlmacenamientoGateway;
import com.biblioteca.recursos.domain.model.gateway.RecursoGateway;
import com.biblioteca.recursos.domain.usecase.RecursoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public RecursoUseCase recursoUseCase(RecursoGateway recursoGateway,
                                          AlmacenamientoGateway almacenamientoGateway) {
        return new RecursoUseCase(recursoGateway, almacenamientoGateway);
    }
}

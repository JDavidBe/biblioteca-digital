package com.biblioteca.descargas.application.config;

import com.biblioteca.descargas.domain.model.gateway.DescargaGateway;
import com.biblioteca.descargas.domain.usecase.DescargaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public DescargaUseCase descargaUseCase(DescargaGateway descargaGateway) {
        return new DescargaUseCase(descargaGateway);
    }
}

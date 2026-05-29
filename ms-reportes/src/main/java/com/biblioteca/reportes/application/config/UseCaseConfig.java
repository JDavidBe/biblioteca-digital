package com.biblioteca.reportes.application.config;

import com.biblioteca.reportes.domain.model.gateway.ActividadGateway;
import com.biblioteca.reportes.domain.usecase.ReporteUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ReporteUseCase reporteUseCase(ActividadGateway actividadGateway) {
        return new ReporteUseCase(actividadGateway);
    }
}

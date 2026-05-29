package com.biblioteca.notificaciones.application.config;

import com.biblioteca.notificaciones.domain.model.gateway.EmailGateway;
import com.biblioteca.notificaciones.domain.model.gateway.NotificacionGateway;
import com.biblioteca.notificaciones.domain.model.gateway.SmsGateway;
import com.biblioteca.notificaciones.domain.usecase.NotificacionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    @Bean
    public NotificacionUseCase notificacionUseCase(
            NotificacionGateway notificacionGateway,
            EmailGateway emailGateway,
            SmsGateway smsGateway) {
        return new NotificacionUseCase(notificacionGateway, emailGateway, smsGateway);
    }
}

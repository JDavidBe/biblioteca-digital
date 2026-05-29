package com.biblioteca.notificaciones.infraestructure.email;

import com.biblioteca.notificaciones.domain.model.gateway.EmailGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JavaMailEmailGateway implements EmailGateway {

    private final JavaMailSender mailSender;

    @Override
    public void enviar(String destinatario, String asunto, String cuerpo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject(asunto);
        message.setText(cuerpo);
        mailSender.send(message);
    }
}

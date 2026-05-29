package com.biblioteca.notificaciones.infraestructure.email;

import com.biblioteca.notificaciones.domain.model.gateway.EmailGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class JavaMailEmailGateway implements EmailGateway {

    @Value("\")
    private String apiKey;

    @Override
    public void enviar(String destinatario, String asunto, String mensaje) {
        try {
            String body = String.format(
                "{\"from\":\"onboarding@resend.dev\",\"to\":[\"%s\"],\"subject\":\"%s\",\"text\":\"%s\"}",
                destinatario, asunto, mensaje
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Resend response: " + response.statusCode() + " - " + response.body());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Error Resend: " + response.body());
            }
        } catch (Exception e) {
            System.err.println("ERROR enviando email con Resend: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

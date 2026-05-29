package com.biblioteca.auth.domain.model.gateway;

public interface EncriptadorGateway {
    String encriptar(String texto);
    Boolean verificar(String textoPlano, String hash);
}

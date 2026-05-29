package com.biblioteca.auth.infraestructure.security_encrypter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.biblioteca.auth.domain.model.gateway.EncriptadorGateway;
import org.springframework.stereotype.Component;

@Component
public class BcryptEncriptadorImpl implements EncriptadorGateway {

    @Override
    public String encriptar(String texto) {
        return BCrypt.withDefaults().hashToString(12, texto.toCharArray());
    }

    @Override
    public Boolean verificar(String textoPlano, String hash) {
        return BCrypt.verifyer().verify(textoPlano.toCharArray(), hash).verified;
    }
}

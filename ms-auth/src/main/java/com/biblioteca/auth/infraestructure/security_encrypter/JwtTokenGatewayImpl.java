package com.biblioteca.auth.infraestructure.security_encrypter;
import com.biblioteca.auth.domain.model.Credencial;
import com.biblioteca.auth.domain.model.gateway.TokenGateway;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Component
public class JwtTokenGatewayImpl implements TokenGateway {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration-ms:28800000}")
    private long expirationMs;
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    @Override
    public String generarToken(Credencial credencial) {
        return Jwts.builder()
                .subject(credencial.getCorreo())
                .claim("rol", credencial.getRol())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey())
                .compact();
    }
    @Override
    public String extraerCorreo(String token) {
        return parsear(token).getSubject();
    }
    @Override
    public String extraerRol(String token) {
        return parsear(token).get("rol", String.class);
    }
    @Override
    public Boolean validarToken(String token) {
        try {
            parsear(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private Claims parsear(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }
}

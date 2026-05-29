package com.biblioteca.notificaciones.infraestructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Filtro JWT - valida el token generado por ms-auth de Biblioteca Digital.
 * CORRECCIÓN: si no hay token, se pasa al filterChain para que Spring Security
 * decida si el endpoint requiere autenticación o no (igual que sportshop).
 */
@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Sin token: dejamos pasar; Spring Security aplica las reglas de autorización
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String correo = claims.getSubject();
            String rol = claims.get("rol", String.class);

            if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                correo, null,
                                rol != null
                                        ? List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                                        : List.of(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            chain.doFilter(request, response);

        } catch (Exception e) {
            log.warn("JWT inválido en ms-notificaciones: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            chain.doFilter(request, response);
        }
    }
}

package com.biblioteca.notificaciones;

import com.biblioteca.notificaciones.infraestructure.entry_points.NotificacionController;
import com.biblioteca.notificaciones.infraestructure.entry_points.GlobalExceptionHandler;
import com.biblioteca.notificaciones.infraestructure.security.JwtAuthFilter;
import com.biblioteca.notificaciones.domain.usecase.NotificacionUseCase;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthFilter Notificaciones - pruebas a través de MockMvc")
class JwtAuthFilterNotificacionesTest {

    @Mock
    private NotificacionUseCase notificacionUseCase;

    private MockMvc mockMvc;
    private static final String SECRET = "TestSecretKeyForJWTBibliotecaDigital2025XYZ!!";

    @BeforeEach
    void setUp() {
        JwtAuthFilter filter = new JwtAuthFilter();
        ReflectionTestUtils.setField(filter, "secret", SECRET);
        NotificacionController controller = new NotificacionController(notificacionUseCase);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilters(filter)
                .build();
    }

    private String buildToken(String subject, String rol, long expirationMs) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(subject)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    @Test
    @DisplayName("Sin token: deja pasar (Spring Security decide)")
    void sinToken_dejaPassar() throws Exception {
        when(notificacionUseCase.listarTodas()).thenReturn(List.of());
        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Token válido con rol: autentica y deja pasar")
    void tokenValido_autentica() throws Exception {
        when(notificacionUseCase.listarTodas()).thenReturn(List.of());
        String token = buildToken("admin@test.com", "ADMIN", 3_600_000L);
        mockMvc.perform(get("/api/notificaciones")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Token válido sin rol: usa ESTUDIANTE")
    void tokenSinRol_usaEstudiante() throws Exception {
        when(notificacionUseCase.listarTodas()).thenReturn(List.of());
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("est@test.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000L))
                .signWith(key).compact();
        mockMvc.perform(get("/api/notificaciones")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Token expirado: deja pasar sin autenticación")
    void tokenExpirado_dejaPassar() throws Exception {
        when(notificacionUseCase.listarTodas()).thenReturn(List.of());
        String token = buildToken("user@test.com", "ADMIN", -1_000L);
        mockMvc.perform(get("/api/notificaciones")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Token malformado: deja pasar sin autenticación")
    void tokenMalformado_dejaPassar() throws Exception {
        when(notificacionUseCase.listarTodas()).thenReturn(List.of());
        mockMvc.perform(get("/api/notificaciones")
                        .header("Authorization", "Bearer esto.no.es.jwt"))
                .andExpect(status().isOk());
    }
}
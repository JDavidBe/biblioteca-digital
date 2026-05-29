package com.biblioteca.descargas;

import com.biblioteca.descargas.infraestructure.entry_points.DescargaController;
import com.biblioteca.descargas.infraestructure.entry_points.GlobalExceptionHandler;
import com.biblioteca.descargas.infraestructure.security.JwtAuthFilter;
import com.biblioteca.descargas.domain.usecase.DescargaUseCase;
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
@DisplayName("JwtAuthFilter - pruebas a través de MockMvc con filtro activo")
class JwtAuthFilterTest {

    @Mock
    private DescargaUseCase descargaUseCase;

    private MockMvc mockMvc;

    private static final String SECRET = "TestSecretKeyForJWTBibliotecaDigital2025XYZ!!";

    @BeforeEach
    void setUp() {
        JwtAuthFilter filter = new JwtAuthFilter();
        ReflectionTestUtils.setField(filter, "secret", SECRET);

        DescargaController controller = new DescargaController(descargaUseCase);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilters(filter)          // <-- registra el filtro como un Filter normal
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
    @DisplayName("Sin header Authorization → 401 Token requerido")
    void sinHeader_retorna401() throws Exception {
        mockMvc.perform(get("/api/descargas/usuario/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Header Basic (sin Bearer) → 401")
    void headerSinBearer_retorna401() throws Exception {
        mockMvc.perform(get("/api/descargas/usuario/1")
                        .header("Authorization", "Basic sometoken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Token JWT válido con rol ADMIN → 200")
    void tokenValido_conRol_retorna200() throws Exception {
        String token = buildToken("admin@test.com", "ADMIN", 3_600_000L);
        when(descargaUseCase.historialPorUsuario(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/descargas/usuario/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Token JWT válido sin claim rol → usa ESTUDIANTE, retorna 200")
    void tokenValidoSinRol_usaEstudiante() throws Exception {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("estudiante@test.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000L))
                .signWith(key)
                .compact();
        when(descargaUseCase.historialPorUsuario(2L)).thenReturn(List.of());

        mockMvc.perform(get("/api/descargas/usuario/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Token JWT expirado → 401")
    void tokenExpirado_retorna401() throws Exception {
        String token = buildToken("user@test.com", "ADMIN", -1_000L);

        mockMvc.perform(get("/api/descargas/usuario/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("Token con firma inválida → 401")
    void tokenFirmaInvalida_retorna401() throws Exception {
        mockMvc.perform(get("/api/descargas/usuario/1")
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0In0.firma_invalida"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("Token malformado → 401")
    void tokenMalformado_retorna401() throws Exception {
        mockMvc.perform(get("/api/descargas/usuario/1")
                        .header("Authorization", "Bearer esto.no.es.jwt"))
                .andExpect(status().isUnauthorized());
    }
}
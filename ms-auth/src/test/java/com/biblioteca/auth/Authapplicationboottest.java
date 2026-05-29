package com.biblioteca.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("AuthApplication - Carga del contexto")
class AuthApplicationBootTest {

    @Test
    @DisplayName("main(): el contexto de Spring arranca sin errores")
    void main_arrancaSinErrores() {
        AuthApplication.main(new String[]{});
    }
}
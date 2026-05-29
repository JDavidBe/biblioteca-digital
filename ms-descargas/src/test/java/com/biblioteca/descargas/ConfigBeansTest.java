package com.biblioteca.descargas;

import com.biblioteca.descargas.application.config.SecurityConfig;
import com.biblioteca.descargas.application.config.SwaggerConfig;
import com.biblioteca.descargas.application.config.UseCaseConfig;
import com.biblioteca.descargas.domain.model.gateway.DescargaGateway;
import com.biblioteca.descargas.domain.usecase.DescargaUseCase;
import com.biblioteca.descargas.infraestructure.security.JwtAuthFilter;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("Config classes - cobertura de beans de configuración")
class ConfigBeansTest {


    @Test
    @DisplayName("SwaggerConfig: openAPI bean no es nulo y contiene título correcto")
    void swaggerConfig_openApiBeanCreado() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI openAPI = config.openAPI();

        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).contains("ms-descargas");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearerAuth");
    }

    @Test
    @DisplayName("SwaggerConfig: SecurityScheme es de tipo HTTP bearer")
    void swaggerConfig_securitySchemeTipoBearer() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI openAPI = config.openAPI();

        var scheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
        assertThat(scheme).isNotNull();
        assertThat(scheme.getScheme()).isEqualTo("bearer");
        assertThat(scheme.getBearerFormat()).isEqualTo("JWT");
    }


    @Mock
    private DescargaGateway descargaGateway;

    @Test
    @DisplayName("UseCaseConfig: descargaUseCase bean no es nulo")
    void useCaseConfig_descargaUseCaseBeanCreado() {
        UseCaseConfig config = new UseCaseConfig();
        DescargaUseCase useCase = config.descargaUseCase(descargaGateway);

        assertThat(useCase).isNotNull();
    }

    @Test
    @DisplayName("SecurityConfig: corsConfigurationSource permite orígenes configurados")
    void securityConfig_corsConfigurationSource_origenesPermitidos() {
        JwtAuthFilter filter = mock(JwtAuthFilter.class);
        SecurityConfig securityConfig = new SecurityConfig(filter);

        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertThat(source).isNotNull();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/descargas");
        CorsConfiguration corsConfig = source.getCorsConfiguration(request);

        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOrigins()).contains("http://localhost:3000");
        assertThat(corsConfig.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertThat(corsConfig.getAllowCredentials()).isTrue();
    }

    @Test
    @DisplayName("SecurityConfig: corsConfigurationSource contiene origen de producción")
    void securityConfig_corsConfigurationSource_origenProduccion() {
        JwtAuthFilter filter = mock(JwtAuthFilter.class);
        SecurityConfig securityConfig = new SecurityConfig(filter);

        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        CorsConfiguration corsConfig = source.getCorsConfiguration(request);

        assertThat(corsConfig.getAllowedOrigins())
                .contains("https://biblioteca-digital-uhyg.onrender.com");
    }
}

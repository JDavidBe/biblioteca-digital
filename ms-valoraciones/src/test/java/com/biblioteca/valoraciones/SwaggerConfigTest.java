package com.biblioteca.valoraciones;

import com.biblioteca.valoraciones.application.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    @Test
    void openAPI_creaInstanciaCorrectamente() {
        OpenAPI api = new SwaggerConfig().openAPI();
        assertThat(api).isNotNull();
        assertThat(api.getInfo().getTitle()).isEqualTo("Biblioteca Digital - ms-valoraciones");
        assertThat(api.getInfo().getVersion()).isEqualTo("1.0.0");
    }
}
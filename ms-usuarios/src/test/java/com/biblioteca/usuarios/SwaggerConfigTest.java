
package com.biblioteca.usuarios;

import com.biblioteca.usuarios.application.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    @Test
    void openAPI_creaInstanciaCorrectamente() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI api = config.openAPI();
        assertThat(api).isNotNull();
        assertThat(api.getInfo().getTitle()).isEqualTo("Biblioteca Digital - ms-usuarios");
        assertThat(api.getInfo().getVersion()).isEqualTo("1.0.0");
    }
}
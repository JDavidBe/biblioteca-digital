package com.biblioteca.recursos;

import com.biblioteca.recursos.application.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    @Test
    void openApiBean_seCreaCorrectamente() {

        SwaggerConfig config = new SwaggerConfig();

        OpenAPI openAPI = config.openAPI();

        assertNotNull(openAPI);

        assertEquals(
                "Biblioteca Digital - ms-recursos",
                openAPI.getInfo().getTitle()
        );
    }
}
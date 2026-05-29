package com.biblioteca.recursos.infraestructure.almacenamiento;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class LocalAlmacenamientoImplTest {

    @Test
    void guardarArchivo_retornaNombreGenerado() {

        LocalAlmacenamientoImpl storage = new LocalAlmacenamientoImpl();

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "test.pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        String resultado = storage.guardarArchivo(archivo, "Mi Archivo.pdf");

        assertNotNull(resultado);
        assertTrue(resultado.contains("Mi_Archivo.pdf"));
    }

    @Test
    void obtenerArchivo_lanzaExcepcion() {

        LocalAlmacenamientoImpl storage = new LocalAlmacenamientoImpl();

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> storage.obtenerArchivo("archivo.pdf")
        );

        assertEquals("Use obtenerContenido desde la BD", ex.getMessage());
    }

    @Test
    void eliminarArchivo_noLanzaError() {

        LocalAlmacenamientoImpl storage = new LocalAlmacenamientoImpl();

        assertDoesNotThrow(() ->
                storage.eliminarArchivo("archivo.pdf")
        );
    }
}
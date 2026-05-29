package com.biblioteca.recursos.infraestructure.almacenamiento;
import com.biblioteca.recursos.domain.model.gateway.AlmacenamientoGateway;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;
@Component
public class LocalAlmacenamientoImpl implements AlmacenamientoGateway {
    @Override
    public String guardarArchivo(MultipartFile archivo, String nombreBase) {
        try {
            return UUID.randomUUID() + "_" + nombreBase.replaceAll("\\s+", "_");
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo: " + e.getMessage());
        }
    }
    @Override
    public byte[] obtenerArchivo(String clave) {
        throw new RuntimeException("Use obtenerContenido desde la BD");
    }
    @Override
    public void eliminarArchivo(String clave) {
        
    }
}

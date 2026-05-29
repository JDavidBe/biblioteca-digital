package com.biblioteca.recursos.domain.model.gateway;

import org.springframework.web.multipart.MultipartFile;

public interface AlmacenamientoGateway {
    String guardarArchivo(MultipartFile archivo, String nombreBase);
    byte[] obtenerArchivo(String rutaArchivo);
    void eliminarArchivo(String rutaArchivo);
}

package com.biblioteca.recursos.domain.usecase;

import com.biblioteca.recursos.domain.model.Recurso;
import com.biblioteca.recursos.domain.model.gateway.AlmacenamientoGateway;
import com.biblioteca.recursos.domain.model.gateway.RecursoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class RecursoUseCase {

    private final RecursoGateway recursoGateway;
    private final AlmacenamientoGateway almacenamientoGateway;

    public Recurso publicarRecurso(Recurso recurso, MultipartFile archivo) {
        if (recurso.getTitulo() == null || recurso.getTitulo().isBlank())
            throw new RuntimeException("El título es obligatorio");
        if (recurso.getArea() == null || recurso.getArea().isBlank())
            throw new RuntimeException("El área es obligatoria");
        if (archivo == null || archivo.isEmpty())
            throw new RuntimeException("Debe adjuntar un archivo");

        recurso.setCreadoEn(LocalDateTime.now());
        recurso.setDisponible(true);
        if (recurso.getTipo() == null || recurso.getTipo().isBlank())
            recurso.setTipo("PDF");

        return recursoGateway.guardar(recurso);
    }

    public Recurso obtenerPorId(Long id) {
        Recurso recurso = recursoGateway.buscarPorId(id);
        if (recurso == null)
            throw new RuntimeException("No existe recurso con id: " + id);
        return recurso;
    }

    public List<Recurso> buscarRecursos(String area, String grado, String q) {
        return recursoGateway.buscar(area, grado, q);
    }

    public List<Recurso> listarTodos() {
        return recursoGateway.listarDisponibles();
    }

    public List<Recurso> listarPorUsuario(Long usuarioId) {
        return recursoGateway.listarPorUsuario(usuarioId);
    }

    public byte[] descargarArchivo(Long id) {
        Recurso recurso = obtenerPorId(id);
        return recurso.getContenido();
    }

    public Recurso editarRecurso(Long id, Recurso recurso) {
        if (recursoGateway.buscarPorId(id) == null)
            throw new RuntimeException("No existe recurso con id: " + id);
        return recursoGateway.actualizar(id, recurso);
    }

    public void eliminarRecurso(Long id) {
        obtenerPorId(id);
        recursoGateway.eliminar(id);
    }
}

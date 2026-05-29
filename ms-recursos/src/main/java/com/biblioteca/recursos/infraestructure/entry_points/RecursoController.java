package com.biblioteca.recursos.infraestructure.entry_points;
import com.biblioteca.recursos.application.dto.*;
import com.biblioteca.recursos.domain.model.Recurso;
import com.biblioteca.recursos.domain.usecase.RecursoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoController {
    private final RecursoUseCase recursoUseCase;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDTO<RecursoResponseDTO>> publicar(
            @RequestPart("datos") @Valid RecursoRequestDTO request,
            @RequestPart("archivo") MultipartFile archivo) throws Exception {
        Recurso recurso = new Recurso();
        recurso.setTitulo(request.getTitulo());
        recurso.setDescripcion(request.getDescripcion());
        recurso.setArea(request.getArea());
        recurso.setGrado(request.getGrado());
        recurso.setTipo(request.getTipo());
        recurso.setSubidoPorId(request.getSubidoPorId());
        recurso.setSubidoPorCorreo(request.getSubidoPorCorreo());
        recurso.setNombreArchivo(archivo.getOriginalFilename());
        recurso.setTamanoBytes(archivo.getSize());
        recurso.setContenido(archivo.getBytes());
        Recurso creado = recursoUseCase.publicarRecurso(recurso, archivo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(true, "Recurso publicado", toResponse(creado)));
    }
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<RecursoResponseDTO>>> listar(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String grado,
            @RequestParam(required = false) String q) {
        List<Recurso> lista = (area != null || grado != null || q != null)
                ? recursoUseCase.buscarRecursos(area, grado, q)
                : recursoUseCase.listarTodos();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Recursos obtenidos",
                lista.stream().map(this::toResponse).toList()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RecursoResponseDTO>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Recurso encontrado",
                toResponse(recursoUseCase.obtenerPorId(id))));
    }
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponseDTO<List<RecursoResponseDTO>>> listarPorUsuario(
            @PathVariable Long usuarioId) {
        List<RecursoResponseDTO> lista = recursoUseCase.listarPorUsuario(usuarioId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Recursos del usuario", lista));
    }
    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargar(@PathVariable Long id) {
        Recurso recurso = recursoUseCase.obtenerPorId(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + recurso.getNombreArchivo() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(recurso.getContenido());
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RecursoResponseDTO>> editar(
            @PathVariable Long id, @RequestBody RecursoUpdateDTO request) {
        Recurso recurso = new Recurso();
        recurso.setTitulo(request.getTitulo());
        recurso.setDescripcion(request.getDescripcion());
        recurso.setArea(request.getArea());
        recurso.setGrado(request.getGrado());
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Recurso actualizado",
                toResponse(recursoUseCase.editarRecurso(id, recurso))));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(@PathVariable Long id) {
        recursoUseCase.eliminarRecurso(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Recurso eliminado", null));
    }
    private RecursoResponseDTO toResponse(Recurso r) {
        return new RecursoResponseDTO(r.getId(), r.getTitulo(), r.getDescripcion(),
                r.getArea(), r.getGrado(), r.getTipo(), r.getNombreArchivo(),
                r.getTamanoBytes(), r.getSubidoPorCorreo(), r.getCreadoEn(), r.getDisponible());
    }
}

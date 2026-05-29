package com.biblioteca.descargas.infraestructure.entry_points;

import com.biblioteca.descargas.application.dto.*;
import com.biblioteca.descargas.domain.model.Descarga;
import com.biblioteca.descargas.domain.model.EstadisticaDescarga;
import com.biblioteca.descargas.domain.usecase.DescargaUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/descargas")
@RequiredArgsConstructor
public class DescargaController {

    private final DescargaUseCase descargaUseCase;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<DescargaResponseDTO>> registrar(
            @Valid @RequestBody DescargaRequestDTO request) {
        Descarga descarga = new Descarga();
        descarga.setRecursoId(request.getRecursoId());
        descarga.setTituloRecurso(request.getTituloRecurso());
        descarga.setUsuarioId(request.getUsuarioId());
        descarga.setUsuarioCorreo(request.getUsuarioCorreo());
        descarga.setIpOrigen(request.getIpOrigen());
        Descarga registrada = descargaUseCase.registrarDescarga(descarga);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(true, "Descarga registrada", toResponse(registrada)));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponseDTO<List<DescargaResponseDTO>>> historialPorUsuario(
            @PathVariable Long usuarioId) {
        List<DescargaResponseDTO> lista = descargaUseCase.historialPorUsuario(usuarioId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Historial del usuario", lista));
    }

    @GetMapping("/recurso/{recursoId}")
    public ResponseEntity<ApiResponseDTO<List<DescargaResponseDTO>>> historialPorRecurso(
            @PathVariable Long recursoId) {
        List<DescargaResponseDTO> lista = descargaUseCase.historialPorRecurso(recursoId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Historial del recurso", lista));
    }

    @GetMapping("/recurso/{recursoId}/total")
    public ResponseEntity<ApiResponseDTO<Long>> totalPorRecurso(@PathVariable Long recursoId) {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Total descargas",
                descargaUseCase.contarPorRecurso(recursoId)));
    }

    @GetMapping("/usuario/{usuarioId}/total")
    public ResponseEntity<ApiResponseDTO<Long>> totalPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Total descargas del usuario",
                descargaUseCase.contarPorUsuario(usuarioId)));
    }

    @GetMapping("/recientes")
    public ResponseEntity<ApiResponseDTO<List<DescargaResponseDTO>>> recientes(
            @RequestParam(defaultValue = "20") Integer limite) {
        List<DescargaResponseDTO> lista = descargaUseCase.descargasRecientes(limite)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Descargas recientes", lista));
    }

    @GetMapping("/top")
    public ResponseEntity<ApiResponseDTO<List<EstadisticaDescargaDTO>>> topDescargados(
            @RequestParam(defaultValue = "10") Integer limite) {
        List<EstadisticaDescarga> top = descargaUseCase.topMasDescargados(limite);
        List<EstadisticaDescargaDTO> dtos = top.stream()
                .map(e -> new EstadisticaDescargaDTO(e.getRecursoId(), e.getTituloRecurso(), e.getTotalDescargas()))
                .toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Top recursos más descargados", dtos));
    }

    private DescargaResponseDTO toResponse(Descarga d) {
        return new DescargaResponseDTO(d.getId(), d.getRecursoId(), d.getTituloRecurso(),
                d.getUsuarioId(), d.getUsuarioCorreo(), d.getDescargadoEn());
    }
}

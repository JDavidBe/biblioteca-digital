package com.biblioteca.valoraciones.infraestructure.entry_points;

import com.biblioteca.valoraciones.application.dto.*;
import com.biblioteca.valoraciones.domain.model.ResumenValoracion;
import com.biblioteca.valoraciones.domain.model.Valoracion;
import com.biblioteca.valoraciones.domain.usecase.ValoracionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/valoraciones")
@RequiredArgsConstructor
public class ValoracionController {

    private final ValoracionUseCase valoracionUseCase;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ValoracionResponseDTO>> calificar(
            @Valid @RequestBody ValoracionRequestDTO request) {
        Valoracion valoracion = new Valoracion();
        valoracion.setRecursoId(request.getRecursoId());
        valoracion.setUsuarioId(request.getUsuarioId());
        valoracion.setUsuarioCorreo(request.getUsuarioCorreo());
        valoracion.setPuntuacion(request.getPuntuacion());
        valoracion.setComentario(request.getComentario());
        Valoracion creada = valoracionUseCase.calificar(valoracion);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(true, "Valoración registrada", toResponse(creada)));
    }

    @GetMapping("/recurso/{recursoId}")
    public ResponseEntity<ApiResponseDTO<List<ValoracionResponseDTO>>> porRecurso(
            @PathVariable Long recursoId) {
        List<ValoracionResponseDTO> lista = valoracionUseCase.obtenerPorRecurso(recursoId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Valoraciones del recurso", lista));
    }

    @GetMapping("/recurso/{recursoId}/resumen")
    public ResponseEntity<ApiResponseDTO<ResumenValoracionDTO>> resumen(@PathVariable Long recursoId) {
        ResumenValoracion resumen = valoracionUseCase.resumenPorRecurso(recursoId);
        ResumenValoracionDTO dto = new ResumenValoracionDTO(
                resumen.getRecursoId(), resumen.getPromedio(), resumen.getTotalValoraciones());
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Resumen de valoraciones", dto));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponseDTO<List<ValoracionResponseDTO>>> porUsuario(
            @PathVariable Long usuarioId) {
        List<ValoracionResponseDTO> lista = valoracionUseCase.obtenerPorUsuario(usuarioId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Valoraciones del usuario", lista));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(@PathVariable Long id) {
        valoracionUseCase.eliminarValoracion(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Valoración eliminada", null));
    }

    private ValoracionResponseDTO toResponse(Valoracion v) {
        return new ValoracionResponseDTO(v.getId(), v.getRecursoId(), v.getUsuarioId(),
                v.getUsuarioCorreo(), v.getPuntuacion(), v.getComentario(), v.getCreadoEn());
    }
}

package com.biblioteca.reportes.infraestructure.entry_points;

import com.biblioteca.reportes.application.dto.*;
import com.biblioteca.reportes.domain.model.RegistroActividad;
import com.biblioteca.reportes.domain.model.ResumenGeneral;
import com.biblioteca.reportes.domain.usecase.ReporteUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteUseCase reporteUseCase;

    @PostMapping("/actividad")
    public ResponseEntity<ApiResponseDTO<ActividadResponseDTO>> registrar(
            @Valid @RequestBody ActividadRequestDTO request) {
        RegistroActividad actividad = new RegistroActividad();
        actividad.setTipoEvento(request.getTipoEvento());
        actividad.setEntidadId(request.getEntidadId());
        actividad.setEntidadTipo(request.getEntidadTipo());
        actividad.setUsuarioId(request.getUsuarioId());
        actividad.setUsuarioCorreo(request.getUsuarioCorreo());
        actividad.setDetalle(request.getDetalle());
        RegistroActividad creada = reporteUseCase.registrarEvento(actividad);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(true, "Actividad registrada", toResponse(creada)));
    }

    @GetMapping("/resumen")
    public ResponseEntity<ApiResponseDTO<ResumenGeneralDTO>> resumen() {
        ResumenGeneral r = reporteUseCase.resumenGeneral();
        ResumenGeneralDTO dto = new ResumenGeneralDTO(r.getTotalEventos(), r.getEventosHoy(),
                r.getEventosSemana(), r.getEventosMes(), r.getEventoMasFrecuente());
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Resumen general", dto));
    }

    @GetMapping("/actividad/recientes")
    public ResponseEntity<ApiResponseDTO<List<ActividadResponseDTO>>> recientes(
            @RequestParam(defaultValue = "50") Integer limite) {
        List<ActividadResponseDTO> lista = reporteUseCase.actividadReciente(limite)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Actividad reciente", lista));
    }

    @GetMapping("/actividad/tipo/{tipo}")
    public ResponseEntity<ApiResponseDTO<List<ActividadResponseDTO>>> porTipo(
            @PathVariable String tipo) {
        List<ActividadResponseDTO> lista = reporteUseCase.actividadPorTipo(tipo)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Actividad por tipo", lista));
    }

    @GetMapping("/actividad/usuario/{usuarioId}")
    public ResponseEntity<ApiResponseDTO<List<ActividadResponseDTO>>> porUsuario(
            @PathVariable Long usuarioId) {
        List<ActividadResponseDTO> lista = reporteUseCase.actividadPorUsuario(usuarioId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Actividad del usuario", lista));
    }

    @GetMapping("/actividad/periodo")
    public ResponseEntity<ApiResponseDTO<List<ActividadResponseDTO>>> porPeriodo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        List<ActividadResponseDTO> lista = reporteUseCase.actividadEnPeriodo(desde, hasta)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Actividad en el periodo", lista));
    }

    private ActividadResponseDTO toResponse(RegistroActividad r) {
        return new ActividadResponseDTO(r.getId(), r.getTipoEvento(), r.getEntidadId(),
                r.getEntidadTipo(), r.getUsuarioId(), r.getUsuarioCorreo(),
                r.getDetalle(), r.getOcurridoEn());
    }
}

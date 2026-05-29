package com.biblioteca.notificaciones.infraestructure.entry_points;

import com.biblioteca.notificaciones.application.dto.*;
import com.biblioteca.notificaciones.domain.model.Notificacion;
import com.biblioteca.notificaciones.domain.usecase.NotificacionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionUseCase notificacionUseCase;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<NotificacionResponseDTO>> enviar(
            @Valid @RequestBody NotificacionRequestDTO request) {
        Notificacion n = new Notificacion();
        n.setDestinatarioCorreo(request.getDestinatarioCorreo());
        n.setDestinatarioTelefono(request.getDestinatarioTelefono());
        n.setDestinatarioId(request.getDestinatarioId());
        n.setCanal(request.getCanal());
        n.setTipo(request.getTipo());
        n.setAsunto(request.getAsunto());
        n.setMensaje(request.getMensaje());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(true, "Notificación enviada", toResponse(notificacionUseCase.enviarNotificacion(n))));
    }

    @PostMapping("/bienvenida")
    public ResponseEntity<ApiResponseDTO<NotificacionResponseDTO>> bienvenida(
            @Valid @RequestBody BienvenidaRequestDTO request) {
        Notificacion n = notificacionUseCase.enviarBienvenida(
                request.getCorreo(), request.getTelefono(), request.getUsuarioId(), request.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(true, "Bienvenida enviada", toResponse(n)));
    }

    @PostMapping("/nuevo-recurso")
    public ResponseEntity<ApiResponseDTO<NotificacionResponseDTO>> nuevoRecurso(
            @Valid @RequestBody RecursoNotificacionRequestDTO request) {
        Notificacion n = notificacionUseCase.notificarNuevoRecurso(
                request.getCorreo(), request.getTelefono(), request.getUsuarioId(), request.getTituloRecurso());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(true, "Notificación de recurso enviada", toResponse(n)));
    }

    @PostMapping("/descarga")
    public ResponseEntity<ApiResponseDTO<NotificacionResponseDTO>> descarga(
            @Valid @RequestBody RecursoNotificacionRequestDTO request) {
        Notificacion n = notificacionUseCase.notificarDescarga(
                request.getCorreo(), request.getTelefono(), request.getUsuarioId(), request.getTituloRecurso());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(true, "Confirmación de descarga enviada", toResponse(n)));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<NotificacionResponseDTO>>> listarTodas() {
        List<NotificacionResponseDTO> lista = notificacionUseCase.listarTodas()
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Notificaciones obtenidas", lista));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponseDTO<List<NotificacionResponseDTO>>> pendientes() {
        List<NotificacionResponseDTO> lista = notificacionUseCase.listarPendientes()
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Notificaciones pendientes", lista));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponseDTO<List<NotificacionResponseDTO>>> porUsuario(
            @PathVariable Long usuarioId) {
        List<NotificacionResponseDTO> lista = notificacionUseCase.listarPorDestinatario(usuarioId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Notificaciones del usuario", lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<NotificacionResponseDTO>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Notificación encontrada",
                toResponse(notificacionUseCase.obtenerPorId(id))));
    }

    private NotificacionResponseDTO toResponse(Notificacion n) {
        return new NotificacionResponseDTO(
                n.getId(), n.getDestinatarioCorreo(), n.getDestinatarioTelefono(),
                n.getDestinatarioId(), n.getCanal(), n.getTipo(), n.getAsunto(),
                n.getMensaje(), n.getEnviada(), n.getEnviadaSms(),
                n.getCreadaEn(), n.getEnviadaEn());
    }
}

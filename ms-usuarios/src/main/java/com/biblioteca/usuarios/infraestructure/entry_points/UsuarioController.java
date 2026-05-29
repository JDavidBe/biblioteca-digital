package com.biblioteca.usuarios.infraestructure.entry_points;

import com.biblioteca.usuarios.application.dto.*;
import com.biblioteca.usuarios.domain.model.Usuario;
import com.biblioteca.usuarios.domain.usecase.UsuarioUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<UsuarioResponseDTO>> crear(@Valid @RequestBody UsuarioRequestDTO request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setInstitucion(request.getInstitucion());
        usuario.setGrado(request.getGrado());
        usuario.setRol(request.getRol());
        Usuario creado = usuarioUseCase.crearUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(true, "Usuario creado", toResponse(creado)));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<UsuarioResponseDTO>>> listar(
            @RequestParam(required = false) String rol) {
        List<Usuario> lista = rol != null ? usuarioUseCase.listarPorRol(rol) : usuarioUseCase.listarTodos();
        List<UsuarioResponseDTO> response = lista.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Usuarios obtenidos", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UsuarioResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Usuario encontrado",
                toResponse(usuarioUseCase.obtenerPorId(id))));
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<ApiResponseDTO<UsuarioResponseDTO>> obtenerPorCorreo(@PathVariable String correo) {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Usuario encontrado",
                toResponse(usuarioUseCase.obtenerPorCorreo(correo))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UsuarioResponseDTO>> actualizar(
            @PathVariable Long id, @RequestBody UsuarioUpdateDTO request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setInstitucion(request.getInstitucion());
        usuario.setGrado(request.getGrado());
        usuario.setRol(request.getRol());
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Usuario actualizado",
                toResponse(usuarioUseCase.modificarUsuario(id, usuario))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(@PathVariable Long id) {
        usuarioUseCase.eliminarUsuario(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Usuario eliminado", null));
    }

    private UsuarioResponseDTO toResponse(Usuario u) {
        return new UsuarioResponseDTO(u.getId(), u.getNombre(), u.getCorreo(),
                u.getInstitucion(), u.getGrado(), u.getRol(), u.getActivo(), u.getCreadoEn());
    }
}

package com.biblioteca.auth.infraestructure.entry_points;
import com.biblioteca.auth.application.dto.*;
import com.biblioteca.auth.domain.model.Credencial;
import com.biblioteca.auth.domain.usecase.AuthUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthUseCase authUseCase;
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        Credencial credencial = authUseCase.iniciarSesionCompleto(request.getCorreo(), request.getPassword());
        String token = authUseCase.generarToken(credencial);
        LoginResponseDTO response = new LoginResponseDTO(credencial.getId(), token, "Bearer", request.getCorreo(), credencial.getRol());
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Sesión iniciada", response));
    }
    @PostMapping("/registro")
    public ResponseEntity<ApiResponseDTO<String>> registro(@Valid @RequestBody RegistroRequestDTO request) {
        Credencial credencial = new Credencial();
        credencial.setCorreo(request.getCorreo());
        credencial.setPasswordHash(request.getPassword());
        credencial.setRol(request.getRol());
        authUseCase.registrar(credencial);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(true, "Cuenta creada exitosamente", request.getCorreo()));
    }
    @GetMapping("/validar")
    public ResponseEntity<ApiResponseDTO<ValidacionTokenResponseDTO>> validarToken(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Boolean valido = authUseCase.validarToken(token);
        String correo = valido ? authUseCase.extraerCorreo(token) : null;
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Validación completada",
                new ValidacionTokenResponseDTO(valido, correo)));
    }
    @DeleteMapping("/credencial/{correo}")
    public ResponseEntity<ApiResponseDTO<String>> eliminarCredencial(@PathVariable String correo) {
        authUseCase.eliminarCredencial(correo);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Credencial eliminada", correo));
    }
}

package com.colegio.api_gateway.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Autenticación de usuarios con JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
               description = "Autentica un usuario con su RUT y contraseña, devolviendo un token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa - devuelve token, rut y role"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String rut = request.get("rut");
        String password = request.get("password");

        Optional<Map<String, String>> resultado = authService.autenticar(rut, password);

        if (resultado.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "RUT o contraseña incorrectos"));
        }

        return ResponseEntity.ok(resultado.get());
    }

    @PostMapping("/cambiar-contrasena")
    @Operation(summary = "Cambiar contraseña",
               description = "Cambia la contraseña del usuario autenticado. Requiere la contraseña actual.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultado de la operación"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<?> cambiarContrasena(@RequestBody Map<String, String> request) {
        String rut = request.get("rut");
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (rut == null || currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "exito", false,
                "error", "RUT, contraseña actual y nueva contraseña son requeridos"
            ));
        }

        if (newPassword.length() < 4) {
            return ResponseEntity.badRequest().body(Map.of(
                "exito", false,
                "error", "La nueva contraseña debe tener al menos 4 caracteres"
            ));
        }

        Map<String, Object> resultado = authService.cambiarContrasena(rut, currentPassword, newPassword);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/resetear-contrasena")
    @Operation(summary = "Resetear contraseña (solo UTP)",
               description = "Resetea la contraseña de cualquier usuario sin verificar la actual. Solo para administradores UTP.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultado de la operación"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<?> resetearContrasena(@RequestBody Map<String, String> request) {
        String rut = request.get("rut");
        String newPassword = request.get("newPassword");

        if (rut == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "exito", false,
                "error", "RUT y nueva contraseña son requeridos"
            ));
        }

        if (newPassword.length() < 4) {
            return ResponseEntity.badRequest().body(Map.of(
                "exito", false,
                "error", "La nueva contraseña debe tener al menos 4 caracteres"
            ));
        }

        Map<String, Object> resultado = authService.resetearContrasena(rut, newPassword);
        return ResponseEntity.ok(resultado);
    }
}
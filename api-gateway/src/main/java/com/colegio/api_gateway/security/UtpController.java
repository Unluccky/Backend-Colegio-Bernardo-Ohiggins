package com.colegio.api_gateway.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/usuarios-utp")
@RequiredArgsConstructor
@Tag(name = "Usuarios UTP", description = "Gestión de usuarios administradores UTP (solo accesible por usuarios UTP)")
public class UtpController {

    private final AuthService authService;

    @GetMapping
    @Operation(summary = "Listar usuarios UTP",
               description = "Obtiene la lista de todos los usuarios administradores UTP registrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios UTP")
    public ResponseEntity<List<Map<String, String>>> listar() {
        return ResponseEntity.ok(authService.listarUsuariosUtp());
    }

    @PostMapping
    @Operation(summary = "Crear usuario UTP",
               description = "Registra un nuevo usuario administrador UTP en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario UTP creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o el RUT ya existe")
    })
    public ResponseEntity<?> crear(@RequestBody Map<String, String> request) {
        String rut = request.get("rut");
        String password = request.get("password");

        if (rut == null || rut.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "RUT y contraseña son requeridos"));
        }

        if (authService.existeUsuarioUtp(rut)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ya existe un usuario UTP con ese RUT"));
        }

        Map<String, String> usuario = authService.crearUsuarioUtp(rut, password);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{rut}")
    @Operation(summary = "Eliminar usuario UTP",
               description = "Elimina un usuario administrador UTP del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario UTP eliminado"),
        @ApiResponse(responseCode = "404", description = "Usuario UTP no encontrado")
    })
    public ResponseEntity<?> eliminar(@PathVariable String rut) {
        boolean eliminado = authService.eliminarUsuarioUtp(rut);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

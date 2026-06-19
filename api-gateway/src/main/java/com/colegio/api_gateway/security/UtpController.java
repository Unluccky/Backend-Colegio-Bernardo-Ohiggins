package com.colegio.api_gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/usuarios-utp")
@RequiredArgsConstructor
public class UtpController {

    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> listar() {
        return ResponseEntity.ok(authService.listarUsuariosUtp());
    }

    @PostMapping
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
    public ResponseEntity<?> eliminar(@PathVariable String rut) {
        boolean eliminado = authService.eliminarUsuarioUtp(rut);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

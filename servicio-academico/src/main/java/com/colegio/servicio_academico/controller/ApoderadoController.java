package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.model.Apoderado;
import com.colegio.servicio_academico.service.ApoderadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/apoderados")
@RequiredArgsConstructor
@Tag(name = "Apoderados", description = "Gestión de apoderados y tutores")
public class ApoderadoController {

    private final ApoderadoService service;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping
    @Operation(summary = "Listar todos los apoderados")
    @ApiResponse(responseCode = "200", description = "Lista de apoderados")
    public List<Apoderado> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar apoderado por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Apoderado encontrado"),
        @ApiResponse(responseCode = "404", description = "Apoderado no encontrado")
    })
    public ResponseEntity<Apoderado> buscar(
            @Parameter(description = "ID del apoderado") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/rut/{rut}")
    @Operation(summary = "Buscar apoderado por RUT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Apoderado encontrado"),
        @ApiResponse(responseCode = "404", description = "Apoderado no encontrado")
    })
    public ResponseEntity<Apoderado> buscarPorRut(
            @Parameter(description = "RUT del apoderado") @PathVariable String rut) {
        return ResponseEntity.ok(service.buscarPorRut(rut));
    }

    @GetMapping("/estudiante/{estudianteId}")
    @Operation(summary = "Buscar apoderados por estudiante",
               description = "Obtiene los apoderados asociados a un estudiante")
    @ApiResponse(responseCode = "200", description = "Lista de apoderados del estudiante")
    public ResponseEntity<List<Apoderado>> buscarPorEstudiante(
            @Parameter(description = "ID del estudiante") @PathVariable Long estudianteId) {
        return ResponseEntity.ok(service.buscarPorEstudiante(estudianteId));
    }

    @PostMapping
    @Operation(summary = "Crear apoderado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Apoderado creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Apoderado> crear(@RequestBody Apoderado apoderado) {
        if (apoderado.getContrasena() != null && !apoderado.getContrasena().isEmpty()) {
            String hash = passwordEncoder.encode(apoderado.getContrasena());
            apoderado.setContrasena(hash);
        }
        return ResponseEntity.ok(service.guardar(apoderado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar apoderado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Apoderado actualizado"),
        @ApiResponse(responseCode = "404", description = "Apoderado no encontrado")
    })
    public ResponseEntity<Apoderado> actualizar(
            @Parameter(description = "ID del apoderado") @PathVariable Long id,
            @RequestBody Apoderado datos) {
        return ResponseEntity.ok(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar apoderado")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Apoderado eliminado"),
        @ApiResponse(responseCode = "404", description = "Apoderado no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del apoderado") @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.model.Profesor;
import com.colegio.servicio_academico.service.ProfesorService;
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
@RequestMapping("/api/profesores")
@RequiredArgsConstructor
@Tag(name = "Profesores", description = "Gestión de profesores del colegio")
public class ProfesorController {

    private final ProfesorService service;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping
    @Operation(summary = "Listar todos los profesores")
    @ApiResponse(responseCode = "200", description = "Lista de profesores")
    public List<Profesor> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar profesor por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profesor encontrado"),
        @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    public ResponseEntity<Profesor> buscar(
            @Parameter(description = "ID del profesor") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear profesor")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profesor creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Profesor> crear(@RequestBody Profesor profesor) {
        if (profesor.getContrasena() != null && !profesor.getContrasena().isEmpty()) {
            String hash = passwordEncoder.encode(profesor.getContrasena());
            profesor.setContrasena(hash);
        }
        return ResponseEntity.ok(service.guardar(profesor));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar profesor")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profesor actualizado"),
        @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    public ResponseEntity<Profesor> actualizar(
            @Parameter(description = "ID del profesor") @PathVariable Long id,
            @RequestBody Profesor datos) {
        return ResponseEntity.ok(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar profesor")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Profesor eliminado"),
        @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del profesor") @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
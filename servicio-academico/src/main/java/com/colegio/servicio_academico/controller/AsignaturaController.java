package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.model.Asignatura;
import com.colegio.servicio_academico.service.AsignaturaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/asignaturas")
@RequiredArgsConstructor
@Tag(name = "Asignaturas", description = "Gestión de asignaturas o materias")
public class AsignaturaController {

    private final AsignaturaService service;

    @GetMapping
    @Operation(summary = "Listar todas las asignaturas")
    @ApiResponse(responseCode = "200", description = "Lista de asignaturas")
    public List<Asignatura> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar asignatura por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignatura encontrada"),
        @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    public ResponseEntity<Asignatura> buscar(
            @Parameter(description = "ID de la asignatura") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear asignatura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignatura creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Asignatura> crear(@RequestBody Asignatura asignatura) {
        return ResponseEntity.ok(service.guardar(asignatura));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar asignatura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignatura actualizada"),
        @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    public ResponseEntity<Asignatura> actualizar(
            @Parameter(description = "ID de la asignatura") @PathVariable Long id,
            @RequestBody Asignatura datos) {
        return ResponseEntity.ok(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar asignatura")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Asignatura eliminada"),
        @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la asignatura") @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
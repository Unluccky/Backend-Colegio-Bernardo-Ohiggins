package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.model.Evaluacion;
import com.colegio.servicio_academico.service.EvaluacionService;
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
@RequestMapping("/api/evaluaciones")
@RequiredArgsConstructor
@Tag(name = "Evaluaciones", description = "Gestión de evaluaciones académicas")
public class EvaluacionController {

    private final EvaluacionService service;

    @GetMapping
    @Operation(summary = "Listar todas las evaluaciones")
    @ApiResponse(responseCode = "200", description = "Lista de evaluaciones")
    public List<Evaluacion> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar evaluación por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evaluación encontrada"),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<Evaluacion> buscar(
            @Parameter(description = "ID de la evaluación") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear evaluación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evaluación creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Evaluacion> crear(@RequestBody Evaluacion evaluacion) {
        return ResponseEntity.ok(service.guardar(evaluacion));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar evaluación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evaluación actualizada"),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<Evaluacion> actualizar(
            @Parameter(description = "ID de la evaluación") @PathVariable Long id,
            @RequestBody Evaluacion datos) {
        return ResponseEntity.ok(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar evaluación")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Evaluación eliminada"),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la evaluación") @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
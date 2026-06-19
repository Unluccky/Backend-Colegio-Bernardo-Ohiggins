package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.model.Nota;
import com.colegio.servicio_academico.service.NotaService;
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
@RequestMapping("/api/notas")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Gestión de calificaciones")
public class NotaController {

    private final NotaService service;

    @GetMapping
    @Operation(summary = "Listar todas las notas")
    @ApiResponse(responseCode = "200", description = "Lista de notas")
    public List<Nota> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar nota por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota encontrada"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    public ResponseEntity<Nota> buscar(
            @Parameter(description = "ID de la nota") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/estudiante/{estudianteId}")
    @Operation(summary = "Buscar notas por estudiante",
               description = "Obtiene todas las notas de un estudiante específico")
    @ApiResponse(responseCode = "200", description = "Lista de notas del estudiante")
    public List<Nota> buscarPorEstudiante(
            @Parameter(description = "ID del estudiante") @PathVariable Long estudianteId) {
        return service.buscarPorEstudiante(estudianteId);
    }

    @PostMapping
    @Operation(summary = "Registrar nota",
               description = "Registra una nueva calificación para un estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota registrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Nota> crear(@RequestBody Nota nota) {
        return ResponseEntity.ok(service.guardar(nota));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar nota")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Nota eliminada"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la nota") @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
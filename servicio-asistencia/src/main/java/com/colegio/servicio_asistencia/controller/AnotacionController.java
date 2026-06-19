package com.colegio.servicio_asistencia.controller;

import com.colegio.servicio_asistencia.model.Anotacion;
import com.colegio.servicio_asistencia.service.AnotacionService;
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
@RequestMapping("/api/anotaciones")
@RequiredArgsConstructor
@Tag(name = "Anotaciones", description = "Gestión de anotaciones y observaciones de estudiantes")
public class AnotacionController {

    private final AnotacionService service;

    @GetMapping
    @Operation(summary = "Listar todas las anotaciones")
    @ApiResponse(responseCode = "200", description = "Lista de anotaciones")
    public List<Anotacion> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar anotación por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Anotación encontrada"),
        @ApiResponse(responseCode = "404", description = "Anotación no encontrada")
    })
    public ResponseEntity<Anotacion> buscar(
            @Parameter(description = "ID de la anotación") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/estudiante/{estudianteId}")
    @Operation(summary = "Buscar anotaciones por estudiante")
    @ApiResponse(responseCode = "200", description = "Lista de anotaciones del estudiante")
    public List<Anotacion> buscarPorEstudiante(
            @Parameter(description = "ID del estudiante") @PathVariable Long estudianteId) {
        return service.buscarPorEstudiante(estudianteId);
    }

    @PostMapping
    @Operation(summary = "Crear anotación",
               description = "Registra una nueva anotación para un estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Anotación creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Anotacion> crear(@RequestBody Anotacion anotacion) {
        return ResponseEntity.ok(service.guardar(anotacion));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar anotación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Anotación actualizada"),
        @ApiResponse(responseCode = "404", description = "Anotación no encontrada")
    })
    public ResponseEntity<Anotacion> actualizar(
            @Parameter(description = "ID de la anotación") @PathVariable Long id,
            @RequestBody Anotacion datos) {
        return ResponseEntity.ok(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar anotación")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Anotación eliminada"),
        @ApiResponse(responseCode = "404", description = "Anotación no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la anotación") @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
package com.colegio.servicio_asistencia.controller;

import com.colegio.servicio_asistencia.model.Asistencia;
import com.colegio.servicio_asistencia.service.AsistenciaService;
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
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
@Tag(name = "Asistencias", description = "Registro de asistencia de estudiantes")
public class AsistenciaController {

    private final AsistenciaService service;

    @GetMapping
    @Operation(summary = "Listar todas las asistencias")
    @ApiResponse(responseCode = "200", description = "Lista de asistencias")
    public List<Asistencia> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar asistencia por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asistencia encontrada"),
        @ApiResponse(responseCode = "404", description = "Asistencia no encontrada")
    })
    public ResponseEntity<Asistencia> buscar(
            @Parameter(description = "ID del registro") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/estudiante/{estudianteId}")
    @Operation(summary = "Buscar asistencias por estudiante")
    @ApiResponse(responseCode = "200", description = "Lista de asistencias del estudiante")
    public List<Asistencia> buscarPorEstudiante(
            @Parameter(description = "ID del estudiante") @PathVariable Long estudianteId) {
        return service.buscarPorEstudiante(estudianteId);
    }

    @GetMapping("/por-clase")
    @Operation(summary = "Buscar asistencias por asignatura y fecha",
               description = "Retorna las asistencias registradas para una asignatura en una fecha específica")
    @ApiResponse(responseCode = "200", description = "Lista de asistencias")
    public List<Asistencia> buscarPorAsignaturaYFecha(
            @RequestParam Long asignaturaId,
            @RequestParam String fecha) {
        return service.buscarPorAsignaturaYFecha(asignaturaId, java.time.LocalDate.parse(fecha));
    }

    @PostMapping("/batch")
    @Operation(summary = "Registrar múltiples asistencias",
               description = "Guarda una lista de asistencias en una sola operación. Si ya existe un registro para el mismo estudiante+asignatura+fecha, lo actualiza.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asistencias guardadas"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<List<Asistencia>> crearBatch(@RequestBody List<Asistencia> asistencias) {
        return ResponseEntity.ok(service.guardarBatch(asistencias));
    }

    @PostMapping
    @Operation(summary = "Registrar asistencia",
               description = "Registra la asistencia de un estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asistencia registrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Asistencia> crear(@RequestBody Asistencia asistencia) {
        return ResponseEntity.ok(service.guardar(asistencia));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar asistencia",
               description = "Actualiza el estado de una asistencia existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asistencia actualizada"),
        @ApiResponse(responseCode = "404", description = "Asistencia no encontrada")
    })
    public ResponseEntity<Asistencia> actualizar(
            @Parameter(description = "ID del registro") @PathVariable Long id,
            @RequestBody Asistencia datos) {
        return ResponseEntity.ok(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar asistencia")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Asistencia eliminada"),
        @ApiResponse(responseCode = "404", description = "Asistencia no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del registro") @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
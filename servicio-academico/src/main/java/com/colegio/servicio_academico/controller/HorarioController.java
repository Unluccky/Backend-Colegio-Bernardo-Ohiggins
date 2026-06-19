package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.model.Horario;
import com.colegio.servicio_academico.service.HorarioService;
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
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
@Tag(name = "Horarios", description = "Gestión de horarios de clases")
public class HorarioController {

    private final HorarioService service;

    @GetMapping
    @Operation(summary = "Listar todos los horarios",
               description = "Obtiene todos los horarios de clases registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de horarios")
    public List<Horario> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar horario por ID",
               description = "Obtiene un horario específico por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Horario encontrado"),
        @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    })
    public ResponseEntity<Horario> buscar(
            @Parameter(description = "ID del horario") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/curso/{curso}")
    @Operation(summary = "Buscar horarios por curso",
               description = "Obtiene todos los horarios de un curso específico")
    @ApiResponse(responseCode = "200", description = "Lista de horarios del curso")
    public List<Horario> buscarPorCurso(
            @Parameter(description = "Nivel del curso (1-12)") @PathVariable Integer curso) {
        return service.buscarPorCurso(curso);
    }

    @GetMapping("/profesor/{profesorId}")
    @Operation(summary = "Buscar horarios por profesor",
               description = "Obtiene los horarios asignados a un profesor")
    @ApiResponse(responseCode = "200", description = "Lista de horarios del profesor")
    public List<Horario> buscarPorProfesor(
            @Parameter(description = "ID del profesor") @PathVariable Long profesorId) {
        return service.buscarPorProfesor(profesorId);
    }

    @GetMapping("/curso/{curso}/dia/{dia}")
    @Operation(summary = "Buscar horarios por curso y día",
               description = "Obtiene los horarios de un curso en un día específico")
    @ApiResponse(responseCode = "200", description = "Lista de horarios filtrada")
    public List<Horario> buscarPorCursoYDia(
            @Parameter(description = "Nivel del curso") @PathVariable Integer curso,
            @Parameter(description = "Día de la semana (1=Lunes...5=Viernes)") @PathVariable Integer dia) {
        return service.buscarPorCursoYDia(curso, dia);
    }

    @PostMapping
    @Operation(summary = "Crear horario",
               description = "Registra un nuevo bloque horario en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Horario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Horario> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del horario a crear")
            @RequestBody Horario horario) {
        return ResponseEntity.ok(service.guardar(horario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar horario",
               description = "Actualiza los datos de un horario existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Horario actualizado"),
        @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    })
    public ResponseEntity<Horario> actualizar(
            @Parameter(description = "ID del horario a actualizar") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del horario")
            @RequestBody Horario datos) {
        return ResponseEntity.ok(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar horario",
               description = "Elimina un horario del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Horario eliminado"),
        @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del horario a eliminar") @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.model.Estudiante;
import com.colegio.servicio_academico.service.EstudianteService;
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
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
@Tag(name = "Estudiantes", description = "Gestión de estudiantes del colegio")
public class EstudianteController {

    private final EstudianteService service;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping
    @Operation(summary = "Listar todos los estudiantes",
               description = "Obtiene la lista completa de estudiantes registrados")
    @ApiResponse(responseCode = "200", description = "Lista de estudiantes")
    public List<Estudiante> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar estudiante por ID",
               description = "Obtiene los datos de un estudiante específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public ResponseEntity<Estudiante> buscar(
            @Parameter(description = "ID del estudiante") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/rut/{rut}")
    @Operation(summary = "Buscar estudiante por RUT",
               description = "Obtiene los datos de un estudiante usando su RUT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public ResponseEntity<Estudiante> buscarPorRut(
            @Parameter(description = "RUT del estudiante (ej: 12.345.678-9)") @PathVariable String rut) {
        return ResponseEntity.ok(service.buscarPorRut(rut));
    }

    @PostMapping
    @Operation(summary = "Crear estudiante",
               description = "Registra un nuevo estudiante en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estudiante creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Estudiante> crear(@RequestBody Estudiante estudiante) {
        if (estudiante.getPassword() != null && !estudiante.getPassword().isEmpty()) {
            String hash = passwordEncoder.encode(estudiante.getPassword());
            estudiante.setPassword(hash);
        }
        return ResponseEntity.ok(service.guardar(estudiante));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estudiante",
               description = "Actualiza los datos de un estudiante existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estudiante actualizado"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public ResponseEntity<Estudiante> actualizar(
            @Parameter(description = "ID del estudiante") @PathVariable Long id,
            @RequestBody Estudiante datos) {
        return ResponseEntity.ok(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar estudiante",
               description = "Elimina un estudiante del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Estudiante eliminado"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del estudiante") @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
package com.colegio.servicio_comunicaciones.controller;

import com.colegio.servicio_comunicaciones.model.Notificacion;
import com.colegio.servicio_comunicaciones.service.NotificacionService;
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
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Sistema de notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    @GetMapping
    @Operation(summary = "Listar todas las notificaciones")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones")
    public List<Notificacion> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar notificación por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<Notificacion> buscar(
            @Parameter(description = "ID de la notificación") @PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/destinatario/{destinatarioId}")
    @Operation(summary = "Buscar notificaciones por destinatario")
    @ApiResponse(responseCode = "200", description = "Notificaciones del destinatario")
    public List<Notificacion> buscarPorDestinatario(
            @Parameter(description = "ID del destinatario") @PathVariable Long destinatarioId) {
        return service.buscarPorDestinatario(destinatarioId);
    }

    @PostMapping
    @Operation(summary = "Crear notificación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificación creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Notificacion> crear(@RequestBody Notificacion notificacion) {
        return ResponseEntity.ok(service.guardar(notificacion));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar notificación")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notificación eliminada"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la notificación") @PathVariable String id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
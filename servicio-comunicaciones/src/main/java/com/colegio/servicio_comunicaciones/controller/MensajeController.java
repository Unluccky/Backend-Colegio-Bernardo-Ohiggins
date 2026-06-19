package com.colegio.servicio_comunicaciones.controller;

import com.colegio.servicio_comunicaciones.model.Mensaje;
import com.colegio.servicio_comunicaciones.service.MensajeService;
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
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
@Tag(name = "Mensajes", description = "Sistema de mensajería interna")
public class MensajeController {

    private final MensajeService service;

    @GetMapping
    @Operation(summary = "Listar todos los mensajes")
    @ApiResponse(responseCode = "200", description = "Lista de mensajes")
    public List<Mensaje> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar mensaje por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mensaje encontrado"),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado")
    })
    public ResponseEntity<Mensaje> buscar(
            @Parameter(description = "ID del mensaje (MongoDB)") @PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/destinatario/{destinatarioId}")
    @Operation(summary = "Buscar mensajes por destinatario")
    @ApiResponse(responseCode = "200", description = "Lista de mensajes del destinatario")
    public List<Mensaje> buscarPorDestinatario(
            @Parameter(description = "ID del destinatario") @PathVariable Long destinatarioId) {
        return service.buscarPorDestinatario(destinatarioId);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Buscar mensajes por usuario (ID + tipo)",
               description = "Retorna mensajes donde el usuario es remitente o destinatario, filtrados por su ID y tipo (ESTUDIANTE, PROFESOR, APODERADO, UTP)")
    @ApiResponse(responseCode = "200", description = "Lista de mensajes del usuario")
    public List<Mensaje> buscarPorUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId,
            @Parameter(description = "Tipo de usuario: ESTUDIANTE, PROFESOR, APODERADO o UTP") @RequestParam String tipo) {
        return service.buscarPorUsuario(usuarioId, tipo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mensaje",
               description = "Actualiza el mensaje (ej: marcar como leído)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mensaje actualizado"),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado")
    })
    public ResponseEntity<Mensaje> actualizar(
            @Parameter(description = "ID del mensaje") @PathVariable String id,
            @RequestBody Mensaje mensaje) {
        return ResponseEntity.ok(service.actualizar(id, mensaje));
    }

    @PostMapping
    @Operation(summary = "Enviar mensaje",
               description = "Envía un nuevo mensaje a un destinatario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mensaje enviado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Mensaje> crear(@RequestBody Mensaje mensaje) {
        return ResponseEntity.ok(service.guardar(mensaje));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mensaje")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Mensaje eliminado"),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del mensaje") @PathVariable String id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
package com.colegio.servicio_comunicaciones.controller;

import com.colegio.servicio_comunicaciones.config.GlobalExceptionHandler;
import com.colegio.servicio_comunicaciones.model.Mensaje;
import com.colegio.servicio_comunicaciones.service.MensajeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MensajeController - Pruebas de capa web")
class MensajeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MensajeService service;

    @InjectMocks
    private MensajeController controller;

    private Mensaje mensaje;
    private String jsonMensaje;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mensaje = Mensaje.builder()
                .id("msg-001")
                .remitenteId(1L)
                .remitenteTipo("PROFESOR")
                .destinatarioId(2L)
                .destinatarioTipo("ESTUDIANTE")
                .asunto("Reunión de apoderados")
                .contenido("Se convoca a reunión el día viernes")
                .fechaEnvio(LocalDateTime.of(2025, 5, 1, 10, 0))
                .leido(false)
                .build();

        jsonMensaje = """
                {"remitenteId":1,"remitenteTipo":"PROFESOR","destinatarioId":2,"destinatarioTipo":"ESTUDIANTE","asunto":"Reunión","contenido":"Contenido del mensaje"}
                """;
    }

    @Test
    @DisplayName("GET /api/mensajes - retorna lista")
    void listar_deberiaRetornarLista() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(mensaje));

        mockMvc.perform(get("/api/mensajes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].asunto").value("Reunión de apoderados"));
    }

    @Test
    @DisplayName("GET /api/mensajes - lista vacía")
    void listar_sinDatos_retornaVacio() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/mensajes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/mensajes/{id} - retorna mensaje")
    void buscarPorId_existente_retornaMensaje() throws Exception {
        when(service.buscarPorId("msg-001")).thenReturn(mensaje);

        mockMvc.perform(get("/api/mensajes/{id}", "msg-001").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.asunto").value("Reunión de apoderados"));
    }

    @Test
    @DisplayName("GET /api/mensajes/{id} - 404 cuando no existe")
    void buscarPorId_noExiste_retorna404() throws Exception {
        when(service.buscarPorId("no-existe")).thenThrow(new RuntimeException("Mensaje no encontrado: no-existe"));

        mockMvc.perform(get("/api/mensajes/{id}", "no-existe").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Mensaje no encontrado: no-existe"));
    }

    @Test
    @DisplayName("GET /api/mensajes/destinatario/{id} - retorna mensajes por destinatario")
    void buscarPorDestinatario_retornaMensajes() throws Exception {
        when(service.buscarPorDestinatario(2L)).thenReturn(List.of(mensaje));

        mockMvc.perform(get("/api/mensajes/destinatario/{id}", 2L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].destinatarioId").value(2));
    }

    @Test
    @DisplayName("GET /api/mensajes/usuario/{id} - retorna mensajes por usuario y tipo")
    void buscarPorUsuario_retornaMensajes() throws Exception {
        when(service.buscarPorUsuario(1L, "PROFESOR")).thenReturn(List.of(mensaje));

        mockMvc.perform(get("/api/mensajes/usuario/{id}", 1L)
                        .param("tipo", "PROFESOR")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("POST /api/mensajes - crea mensaje")
    void crear_conDatosValidos_retornaOk() throws Exception {
        when(service.guardar(any(Mensaje.class))).thenReturn(mensaje);

        mockMvc.perform(post("/api/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMensaje)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("msg-001"));
    }

    @Test
    @DisplayName("PUT /api/mensajes/{id} - actualiza mensaje")
    void actualizar_existente_retornaActualizado() throws Exception {
        Mensaje actualizado = Mensaje.builder()
                .id("msg-001").remitenteId(1L).destinatarioId(2L)
                .asunto("Reunión de apoderados")
                .contenido("Contenido actualizado")
                .fechaEnvio(LocalDateTime.of(2025, 5, 1, 10, 0))
                .leido(true).build();

        when(service.actualizar(eq("msg-001"), any(Mensaje.class))).thenReturn(actualizado);

        String jsonActualizar = """
                {"leido":true}
                """;

        mockMvc.perform(put("/api/mensajes/{id}", "msg-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizar)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leido").value(true));
    }

    @Test
    @DisplayName("DELETE /api/mensajes/{id} - elimina mensaje")
    void eliminar_existente_retorna204() throws Exception {
        doNothing().when(service).eliminar("msg-001");

        mockMvc.perform(delete("/api/mensajes/{id}", "msg-001"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).eliminar("msg-001");
    }
}

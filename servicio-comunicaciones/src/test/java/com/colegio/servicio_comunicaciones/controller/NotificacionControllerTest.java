package com.colegio.servicio_comunicaciones.controller;

import com.colegio.servicio_comunicaciones.config.GlobalExceptionHandler;
import com.colegio.servicio_comunicaciones.model.Notificacion;
import com.colegio.servicio_comunicaciones.model.TipoNotificacion;
import com.colegio.servicio_comunicaciones.service.NotificacionService;
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
@DisplayName("NotificacionController - Pruebas de capa web")
class NotificacionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificacionService service;

    @InjectMocks
    private NotificacionController controller;

    private Notificacion notificacion;
    private String jsonNotificacion;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        notificacion = Notificacion.builder()
                .id("notif-001")
                .destinatarioId(2L)
                .titulo("Anotación negativa registrada")
                .mensaje("Su pupilo recibió una anotación negativa")
                .fecha(LocalDateTime.of(2025, 5, 1, 9, 0))
                .leida(false)
                .tipo(TipoNotificacion.ANOTACION)
                .build();

        jsonNotificacion = """
                {"destinatarioId":2,"titulo":"Anotación negativa","mensaje":"Su pupilo recibió una anotación","tipo":"ANOTACION"}
                """;
    }

    @Test
    @DisplayName("GET /api/notificaciones - retorna lista")
    void listar_deberiaRetornarLista() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(notificacion));

        mockMvc.perform(get("/api/notificaciones").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("ANOTACION"));
    }

    @Test
    @DisplayName("GET /api/notificaciones - lista vacía")
    void listar_sinDatos_retornaVacio() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/notificaciones").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/notificaciones/{id} - retorna notificación")
    void buscarPorId_existente_retornaNotificacion() throws Exception {
        when(service.buscarPorId("notif-001")).thenReturn(notificacion);

        mockMvc.perform(get("/api/notificaciones/{id}", "notif-001").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Anotación negativa registrada"));
    }

    @Test
    @DisplayName("GET /api/notificaciones/{id} - 404 cuando no existe")
    void buscarPorId_noExiste_retorna404() throws Exception {
        when(service.buscarPorId("no-existe")).thenThrow(new RuntimeException("Notificacion no encontrada: no-existe"));

        mockMvc.perform(get("/api/notificaciones/{id}", "no-existe").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Notificacion no encontrada: no-existe"));
    }

    @Test
    @DisplayName("GET /api/notificaciones/destinatario/{id} - retorna por destinatario")
    void buscarPorDestinatario_retornaNotificaciones() throws Exception {
        when(service.buscarPorDestinatario(2L)).thenReturn(List.of(notificacion));

        mockMvc.perform(get("/api/notificaciones/destinatario/{id}", 2L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].destinatarioId").value(2));
    }

    @Test
    @DisplayName("POST /api/notificaciones - crea notificación")
    void crear_conDatosValidos_retornaOk() throws Exception {
        when(service.guardar(any(Notificacion.class))).thenReturn(notificacion);

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonNotificacion)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("notif-001"));
    }

    @Test
    @DisplayName("DELETE /api/notificaciones/{id} - elimina notificación")
    void eliminar_existente_retorna204() throws Exception {
        doNothing().when(service).eliminar("notif-001");

        mockMvc.perform(delete("/api/notificaciones/{id}", "notif-001"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).eliminar("notif-001");
    }
}

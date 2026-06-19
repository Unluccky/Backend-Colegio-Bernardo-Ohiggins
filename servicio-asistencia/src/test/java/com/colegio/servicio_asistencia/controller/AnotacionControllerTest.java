package com.colegio.servicio_asistencia.controller;

import com.colegio.servicio_asistencia.config.GlobalExceptionHandler;
import com.colegio.servicio_asistencia.model.Anotacion;
import com.colegio.servicio_asistencia.model.TipoAnotacion;
import com.colegio.servicio_asistencia.service.AnotacionService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnotacionController - Pruebas de capa web")
class AnotacionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AnotacionService service;

    @InjectMocks
    private AnotacionController controller;

    private Anotacion anotacion;
    private String jsonAnotacion;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        anotacion = Anotacion.builder()
                .id(1L)
                .estudianteId(10L)
                .profesorId(3L)
                .descripcion("Mal comportamiento en clase")
                .tipo(TipoAnotacion.NEGATIVA)
                .fecha(LocalDate.of(2025, 4, 15))
                .build();

        jsonAnotacion = """
                {"estudianteId":10,"profesorId":3,"descripcion":"Mal comportamiento en clase","tipo":"NEGATIVA","fecha":"2025-04-15"}
                """;
    }

    @Test
    @DisplayName("GET /api/anotaciones - retorna lista")
    void listar_deberiaRetornarLista() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(anotacion));

        mockMvc.perform(get("/api/anotaciones").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("NEGATIVA"));
    }

    @Test
    @DisplayName("GET /api/anotaciones - lista vacía")
    void listar_sinDatos_retornaVacio() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/anotaciones").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/anotaciones/{id} - retorna anotación")
    void buscarPorId_existente_retornaAnotacion() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(anotacion);

        mockMvc.perform(get("/api/anotaciones/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Mal comportamiento en clase"));
    }

    @Test
    @DisplayName("GET /api/anotaciones/{id} - 404 cuando no existe")
    void buscarPorId_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RuntimeException("Anotacion no encontrada: 99"));

        mockMvc.perform(get("/api/anotaciones/{id}", 99L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Anotacion no encontrada: 99"));
    }

    @Test
    @DisplayName("GET /api/anotaciones/estudiante/{id} - retorna anotaciones del estudiante")
    void buscarPorEstudiante_retornaAnotaciones() throws Exception {
        when(service.buscarPorEstudiante(10L)).thenReturn(List.of(anotacion));

        mockMvc.perform(get("/api/anotaciones/estudiante/{id}", 10L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estudianteId").value(10));
    }

    @Test
    @DisplayName("POST /api/anotaciones - crea anotación")
    void crear_conDatosValidos_retornaOk() throws Exception {
        when(service.guardar(any(Anotacion.class))).thenReturn(anotacion);

        mockMvc.perform(post("/api/anotaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAnotacion)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/anotaciones/{id} - actualiza anotación")
    void actualizar_existente_retornaActualizado() throws Exception {
        Anotacion actualizada = Anotacion.builder()
                .id(1L).estudianteId(10L).profesorId(3L)
                .descripcion("Comportamiento mejorado")
                .tipo(TipoAnotacion.POSITIVA)
                .fecha(LocalDate.of(2025, 4, 16)).build();

        when(service.actualizar(eq(1L), any(Anotacion.class))).thenReturn(actualizada);

        String jsonActualizar = """
                {"descripcion":"Comportamiento mejorado","tipo":"POSITIVA","fecha":"2025-04-16","profesorId":3}
                """;

        mockMvc.perform(put("/api/anotaciones/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizar)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("POSITIVA"));
    }

    @Test
    @DisplayName("DELETE /api/anotaciones/{id} - elimina anotación")
    void eliminar_existente_retorna204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/anotaciones/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service, times(1)).eliminar(1L);
    }
}

package com.colegio.servicio_asistencia.controller;

import com.colegio.servicio_asistencia.config.GlobalExceptionHandler;
import com.colegio.servicio_asistencia.model.Asistencia;
import com.colegio.servicio_asistencia.model.EstadoAsistencia;
import com.colegio.servicio_asistencia.service.AsistenciaService;
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
@DisplayName("AsistenciaController - Pruebas de capa web")
class AsistenciaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AsistenciaService service;

    @InjectMocks
    private AsistenciaController controller;

    private Asistencia asistencia;
    private String jsonAsistencia;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        asistencia = Asistencia.builder()
                .id(1L)
                .estudianteId(10L)
                .asignaturaId(5L)
                .fecha(LocalDate.of(2025, 4, 1))
                .estado(EstadoAsistencia.PRESENTE)
                .build();

        jsonAsistencia = """
                {"estudianteId":10,"asignaturaId":5,"fecha":"2025-04-01","estado":"PRESENTE"}
                """;
    }

    @Test
    @DisplayName("GET /api/asistencias - retorna lista")
    void listar_deberiaRetornarLista() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(asistencia));

        mockMvc.perform(get("/api/asistencias").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("PRESENTE"));
    }

    @Test
    @DisplayName("GET /api/asistencias - lista vacía")
    void listar_sinDatos_retornaVacio() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/asistencias").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/asistencias/{id} - retorna asistencia")
    void buscarPorId_existente_retornaAsistencia() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(asistencia);

        mockMvc.perform(get("/api/asistencias/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estudianteId").value(10))
                .andExpect(jsonPath("$.estado").value("PRESENTE"));
    }

    @Test
    @DisplayName("GET /api/asistencias/{id} - 404 cuando no existe")
    void buscarPorId_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RuntimeException("Asistencia no encontrada: 99"));

        mockMvc.perform(get("/api/asistencias/{id}", 99L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Asistencia no encontrada: 99"));
    }

    @Test
    @DisplayName("GET /api/asistencias/estudiante/{id} - retorna asistencias del estudiante")
    void buscarPorEstudiante_retornaAsistencias() throws Exception {
        when(service.buscarPorEstudiante(10L)).thenReturn(List.of(asistencia));

        mockMvc.perform(get("/api/asistencias/estudiante/{id}", 10L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estudianteId").value(10));
    }

    @Test
    @DisplayName("GET /api/asistencias/por-clase - retorna asistencias por asignatura y fecha")
    void buscarPorAsignaturaYFecha_retornaAsistencias() throws Exception {
        when(service.buscarPorAsignaturaYFecha(anyLong(), any(LocalDate.class)))
                .thenReturn(List.of(asistencia));

        mockMvc.perform(get("/api/asistencias/por-clase")
                        .param("asignaturaId", "5")
                        .param("fecha", "2025-04-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("POST /api/asistencias - crea asistencia")
    void crear_conDatosValidos_retornaOk() throws Exception {
        when(service.guardar(any(Asistencia.class))).thenReturn(asistencia);

        mockMvc.perform(post("/api/asistencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAsistencia)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/asistencias/batch - guarda múltiples asistencias")
    void crearBatch_conDatosValidos_retornaOk() throws Exception {
        when(service.guardarBatch(anyList())).thenReturn(List.of(asistencia));

        String jsonBatch = """
                [{"estudianteId":10,"asignaturaId":5,"fecha":"2025-04-01","estado":"PRESENTE"}]
                """;

        mockMvc.perform(post("/api/asistencias/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBatch)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PUT /api/asistencias/{id} - actualiza asistencia")
    void actualizar_existente_retornaActualizado() throws Exception {
        Asistencia actualizada = Asistencia.builder()
                .id(1L).estudianteId(10L).asignaturaId(5L)
                .fecha(LocalDate.of(2025, 4, 2))
                .estado(EstadoAsistencia.AUSENTE).build();

        when(service.actualizar(eq(1L), any(Asistencia.class))).thenReturn(actualizada);

        String jsonActualizar = """
                {"estado":"AUSENTE","fecha":"2025-04-02","asignaturaId":5}
                """;

        mockMvc.perform(put("/api/asistencias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizar)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("AUSENTE"));
    }

    @Test
    @DisplayName("DELETE /api/asistencias/{id} - elimina asistencia")
    void eliminar_existente_retorna204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/asistencias/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service, times(1)).eliminar(1L);
    }
}

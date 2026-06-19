package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.config.GlobalExceptionHandler;
import com.colegio.servicio_academico.model.Estudiante;
import com.colegio.servicio_academico.service.EstudianteService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EstudianteController - Pruebas de capa web")
class EstudianteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EstudianteService service;

    @InjectMocks
    private EstudianteController controller;

    private Estudiante estudiante;
    private String jsonEstudiante;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        estudiante = Estudiante.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .rut("12345678-9")
                .email("juan@colegio.cl")
                .curso(8)
                .build();

        jsonEstudiante = """
                {"nombre":"Juan","apellido":"Perez","rut":"12345678-9","email":"juan@colegio.cl","curso":8}
                """;
    }

    @Test
    @DisplayName("GET /api/estudiantes - retorna lista")
    void listar_deberiaRetornarLista() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(estudiante));

        mockMvc.perform(get("/api/estudiantes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    @DisplayName("GET /api/estudiantes - lista vacia")
    void listar_sinDatos_retornaVacio() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/estudiantes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/estudiantes/{id} - retorna estudiante")
    void buscarPorId_existente_retornaEstudiante() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(estudiante);

        mockMvc.perform(get("/api/estudiantes/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/estudiantes/{id} - 404 cuando no existe")
    void buscarPorId_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RuntimeException("Estudiante no encontrado: 99"));

        mockMvc.perform(get("/api/estudiantes/{id}", 99L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Estudiante no encontrado: 99"));
    }

    @Test
    @DisplayName("GET /api/estudiantes/rut/{rut} - busca por RUT")
    void buscarPorRut_existente_retornaEstudiante() throws Exception {
        when(service.buscarPorRut("12345678-9")).thenReturn(estudiante);

        mockMvc.perform(get("/api/estudiantes/rut/{rut}", "12345678-9").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("12345678-9"));
    }

    @Test
    @DisplayName("POST /api/estudiantes - crea estudiante")
    void crear_conDatosValidos_retornaOk() throws Exception {
        when(service.guardar(any(Estudiante.class))).thenReturn(estudiante);

        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEstudiante)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/estudiantes/{id} - actualiza estudiante")
    void actualizar_existente_retornaActualizado() throws Exception {
        Estudiante actualizado = Estudiante.builder()
                .id(1L).nombre("Juan Carlos").apellido("Perez")
                .rut("12345678-9").email("jc@colegio.cl").curso(9).build();

        when(service.actualizar(eq(1L), any(Estudiante.class))).thenReturn(actualizado);

        String json = "{\"nombre\":\"Juan Carlos\",\"apellido\":\"Perez\",\"rut\":\"12345678-9\",\"email\":\"jc@colegio.cl\",\"curso\":9}";

        mockMvc.perform(put("/api/estudiantes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Carlos"))
                .andExpect(jsonPath("$.curso").value(9));
    }

    @Test
    @DisplayName("DELETE /api/estudiantes/{id} - elimina estudiante")
    void eliminar_existente_retorna204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/estudiantes/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service, times(1)).eliminar(1L);
    }
}

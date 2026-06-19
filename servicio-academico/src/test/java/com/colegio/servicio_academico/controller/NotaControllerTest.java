package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.config.GlobalExceptionHandler;
import com.colegio.servicio_academico.model.Estudiante;
import com.colegio.servicio_academico.model.Evaluacion;
import com.colegio.servicio_academico.model.Nota;
import com.colegio.servicio_academico.service.NotaService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotaController - Pruebas de capa web")
class NotaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotaService service;

    @InjectMocks
    private NotaController controller;

    private Nota nota;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        Estudiante est = Estudiante.builder().id(1L).nombre("Juan").apellido("Perez").build();
        Evaluacion ev = Evaluacion.builder().id(1L).nombre("Prueba 1").build();
        nota = Nota.builder().id(1L).estudiante(est).evaluacion(ev).valor(6.5).build();
    }

    @Test
    @DisplayName("GET /api/notas - retorna lista")
    void listar_deberiaRetornarLista() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(nota));

        mockMvc.perform(get("/api/notas").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].valor").value(6.5));
    }

    @Test
    @DisplayName("GET /api/notas/{id} - retorna nota")
    void buscarPorId_existente_retornaNota() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(nota);

        mockMvc.perform(get("/api/notas/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(6.5));
    }

    @Test
    @DisplayName("GET /api/notas/{id} - 404 cuando no existe")
    void buscarPorId_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RuntimeException("Nota no encontrada: 99"));

        mockMvc.perform(get("/api/notas/{id}", 99L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/notas/estudiante/{id} - notas por estudiante")
    void buscarPorEstudiante_retornaNotas() throws Exception {
        when(service.buscarPorEstudiante(1L)).thenReturn(List.of(nota));

        mockMvc.perform(get("/api/notas/estudiante/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("POST /api/notas - crea nota")
    void crear_conDatosValidos_retornaOk() throws Exception {
        when(service.guardar(any(Nota.class))).thenReturn(nota);

        mockMvc.perform(post("/api/notas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudiante\":{\"id\":1},\"evaluacion\":{\"id\":1},\"valor\":6.5}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(6.5));
    }

    @Test
    @DisplayName("DELETE /api/notas/{id} - elimina nota")
    void eliminar_existente_retorna204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/notas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service, times(1)).eliminar(1L);
    }
}

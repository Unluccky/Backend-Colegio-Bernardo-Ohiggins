package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.config.GlobalExceptionHandler;
import com.colegio.servicio_academico.model.Evaluacion;
import com.colegio.servicio_academico.model.TipoEvaluacion;
import com.colegio.servicio_academico.service.EvaluacionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluacionController - Pruebas de capa web")
class EvaluacionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EvaluacionService service;

    @InjectMocks
    private EvaluacionController controller;

    private Evaluacion evaluacion;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        evaluacion = Evaluacion.builder()
                .id(1L).nombre("Prueba 1")
                .fecha(LocalDate.of(2026, 6, 15))
                .tipo(TipoEvaluacion.PRUEBA).build();
    }

    @Test @DisplayName("GET /api/evaluaciones - retorna lista")
    void listar_deberiaRetornarLista() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(evaluacion));
        mockMvc.perform(get("/api/evaluaciones").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test @DisplayName("GET /api/evaluaciones/{id} - retorna evaluacion")
    void buscarPorId_existente_retornaEvaluacion() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(evaluacion);
        mockMvc.perform(get("/api/evaluaciones/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Prueba 1"));
    }

    @Test @DisplayName("GET /api/evaluaciones/{id} - 404")
    void buscarPorId_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RuntimeException("Evaluacion no encontrada: 99"));
        mockMvc.perform(get("/api/evaluaciones/{id}", 99L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test @DisplayName("POST /api/evaluaciones - crea")
    void crear_conDatosValidos_retornaOk() throws Exception {
        when(service.guardar(any(Evaluacion.class))).thenReturn(evaluacion);
        mockMvc.perform(post("/api/evaluaciones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Prueba 1\",\"fecha\":\"2026-06-15\",\"tipo\":\"PRUEBA\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Prueba 1"));
    }

    @Test @DisplayName("PUT /api/evaluaciones/{id} - actualiza")
    void actualizar_existente_retornaActualizado() throws Exception {
        Evaluacion act = Evaluacion.builder().id(1L).nombre("Prueba 1 Act.").tipo(TipoEvaluacion.TAREA).build();
        when(service.actualizar(eq(1L), any(Evaluacion.class))).thenReturn(act);
        mockMvc.perform(put("/api/evaluaciones/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Prueba 1 Act.\",\"tipo\":\"TAREA\"}").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Prueba 1 Act."));
    }

    @Test @DisplayName("DELETE /api/evaluaciones/{id} - elimina")
    void eliminar_existente_retorna204() throws Exception {
        doNothing().when(service).eliminar(1L);
        mockMvc.perform(delete("/api/evaluaciones/{id}", 1L)).andExpect(status().isNoContent());
        verify(service, times(1)).eliminar(1L);
    }
}

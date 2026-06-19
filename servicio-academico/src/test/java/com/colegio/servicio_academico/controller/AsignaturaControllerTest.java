package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.config.GlobalExceptionHandler;
import com.colegio.servicio_academico.model.Asignatura;
import com.colegio.servicio_academico.model.Profesor;
import com.colegio.servicio_academico.service.AsignaturaService;
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
@DisplayName("AsignaturaController - Pruebas de capa web")
class AsignaturaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AsignaturaService service;

    @InjectMocks
    private AsignaturaController controller;

    private Asignatura asignatura;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        Profesor p = Profesor.builder().id(1L).nombre("Maria").apellido("Lopez").build();
        asignatura = Asignatura.builder().id(1L).nombre("Matematicas").nivelCurso(8).profesor(p).build();
    }

    @Test @DisplayName("GET /api/asignaturas - retorna lista")
    void listar_deberiaRetornarLista() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(asignatura));
        mockMvc.perform(get("/api/asignaturas").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test @DisplayName("GET /api/asignaturas/{id} - retorna asignatura")
    void buscarPorId_existente_retornaAsignatura() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(asignatura);
        mockMvc.perform(get("/api/asignaturas/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Matematicas"));
    }

    @Test @DisplayName("GET /api/asignaturas/{id} - 404")
    void buscarPorId_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RuntimeException("Asignatura no encontrada: 99"));
        mockMvc.perform(get("/api/asignaturas/{id}", 99L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test @DisplayName("POST /api/asignaturas - crea")
    void crear_conDatosValidos_retornaOk() throws Exception {
        when(service.guardar(any(Asignatura.class))).thenReturn(asignatura);
        mockMvc.perform(post("/api/asignaturas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Matematicas\",\"nivelCurso\":8,\"profesor\":{\"id\":1}}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Matematicas"));
    }

    @Test @DisplayName("PUT /api/asignaturas/{id} - actualiza")
    void actualizar_existente_retornaActualizado() throws Exception {
        Asignatura act = Asignatura.builder().id(1L).nombre("Matematicas Avanzadas").nivelCurso(9).build();
        when(service.actualizar(eq(1L), any(Asignatura.class))).thenReturn(act);
        mockMvc.perform(put("/api/asignaturas/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Matematicas Avanzadas\",\"nivelCurso\":9}").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Matematicas Avanzadas"));
    }

    @Test @DisplayName("DELETE /api/asignaturas/{id} - elimina")
    void eliminar_existente_retorna204() throws Exception {
        doNothing().when(service).eliminar(1L);
        mockMvc.perform(delete("/api/asignaturas/{id}", 1L)).andExpect(status().isNoContent());
        verify(service, times(1)).eliminar(1L);
    }
}

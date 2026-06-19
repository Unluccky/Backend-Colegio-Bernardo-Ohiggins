package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.config.GlobalExceptionHandler;
import com.colegio.servicio_academico.model.Asignatura;
import com.colegio.servicio_academico.model.Horario;
import com.colegio.servicio_academico.model.Profesor;
import com.colegio.servicio_academico.service.HorarioService;
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
@DisplayName("HorarioController - Pruebas de capa web")
class HorarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HorarioService service;

    @InjectMocks
    private HorarioController controller;

    private Horario horario;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        Profesor p = Profesor.builder().id(1L).nombre("Maria").apellido("Lopez").build();
        Asignatura a = Asignatura.builder().id(1L).nombre("Matematicas").build();
        horario = Horario.builder().id(1L).asignatura(a).profesor(p)
                .curso(8).dia(1).horaInicio("08:00").horaFin("09:30").sala("A101").build();
    }

    @Test @DisplayName("GET /api/horarios - retorna lista")
    void listar_deberiaRetornarLista() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(horario));
        mockMvc.perform(get("/api/horarios").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test @DisplayName("GET /api/horarios/{id} - retorna horario")
    void buscarPorId_existente_retornaHorario() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(horario);
        mockMvc.perform(get("/api/horarios/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.sala").value("A101"));
    }

    @Test @DisplayName("GET /api/horarios/{id} - 404")
    void buscarPorId_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RuntimeException("Horario no encontrado: 99"));
        mockMvc.perform(get("/api/horarios/{id}", 99L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test @DisplayName("GET /api/horarios/curso/{curso}")
    void buscarPorCurso_retornaHorarios() throws Exception {
        when(service.buscarPorCurso(8)).thenReturn(List.of(horario));
        mockMvc.perform(get("/api/horarios/curso/{curso}", 8).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test @DisplayName("GET /api/horarios/profesor/{id}")
    void buscarPorProfesor_retornaHorarios() throws Exception {
        when(service.buscarPorProfesor(1L)).thenReturn(List.of(horario));
        mockMvc.perform(get("/api/horarios/profesor/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test @DisplayName("GET /api/horarios/curso/{c}/dia/{d}")
    void buscarPorCursoYDia_retornaHorarios() throws Exception {
        when(service.buscarPorCursoYDia(8, 1)).thenReturn(List.of(horario));
        mockMvc.perform(get("/api/horarios/curso/{c}/dia/{d}", 8, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test @DisplayName("POST /api/horarios - crea")
    void crear_conDatosValidos_retornaOk() throws Exception {
        when(service.guardar(any(Horario.class))).thenReturn(horario);
        mockMvc.perform(post("/api/horarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"curso\":8,\"dia\":1,\"horaInicio\":\"08:00\",\"horaFin\":\"09:30\",\"sala\":\"A101\",\"asignatura\":{\"id\":1},\"profesor\":{\"id\":1}}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.curso").value(8));
    }

    @Test @DisplayName("PUT /api/horarios/{id} - actualiza")
    void actualizar_existente_retornaActualizado() throws Exception {
        Horario act = Horario.builder().id(1L).curso(9).dia(2).horaInicio("10:00").horaFin("11:30").sala("B202").build();
        when(service.actualizar(eq(1L), any(Horario.class))).thenReturn(act);
        mockMvc.perform(put("/api/horarios/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"curso\":9,\"dia\":2,\"horaInicio\":\"10:00\",\"horaFin\":\"11:30\",\"sala\":\"B202\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.curso").value(9));
    }

    @Test @DisplayName("DELETE /api/horarios/{id} - elimina")
    void eliminar_existente_retorna204() throws Exception {
        doNothing().when(service).eliminar(1L);
        mockMvc.perform(delete("/api/horarios/{id}", 1L)).andExpect(status().isNoContent());
        verify(service, times(1)).eliminar(1L);
    }
}

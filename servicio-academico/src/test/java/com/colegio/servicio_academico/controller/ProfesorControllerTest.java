package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.config.GlobalExceptionHandler;
import com.colegio.servicio_academico.model.Profesor;
import com.colegio.servicio_academico.service.ProfesorService;
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
@DisplayName("ProfesorController - Pruebas de capa web")
class ProfesorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfesorService service;

    @InjectMocks
    private ProfesorController controller;

    private Profesor profesor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        profesor = Profesor.builder()
                .id(1L).nombre("Maria").apellido("Lopez")
                .rut("11111111-1").email("maria@colegio.cl").especialidad("Matematicas")
                .build();
    }

    @Test
    @DisplayName("GET /api/profesores - retorna lista")
    void listar_deberiaRetornarLista() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(profesor));

        mockMvc.perform(get("/api/profesores").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Maria"));
    }

    @Test
    @DisplayName("GET /api/profesores/{id} - retorna profesor")
    void buscarPorId_existente_retornaProfesor() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(profesor);

        mockMvc.perform(get("/api/profesores/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Maria"))
                .andExpect(jsonPath("$.especialidad").value("Matematicas"));
    }

    @Test
    @DisplayName("GET /api/profesores/{id} - 404 cuando no existe")
    void buscarPorId_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RuntimeException("Profesor no encontrado: 99"));

        mockMvc.perform(get("/api/profesores/{id}", 99L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/profesores - crea profesor")
    void crear_conDatosValidos_retornaOk() throws Exception {
        when(service.guardar(any(Profesor.class))).thenReturn(profesor);

        mockMvc.perform(post("/api/profesores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Maria\",\"apellido\":\"Lopez\",\"rut\":\"11111111-1\",\"email\":\"maria@colegio.cl\",\"especialidad\":\"Matematicas\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/profesores/{id} - actualiza profesor")
    void actualizar_existente_retornaActualizado() throws Exception {
        Profesor act = Profesor.builder().id(1L).nombre("Maria A.").apellido("Lopez").rut("11111111-1").especialidad("Fisica").build();
        when(service.actualizar(eq(1L), any(Profesor.class))).thenReturn(act);

        mockMvc.perform(put("/api/profesores/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Maria A.\",\"apellido\":\"Lopez\",\"especialidad\":\"Fisica\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Maria A."));
    }

    @Test
    @DisplayName("DELETE /api/profesores/{id} - elimina profesor")
    void eliminar_existente_retorna204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/profesores/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service, times(1)).eliminar(1L);
    }
}

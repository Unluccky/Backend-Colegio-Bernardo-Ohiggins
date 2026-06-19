package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.config.GlobalExceptionHandler;
import com.colegio.servicio_academico.model.Apoderado;
import com.colegio.servicio_academico.model.Estudiante;
import com.colegio.servicio_academico.service.ApoderadoService;
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
@DisplayName("ApoderadoController - Pruebas de capa web")
class ApoderadoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ApoderadoService service;

    @InjectMocks
    private ApoderadoController controller;

    private Apoderado apoderado;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        Estudiante est = Estudiante.builder().id(1L).nombre("Juan").apellido("Perez").build();
        apoderado = Apoderado.builder()
                .id(1L).nombre("Carlos").apellido("Gonzalez")
                .rut("98765432-1").email("carlos@correo.cl")
                .estudiante(est).build();
    }

    @Test @DisplayName("GET /api/apoderados - retorna lista")
    void listar_deberiaRetornarLista() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(apoderado));
        mockMvc.perform(get("/api/apoderados").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test @DisplayName("GET /api/apoderados/{id} - retorna apoderado")
    void buscarPorId_existente_retornaApoderado() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(apoderado);
        mockMvc.perform(get("/api/apoderados/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Carlos"));
    }

    @Test @DisplayName("GET /api/apoderados/{id} - 404")
    void buscarPorId_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RuntimeException("Apoderado no encontrado: 99"));
        mockMvc.perform(get("/api/apoderados/{id}", 99L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test @DisplayName("GET /api/apoderados/rut/{rut} - busca por RUT")
    void buscarPorRut_retornaApoderado() throws Exception {
        when(service.buscarPorRut("98765432-1")).thenReturn(apoderado);
        mockMvc.perform(get("/api/apoderados/rut/{rut}", "98765432-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.rut").value("98765432-1"));
    }

    @Test @DisplayName("GET /api/apoderados/estudiante/{id}")
    void buscarPorEstudiante_retornaApoderados() throws Exception {
        when(service.buscarPorEstudiante(1L)).thenReturn(List.of(apoderado));
        mockMvc.perform(get("/api/apoderados/estudiante/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test @DisplayName("POST /api/apoderados - crea")
    void crear_conDatosValidos_retornaOk() throws Exception {
        when(service.guardar(any(Apoderado.class))).thenReturn(apoderado);
        mockMvc.perform(post("/api/apoderados").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Carlos\",\"apellido\":\"Gonzalez\",\"rut\":\"98765432-1\",\"estudiante\":{\"id\":1}}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test @DisplayName("PUT /api/apoderados/{id} - actualiza")
    void actualizar_existente_retornaActualizado() throws Exception {
        Apoderado act = Apoderado.builder().id(1L).nombre("Carlos A.").apellido("Gonzalez").rut("98765432-1").build();
        when(service.actualizar(eq(1L), any(Apoderado.class))).thenReturn(act);
        mockMvc.perform(put("/api/apoderados/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Carlos A.\",\"apellido\":\"Gonzalez\"}").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Carlos A."));
    }

    @Test @DisplayName("DELETE /api/apoderados/{id} - elimina")
    void eliminar_existente_retorna204() throws Exception {
        doNothing().when(service).eliminar(1L);
        mockMvc.perform(delete("/api/apoderados/{id}", 1L)).andExpect(status().isNoContent());
        verify(service, times(1)).eliminar(1L);
    }
}

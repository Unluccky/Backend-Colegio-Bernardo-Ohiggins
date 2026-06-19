package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.config.GlobalExceptionHandler;
import com.colegio.servicio_academico.model.Apoderado;
import com.colegio.servicio_academico.model.Estudiante;
import com.colegio.servicio_academico.model.Profesor;
import com.colegio.servicio_academico.repository.ApoderadoRepository;
import com.colegio.servicio_academico.repository.EstudianteRepository;
import com.colegio.servicio_academico.repository.ProfesorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - Pruebas de capa web")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfesorRepository profesorRepo;

    @Mock
    private EstudianteRepository estudianteRepo;

    @Mock
    private ApoderadoRepository apoderadoRepo;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(profesorRepo, estudianteRepo, apoderadoRepo);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/validar - RUT no encontrado retorna valido=false")
    void validar_rutNoExiste_retornaNoEncontrado() throws Exception {
        when(profesorRepo.findByRut("99999999-9")).thenReturn(Optional.empty());
        when(estudianteRepo.findByRut("99999999-9")).thenReturn(Optional.empty());
        when(apoderadoRepo.findByRut("99999999-9")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"99999999-9\",\"password\":\"cualquier\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false))
                .andExpect(jsonPath("$.error").value("RUT no encontrado en el sistema"));
    }

    @Test
    @DisplayName("POST /api/auth/validar - RUT encontrado pero contrasena incorrecta")
    void validar_profesorEncontrado_passwordIncorrecta() throws Exception {
        Profesor p = Profesor.builder()
                .id(1L).nombre("Maria").apellido("Lopez").rut("11111111-1")
                .contrasena("$2a$10$bcryptplaceholder").build();

        when(profesorRepo.findByRut("11111111-1")).thenReturn(Optional.of(p));

        mockMvc.perform(post("/api/auth/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"11111111-1\",\"password\":\"incorrecta\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/validar - campos requeridos faltantes retorna 400")
    void validar_sinPassword_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/cambiar-contrasena - nueva password muy corta retorna 400")
    void cambiarContrasena_passwordCorta_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/cambiar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"11111111-1\",\"currentPassword\":\"a\",\"newPassword\":\"ab\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/resetear-contrasena - RUT no encontrado retorna exito=false")
    void resetearContrasena_rutNoExiste_retornaError() throws Exception {
        when(profesorRepo.findByRut("88888888-8")).thenReturn(Optional.empty());
        when(estudianteRepo.findByRut("88888888-8")).thenReturn(Optional.empty());
        when(apoderadoRepo.findByRut("88888888-8")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/resetear-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"88888888-8\",\"newPassword\":\"nueva123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.error").value("RUT no encontrado en el sistema"));
    }

    @Test
    @DisplayName("POST /api/auth/resetear-contrasena - password muy corta retorna 400")
    void resetearContrasena_passwordCorta_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/resetear-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"99999999-9\",\"newPassword\":\"ab\"}"))
                .andExpect(status().isBadRequest());
    }
}

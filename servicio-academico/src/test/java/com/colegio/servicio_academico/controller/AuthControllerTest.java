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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

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

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private String hashProfesor;
    private String hashEstudiante;
    private String hashApoderado;

    @BeforeEach
    void setUpPasswords() {
        // Generamos hashes reales de BCrypt para poder probar matches()
        hashProfesor = encoder.encode("password123");
        hashEstudiante = encoder.encode("password123");
        hashApoderado = encoder.encode("password123");
    }

    // ──────────────────────────────────────────────────────
    // /api/auth/validar - Validar credenciales
    // ──────────────────────────────────────────────────────

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
    @DisplayName("POST /api/auth/validar - RUT encontrado como PROFESOR con password correcta")
    void validar_profesorEncontrado_passwordCorrecta() throws Exception {
        Profesor p = Profesor.builder()
                .id(1L).nombre("Maria").apellido("Lopez").rut("11111111-1")
                .contrasena(hashProfesor).build();
        when(profesorRepo.findByRut("11111111-1")).thenReturn(Optional.of(p));

        mockMvc.perform(post("/api/auth/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"11111111-1\",\"password\":\"password123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true))
                .andExpect(jsonPath("$.role").value("PROFESOR"))
                .andExpect(jsonPath("$.nombre").value("Maria Lopez"));
    }

    @Test
    @DisplayName("POST /api/auth/validar - RUT encontrado como PROFESOR con password incorrecta")
    void validar_profesorEncontrado_passwordIncorrecta() throws Exception {
        Profesor p = Profesor.builder()
                .id(1L).nombre("Maria").apellido("Lopez").rut("11111111-1")
                .contrasena(hashProfesor).build();
        when(profesorRepo.findByRut("11111111-1")).thenReturn(Optional.of(p));

        mockMvc.perform(post("/api/auth/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"11111111-1\",\"password\":\"incorrecta\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false))
                .andExpect(jsonPath("$.error").value("Contraseña incorrecta"));
    }

    @Test
    @DisplayName("POST /api/auth/validar - RUT encontrado como ESTUDIANTE con password correcta")
    void validar_estudianteEncontrado_passwordCorrecta() throws Exception {
        when(profesorRepo.findByRut("33333333-3")).thenReturn(Optional.empty());
        Estudiante e = Estudiante.builder()
                .id(1L).nombre("Juan").apellido("Perez").rut("33333333-3")
                .password(hashEstudiante).build();
        when(estudianteRepo.findByRut("33333333-3")).thenReturn(Optional.of(e));

        mockMvc.perform(post("/api/auth/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"33333333-3\",\"password\":\"password123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true))
                .andExpect(jsonPath("$.role").value("ALUMNO"))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"));
    }

    @Test
    @DisplayName("POST /api/auth/validar - RUT encontrado como APODERADO con password correcta")
    void validar_apoderadoEncontrado_passwordCorrecta() throws Exception {
        when(profesorRepo.findByRut("55555555-5")).thenReturn(Optional.empty());
        when(estudianteRepo.findByRut("55555555-5")).thenReturn(Optional.empty());
        Apoderado a = Apoderado.builder()
                .id(1L).nombre("Carlos").apellido("Gonzalez").rut("55555555-5")
                .contrasena(hashApoderado).build();
        when(apoderadoRepo.findByRut("55555555-5")).thenReturn(Optional.of(a));

        mockMvc.perform(post("/api/auth/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"55555555-5\",\"password\":\"password123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true))
                .andExpect(jsonPath("$.role").value("APODERADO"))
                .andExpect(jsonPath("$.nombre").value("Carlos Gonzalez"));
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
    @DisplayName("POST /api/auth/validar - solo RUT sin password retorna 400")
    void validar_soloRut_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"11111111-1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/validar - solo password sin RUT retorna 400")
    void validar_soloPassword_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"1234\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/validar - RUT encontrado como ESTUDIANTE con password incorrecta")
    void validar_estudianteEncontrado_passwordIncorrecta() throws Exception {
        when(profesorRepo.findByRut("33333333-3")).thenReturn(Optional.empty());
        Estudiante e = Estudiante.builder()
                .id(1L).nombre("Juan").apellido("Perez").rut("33333333-3")
                .password(hashEstudiante).build();
        when(estudianteRepo.findByRut("33333333-3")).thenReturn(Optional.of(e));

        mockMvc.perform(post("/api/auth/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"33333333-3\",\"password\":\"wrongpass\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false))
                .andExpect(jsonPath("$.error").value("Contraseña incorrecta"));
    }

    @Test
    @DisplayName("POST /api/auth/validar - RUT encontrado como APODERADO con password incorrecta")
    void validar_apoderadoEncontrado_passwordIncorrecta() throws Exception {
        when(profesorRepo.findByRut("55555555-5")).thenReturn(Optional.empty());
        when(estudianteRepo.findByRut("55555555-5")).thenReturn(Optional.empty());
        Apoderado a = Apoderado.builder()
                .id(1L).nombre("Carlos").apellido("Gonzalez").rut("55555555-5")
                .contrasena(hashApoderado).build();
        when(apoderadoRepo.findByRut("55555555-5")).thenReturn(Optional.of(a));

        mockMvc.perform(post("/api/auth/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"55555555-5\",\"password\":\"wrongpass\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false))
                .andExpect(jsonPath("$.error").value("Contraseña incorrecta"));
    }

    // ──────────────────────────────────────────────────────
    // /api/auth/cambiar-contrasena - Cambiar contraseña
    // ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/cambiar-contrasena - PROFESOR: cambia contraseña correctamente")
    void cambiarContrasena_profesor_exito() throws Exception {
        Profesor p = Profesor.builder()
                .id(1L).nombre("Maria").apellido("Lopez").rut("11111111-1")
                .contrasena(hashProfesor).build();
        when(profesorRepo.findByRut("11111111-1")).thenReturn(Optional.of(p));
        when(profesorRepo.save(any(Profesor.class))).thenReturn(p);

        mockMvc.perform(post("/api/auth/cambiar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"11111111-1\",\"currentPassword\":\"password123\",\"newPassword\":\"nuevaPass1\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Contraseña actualizada correctamente"));

        verify(profesorRepo).save(p);
    }

    @Test
    @DisplayName("POST /api/auth/cambiar-contrasena - ESTUDIANTE: cambia contraseña correctamente")
    void cambiarContrasena_estudiante_exito() throws Exception {
        when(profesorRepo.findByRut("33333333-3")).thenReturn(Optional.empty());
        Estudiante e = Estudiante.builder()
                .id(1L).nombre("Juan").apellido("Perez").rut("33333333-3")
                .password(hashEstudiante).build();
        when(estudianteRepo.findByRut("33333333-3")).thenReturn(Optional.of(e));
        when(estudianteRepo.save(any(Estudiante.class))).thenReturn(e);

        mockMvc.perform(post("/api/auth/cambiar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"33333333-3\",\"currentPassword\":\"password123\",\"newPassword\":\"nuevaPass1\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Contraseña actualizada correctamente"));

        verify(estudianteRepo).save(e);
    }

    @Test
    @DisplayName("POST /api/auth/cambiar-contrasena - APODERADO: cambia contraseña correctamente")
    void cambiarContrasena_apoderado_exito() throws Exception {
        when(profesorRepo.findByRut("55555555-5")).thenReturn(Optional.empty());
        when(estudianteRepo.findByRut("55555555-5")).thenReturn(Optional.empty());
        Apoderado a = Apoderado.builder()
                .id(1L).nombre("Carlos").apellido("Gonzalez").rut("55555555-5")
                .contrasena(hashApoderado).build();
        when(apoderadoRepo.findByRut("55555555-5")).thenReturn(Optional.of(a));
        when(apoderadoRepo.save(any(Apoderado.class))).thenReturn(a);

        mockMvc.perform(post("/api/auth/cambiar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"55555555-5\",\"currentPassword\":\"password123\",\"newPassword\":\"nuevaPass1\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Contraseña actualizada correctamente"));

        verify(apoderadoRepo).save(a);
    }

    @Test
    @DisplayName("POST /api/auth/cambiar-contrasena - password actual incorrecta retorna error")
    void cambiarContrasena_passwordActualIncorrecta() throws Exception {
        Profesor p = Profesor.builder()
                .id(1L).nombre("Maria").apellido("Lopez").rut("11111111-1")
                .contrasena(hashProfesor).build();
        when(profesorRepo.findByRut("11111111-1")).thenReturn(Optional.of(p));

        mockMvc.perform(post("/api/auth/cambiar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"11111111-1\",\"currentPassword\":\"wrongpass\",\"newPassword\":\"nuevaPass1\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.error").value("La contraseña actual no es correcta"));
    }

    @Test
    @DisplayName("POST /api/auth/cambiar-contrasena - RUT no encontrado retorna error")
    void cambiarContrasena_rutNoExiste_retornaError() throws Exception {
        when(profesorRepo.findByRut("99999999-9")).thenReturn(Optional.empty());
        when(estudianteRepo.findByRut("99999999-9")).thenReturn(Optional.empty());
        when(apoderadoRepo.findByRut("99999999-9")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/cambiar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"99999999-9\",\"currentPassword\":\"pass\",\"newPassword\":\"nuevaPass1\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.error").value("RUT no encontrado en el sistema"));
    }

    @Test
    @DisplayName("POST /api/auth/cambiar-contrasena - password nueva muy corta retorna 400")
    void cambiarContrasena_passwordCorta_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/cambiar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"11111111-1\",\"currentPassword\":\"a\",\"newPassword\":\"ab\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/cambiar-contrasena - campos nulos retorna 400")
    void cambiarContrasena_camposNulos_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/cambiar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ──────────────────────────────────────────────────────
    // /api/auth/resetear-contrasena - Resetear contraseña (UTP)
    // ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/resetear-contrasena - PROFESOR: resetea correctamente")
    void resetearContrasena_profesor_exito() throws Exception {
        Profesor p = Profesor.builder()
                .id(1L).nombre("Maria").apellido("Lopez").rut("11111111-1")
                .contrasena(hashProfesor).build();
        when(profesorRepo.findByRut("11111111-1")).thenReturn(Optional.of(p));
        when(profesorRepo.save(any(Profesor.class))).thenReturn(p);

        mockMvc.perform(post("/api/auth/resetear-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"11111111-1\",\"newPassword\":\"nueva123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Contraseña reseteada correctamente"));

        verify(profesorRepo).save(p);
    }

    @Test
    @DisplayName("POST /api/auth/resetear-contrasena - ESTUDIANTE: resetea correctamente")
    void resetearContrasena_estudiante_exito() throws Exception {
        when(profesorRepo.findByRut("33333333-3")).thenReturn(Optional.empty());
        Estudiante e = Estudiante.builder()
                .id(1L).nombre("Juan").apellido("Perez").rut("33333333-3")
                .password(hashEstudiante).build();
        when(estudianteRepo.findByRut("33333333-3")).thenReturn(Optional.of(e));
        when(estudianteRepo.save(any(Estudiante.class))).thenReturn(e);

        mockMvc.perform(post("/api/auth/resetear-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"33333333-3\",\"newPassword\":\"nueva123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Contraseña reseteada correctamente"));

        verify(estudianteRepo).save(e);
    }

    @Test
    @DisplayName("POST /api/auth/resetear-contrasena - APODERADO: resetea correctamente")
    void resetearContrasena_apoderado_exito() throws Exception {
        when(profesorRepo.findByRut("55555555-5")).thenReturn(Optional.empty());
        when(estudianteRepo.findByRut("55555555-5")).thenReturn(Optional.empty());
        Apoderado a = Apoderado.builder()
                .id(1L).nombre("Carlos").apellido("Gonzalez").rut("55555555-5")
                .contrasena(hashApoderado).build();
        when(apoderadoRepo.findByRut("55555555-5")).thenReturn(Optional.of(a));
        when(apoderadoRepo.save(any(Apoderado.class))).thenReturn(a);

        mockMvc.perform(post("/api/auth/resetear-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"55555555-5\",\"newPassword\":\"nueva123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Contraseña reseteada correctamente"));

        verify(apoderadoRepo).save(a);
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

    @Test
    @DisplayName("POST /api/auth/resetear-contrasena - campos nulos retorna 400")
    void resetearContrasena_camposNulos_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/resetear-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/resetear-contrasena - solo RUT sin newPassword retorna 400")
    void resetearContrasena_soloRut_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/resetear-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"11111111-1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/validar - RUT null retorna 400")
    void validar_sinDatos_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":null,\"password\":\"abc\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

package com.colegio.api_gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void resetPasswordRequiresAuthentication() throws Exception {
        mvc.perform(post("/auth/resetear-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"11111111-1\",\"newPassword\":\"nuevaClave\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void resetPasswordIsRestrictedToUtp() throws Exception {
        String token = jwtUtil.generateToken("11111111-1", "ALUMNO");

        mvc.perform(post("/auth/resetear-contrasena")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"22222222-2\",\"newPassword\":\"nuevaClave\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void utpCanResetPassword() throws Exception {
        when(authService.resetearContrasena("22222222-2", "nuevaClave"))
                .thenReturn(Map.of("exito", true));
        String token = jwtUtil.generateToken("11111111-1", "UTP");

        mvc.perform(post("/auth/resetear-contrasena")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"22222222-2\",\"newPassword\":\"nuevaClave\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void studentCannotListAllStudents() throws Exception {
        String token = jwtUtil.generateToken("11111111-1", "ALUMNO");

        mvc.perform(get("/academico/api/estudiantes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void guardianCannotListAllStudents() throws Exception {
        String token = jwtUtil.generateToken("11111111-1", "APODERADO");

        mvc.perform(get("/academico/api/estudiantes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentCanReadOnlyOwnProfileEndpointAndResponseOmitsCredentials() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of(
                "id", 3, "nombre", "Alumno", "apellido", "Prueba", "rut", "11111111-1",
                "curso", 8, "password", "hash-que-no-debe-salir"));
        String token = jwtUtil.generateToken("11111111-1", "ALUMNO");

        mvc.perform(get("/me/estudiante").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.rut").value("11111111-1"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @Test
    void guardianCanReadAssociatedPupilEndpointButGetsOnlyMinimumFields() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of(
                "rut", "11111111-1",
                "estudiante", Map.of("id", 3, "nombre", "Alumno", "apellido", "Prueba",
                        "rut", "33333333-3", "curso", 8, "password", "hash-no-debe-salir")));
        String token = jwtUtil.generateToken("11111111-1", "APODERADO");

        mvc.perform(get("/me/pupilo").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.rut").value("33333333-3"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.contrasena").doesNotExist());
    }
}

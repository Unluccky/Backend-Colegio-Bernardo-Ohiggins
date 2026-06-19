package com.colegio.api_gateway.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - Pruebas Unitarias")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    // ── POST /auth/login ─────────────────────────────────

    @Test
    @DisplayName("login - credenciales válidas retorna 200 con token")
    void login_validas_retorna200() {
        Map<String, String> request = Map.of("rut", "77777777-7", "password", "utp123");
        Map<String, String> respuesta = Map.of("token", "jwt-token", "rut", "77777777-7", "role", "UTP");
        when(authService.autenticar("77777777-7", "utp123")).thenReturn(Optional.of(respuesta));

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(respuesta);
    }

    @Test
    @DisplayName("login - credenciales inválidas retorna 401")
    void login_invalidas_retorna401() {
        Map<String, String> request = Map.of("rut", "77777777-7", "password", "wrong");
        when(authService.autenticar("77777777-7", "wrong")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        assertThat(((Map<String, String>) response.getBody()).get("error")).isEqualTo("RUT o contraseña incorrectos");
    }

    @Test
    @DisplayName("login - credenciales vacías delega al service correctamente")
    void login_rutVacio_retorna401() {
        Map<String, String> request = Map.of("rut", "", "password", "");
        when(authService.autenticar("", "")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ── POST /auth/cambiar-contrasena ────────────────────

    @Test
    @DisplayName("cambiarContrasena - datos válidos retorna 200 con éxito")
    void cambiarContrasena_valido_retorna200() {
        Map<String, String> request = Map.of(
                "rut", "77777777-7",
                "currentPassword", "utp123",
                "newPassword", "nuevaClave"
        );
        when(authService.cambiarContrasena("77777777-7", "utp123", "nuevaClave"))
                .thenReturn(Map.of("exito", true, "mensaje", "Contraseña actualizada correctamente"));

        ResponseEntity<?> response = authController.cambiarContrasena(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<String, Object>) response.getBody()).get("exito")).isEqualTo(true);
    }

    @Test
    @DisplayName("cambiarContrasena - sin datos requeridos retorna 400")
    void cambiarContrasena_sinDatos_retorna400() {
        Map<String, String> request = Map.of("rut", "77777777-7");

        ResponseEntity<?> response = authController.cambiarContrasena(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("cambiarContrasena - newPassword muy corta retorna 400")
    void cambiarContrasena_passwordCorta_retorna400() {
        Map<String, String> request = Map.of(
                "rut", "77777777-7",
                "currentPassword", "utp123",
                "newPassword", "ab"
        );

        ResponseEntity<?> response = authController.cambiarContrasena(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<String, Object>) response.getBody()).get("error"))
                .toString().contains("al menos 4 caracteres");
    }

    // ── POST /auth/resetear-contrasena ───────────────────

    @Test
    @DisplayName("resetearContrasena - datos válidos retorna 200")
    void resetearContrasena_valido_retorna200() {
        Map<String, String> request = Map.of("rut", "77777777-7", "newPassword", "nuevaClave");
        when(authService.resetearContrasena("77777777-7", "nuevaClave"))
                .thenReturn(Map.of("exito", true, "mensaje", "Contraseña reseteada correctamente"));

        ResponseEntity<?> response = authController.resetearContrasena(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<String, Object>) response.getBody()).get("exito")).isEqualTo(true);
    }

    @Test
    @DisplayName("resetearContrasena - sin RUT retorna 400")
    void resetearContrasena_sinRut_retorna400() {
        Map<String, String> request = Map.of("newPassword", "nuevaClave");

        ResponseEntity<?> response = authController.resetearContrasena(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("resetearContrasena - password muy corta retorna 400")
    void resetearContrasena_passwordCorta_retorna400() {
        Map<String, String> request = Map.of("rut", "77777777-7", "newPassword", "ab");

        ResponseEntity<?> response = authController.resetearContrasena(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

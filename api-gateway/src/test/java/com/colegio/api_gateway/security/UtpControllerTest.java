package com.colegio.api_gateway.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UtpController - Pruebas Unitarias")
class UtpControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private UtpController utpController;

    // ── GET /admin/usuarios-utp ──────────────────────────

    @Test
    @DisplayName("listar - retorna lista de usuarios UTP")
    void listar_deberiaRetornarUsuariosUTP() {
        List<Map<String, String>> usuarios = List.of(
                Map.of("rut", "77777777-7", "role", "UTP"),
                Map.of("rut", "88888888-8", "role", "UTP")
        );
        when(authService.listarUsuariosUtp()).thenReturn(usuarios);

        ResponseEntity<List<Map<String, String>>> response = utpController.listar();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        verify(authService, times(1)).listarUsuariosUtp();
    }

    @Test
    @DisplayName("listar - retorna lista vacía cuando no hay UTPs")
    void listar_sinUtps_retornaVacio() {
        when(authService.listarUsuariosUtp()).thenReturn(List.of());

        ResponseEntity<List<Map<String, String>>> response = utpController.listar();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    // ── POST /admin/usuarios-utp ─────────────────────────

    @Test
    @DisplayName("crear - datos válidos retorna 200 con usuario creado")
    void crear_valido_retornaUsuario() {
        Map<String, String> request = Map.of("rut", "12121212-1", "password", "pass123");
        Map<String, String> usuarioCreado = Map.of("rut", "12121212-1", "role", "UTP");
        when(authService.existeUsuarioUtp("12121212-1")).thenReturn(false);
        when(authService.crearUsuarioUtp("12121212-1", "pass123")).thenReturn(usuarioCreado);

        ResponseEntity<?> response = utpController.crear(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(usuarioCreado);
    }

    @Test
    @DisplayName("crear - usuario ya existente retorna 400")
    void crear_yaExistente_retorna400() {
        Map<String, String> request = Map.of("rut", "77777777-7", "password", "pass123");
        when(authService.existeUsuarioUtp("77777777-7")).thenReturn(true);

        ResponseEntity<?> response = utpController.crear(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<String, String>) response.getBody()).get("error"))
                .isEqualTo("Ya existe un usuario UTP con ese RUT");
    }

    @Test
    @DisplayName("crear - RUT vacío retorna 400")
    void crear_rutVacio_retorna400() {
        Map<String, String> request = Map.of("rut", "", "password", "pass123");

        ResponseEntity<?> response = utpController.crear(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("crear - password vacío retorna 400")
    void crear_passwordVacio_retorna400() {
        Map<String, String> request = Map.of("rut", "12121212-1", "password", "");

        ResponseEntity<?> response = utpController.crear(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ── DELETE /admin/usuarios-utp/{rut} ─────────────────

    @Test
    @DisplayName("eliminar - usuario existente retorna 204")
    void eliminar_existente_retorna204() {
        when(authService.eliminarUsuarioUtp("77777777-7")).thenReturn(true);

        ResponseEntity<?> response = utpController.eliminar("77777777-7");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("eliminar - usuario inexistente retorna 404")
    void eliminar_noExistente_retorna404() {
        when(authService.eliminarUsuarioUtp("00-0")).thenReturn(false);

        ResponseEntity<?> response = utpController.eliminar("00-0");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

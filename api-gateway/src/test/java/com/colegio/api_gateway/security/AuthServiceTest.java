package com.colegio.api_gateway.security;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Pruebas Unitarias")
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Mock
    private CircuitBreaker circuitBreaker;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "academicoServiceUrl", "http://localhost:8081");
    }

    // ── autenticar ─────────────────────────────────────────

    @Test
    @DisplayName("autenticar - UTP válido retorna token y role")
    void autenticar_utpValido_retornaTokenYRole() {
        when(jwtUtil.generateToken("77777777-7", "UTP")).thenReturn("token-utp");

        Optional<Map<String, String>> resultado = authService.autenticar("77777777-7", "utp123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().get("rut")).isEqualTo("77777777-7");
        assertThat(resultado.get().get("role")).isEqualTo("UTP");
        assertThat(resultado.get().get("token")).isEqualTo("token-utp");
    }

    @Test
    @DisplayName("autenticar - contraseña incorrecta retorna empty")
    void autenticar_passwordIncorrecta_retornaEmpty() {
        Optional<Map<String, String>> resultado = authService.autenticar("77777777-7", "wrong-password");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("autenticar - RUT inexistente retorna empty")
    void autenticar_rutInexistente_retornaEmpty() {
        Optional<Map<String, String>> resultado = authService.autenticar("00-0", "password");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("autenticar - profesor local válido retorna token y role PROFESOR")
    void autenticar_profesorValido_retornaToken() {
        when(jwtUtil.generateToken("11111111-1", "PROFESOR")).thenReturn("token-prof");

        Optional<Map<String, String>> resultado = authService.autenticar("11111111-1", "profesor123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().get("role")).isEqualTo("PROFESOR");
    }

    @Test
    @DisplayName("autenticar - alumno local válido retorna token y role ALUMNO")
    void autenticar_alumnoValido_retornaToken() {
        when(jwtUtil.generateToken("33333333-3", "ALUMNO")).thenReturn("token-alumno");

        Optional<Map<String, String>> resultado = authService.autenticar("33333333-3", "alumno123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().get("role")).isEqualTo("ALUMNO");
    }

    @Test
    @DisplayName("autenticar - apoderado local válido retorna token y role APODERADO")
    void autenticar_apoderadoValido_retornaToken() {
        when(jwtUtil.generateToken("55555555-5", "APODERADO")).thenReturn("token-apod");

        Optional<Map<String, String>> resultado = authService.autenticar("55555555-5", "apoderado123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().get("role")).isEqualTo("APODERADO");
    }

    // ── Gestión UTP ────────────────────────────────────────

    @Test
    @DisplayName("listarUsuariosUtp - retorna solo usuarios con role UTP")
    void listarUsuariosUtp_deberiaFiltrarSoloUTP() {
        List<Map<String, String>> utpUsers = authService.listarUsuariosUtp();

        assertThat(utpUsers).isNotEmpty();
        assertThat(utpUsers).allSatisfy(user ->
                assertThat(user.get("role")).isEqualTo("UTP")
        );
    }

    @Test
    @DisplayName("existeUsuarioUtp - retorna true para UTP existente")
    void existeUsuarioUtp_existente_retornaTrue() {
        assertThat(authService.existeUsuarioUtp("77777777-7")).isTrue();
    }

    @Test
    @DisplayName("existeUsuarioUtp - retorna false para UTP inexistente")
    void existeUsuarioUtp_noExistente_retornaFalse() {
        assertThat(authService.existeUsuarioUtp("00-0")).isFalse();
    }

    @Test
    @DisplayName("crearUsuarioUtp - crea y retorna nuevo usuario UTP")
    void crearUsuarioUtp_deberiaCrearYRetornar() {
        Map<String, String> creado = authService.crearUsuarioUtp("12121212-1", "newPassword123");

        assertThat(creado.get("rut")).isEqualTo("12121212-1");
        assertThat(creado.get("role")).isEqualTo("UTP");
        assertThat(authService.existeUsuarioUtp("12121212-1")).isTrue();
    }

    @Test
    @DisplayName("eliminarUsuarioUtp - elimina usuario UTP existente")
    void eliminarUsuarioUtp_existente_eliminaYRetornaTrue() {
        authService.crearUsuarioUtp("34343434-4", "pass123");

        boolean eliminado = authService.eliminarUsuarioUtp("34343434-4");

        assertThat(eliminado).isTrue();
        assertThat(authService.existeUsuarioUtp("34343434-4")).isFalse();
    }

    @Test
    @DisplayName("eliminarUsuarioUtp - retorna false si el UTP no existe")
    void eliminarUsuarioUtp_noExistente_retornaFalse() {
        assertThat(authService.eliminarUsuarioUtp("00-0")).isFalse();
    }

    // ── Cambio de contraseña ──────────────────────────────

    @Test
    @DisplayName("cambiarContrasena - UTP cambia su contraseña correctamente usando usuario dedicado")
    void cambiarContrasena_utpValido_actualizaContrasena() {
        // Crear un usuario dedicado para evitar interferencia con otros tests
        String rutTest = "99999999-9"; // UTP conocido del static initializer
        when(jwtUtil.generateToken(rutTest, "UTP")).thenReturn("token-test");

        // Primero verificar que el usuario existe y funciona
        assertThat(authService.autenticar(rutTest, "utp123")).isPresent();

        Map<String, Object> resultado = authService.cambiarContrasena(rutTest, "utp123", "newPassword456");

        assertThat(resultado.get("exito")).isEqualTo(true);
        assertThat(resultado.get("mensaje")).isEqualTo("Contraseña actualizada correctamente");

        // Verificar que la nueva contraseña funciona
        when(jwtUtil.generateToken(rutTest, "UTP")).thenReturn("token-new");
        Optional<Map<String, String>> login = authService.autenticar(rutTest, "newPassword456");
        assertThat(login).isPresent();

        // Y la anterior ya no funciona
        assertThat(authService.autenticar(rutTest, "utp123")).isEmpty();

        // Restaurar contraseña original para no afectar otros tests
        authService.resetearContrasena(rutTest, "utp123");
    }

    @Test
    @DisplayName("cambiarContrasena - contraseña actual incorrecta retorna error")
    void cambiarContrasena_passwordActualIncorrecta_retornaError() {
        Map<String, Object> resultado = authService.cambiarContrasena("77777777-7", "wrong-current", "newPass");

        assertThat(resultado.get("exito")).isEqualTo(false);
        assertThat(resultado.get("error")).isEqualTo("La contraseña actual no es correcta");
    }

    // ── Reset de contraseña ───────────────────────────────

    @Test
    @DisplayName("resetearContrasena - UTP resetea contraseña correctamente usando usuario dedicado")
    void resetearContrasena_utpValido_reseteaContrasena() {
        String rutTest = "88888888-8"; // UTP conocido, dedicado para este test
        when(jwtUtil.generateToken(rutTest, "UTP")).thenReturn("token-reset");

        assertThat(authService.autenticar(rutTest, "utp123")).isPresent();

        Map<String, Object> resultado = authService.resetearContrasena(rutTest, "resetPassword789");

        assertThat(resultado.get("exito")).isEqualTo(true);

        // Verificar que la nueva contraseña funciona
        when(jwtUtil.generateToken(rutTest, "UTP")).thenReturn("token-new");
        Optional<Map<String, String>> login = authService.autenticar(rutTest, "resetPassword789");
        assertThat(login).isPresent();

        // Restaurar
        authService.resetearContrasena(rutTest, "utp123");
    }

    @Test
    @DisplayName("resetearContrasena - resetea sin importar la contraseña anterior (usuario dedicado)")
    void resetearContrasena_reseteaSinValidarAnterior() {
        String rutTest = "88888888-8";

        // Cambiar la contraseña primero
        authService.resetearContrasena(rutTest, "nuevaClave");

        // Ya no funciona con la original
        Optional<Map<String, String>> loginViejo = authService.autenticar(rutTest, "utp123");
        assertThat(loginViejo).isEmpty();

        // Pero sí con la nueva
        Optional<Map<String, String>> loginNuevo = authService.autenticar(rutTest, "nuevaClave");
        assertThat(loginNuevo).isPresent();

        // Restaurar
        authService.resetearContrasena(rutTest, "utp123");
    }
}

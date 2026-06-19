package com.colegio.api_gateway.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtil - Pruebas Unitarias")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET = "colegio-bernardo-ohiggins-secret-key-2025-fullstack-iii";
    private static final String RUT = "12345678-9";
    private static final String ROLE = "UTP";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
    }

    @Test
    @DisplayName("generateToken - crea un token JWT válido con los claims correctos")
    void generateToken_deberiaCrearTokenValido() {
        String token = jwtUtil.generateToken(RUT, ROLE);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);

        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractRut(token)).isEqualTo(RUT);
        assertThat(jwtUtil.extractRole(token)).isEqualTo(ROLE);
    }

    @Test
    @DisplayName("generateToken - tokens diferentes para distintos usuarios")
    void generateToken_tokensDiferentes_paraDistintosUsuarios() {
        String token1 = jwtUtil.generateToken("11111111-1", "PROFESOR");
        String token2 = jwtUtil.generateToken("22222222-2", "ALUMNO");

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("extractRut - extrae el RUT correcto del token")
    void extractRut_deberiaExtraerRut() {
        String token = jwtUtil.generateToken(RUT, ROLE);

        String rutExtraido = jwtUtil.extractRut(token);

        assertThat(rutExtraido).isEqualTo(RUT);
    }

    @Test
    @DisplayName("extractRole - extrae el rol correcto del token")
    void extractRole_deberiaExtraerRole() {
        String token = jwtUtil.generateToken(RUT, "PROFESOR");

        String role = jwtUtil.extractRole(token);

        assertThat(role).isEqualTo("PROFESOR");
    }

    @Test
    @DisplayName("extractClaims - retorna todos los claims del token")
    void extractClaims_deberiaRetornarClaims() {
        String token = jwtUtil.generateToken(RUT, ROLE);

        Claims claims = jwtUtil.extractClaims(token);

        assertThat(claims.getSubject()).isEqualTo(RUT);
        assertThat(claims.get("role", String.class)).isEqualTo(ROLE);
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
    }

    @Test
    @DisplayName("isTokenValid - retorna false para token vacío")
    void isTokenValid_tokenVacio_retornaFalse() {
        assertThat(jwtUtil.isTokenValid("")).isFalse();
    }

    @Test
    @DisplayName("isTokenValid - retorna false para token mal formado")
    void isTokenValid_tokenMalFormado_retornaFalse() {
        assertThat(jwtUtil.isTokenValid("token-invalido")).isFalse();
    }

    @Test
    @DisplayName("isTokenValid - retorna false para token null")
    void isTokenValid_tokenNull_retornaFalse() {
        assertThat(jwtUtil.isTokenValid(null)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid - retorna false para token con firma alterada")
    void isTokenValid_firmaAlterada_retornaFalse() {
        String token = jwtUtil.generateToken(RUT, ROLE);
        String tokenAlterado = token.substring(0, token.length() - 5) + "XXXXX";

        assertThat(jwtUtil.isTokenValid(tokenAlterado)).isFalse();
    }
}

package com.colegio.servicio_asistencia.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("GlobalExceptionHandler - Pruebas Unitarias")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleRuntimeException - retorna 404 con mensaje de error")
    void handleRuntimeException_deberiaRetornar404() {
        RuntimeException ex = new RuntimeException("Recurso no encontrado: 99");

        ResponseEntity<Map<String, String>> response = handler.handleRuntimeException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("error", "Recurso no encontrado: 99");
    }

    @Test
    @DisplayName("handleRuntimeException - maneja mensaje genérico")
    void handleRuntimeException_mensajeGenerico() {
        RuntimeException ex = new RuntimeException("Error interno del servidor");

        ResponseEntity<Map<String, String>> response = handler.handleRuntimeException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Error interno del servidor");
    }
}

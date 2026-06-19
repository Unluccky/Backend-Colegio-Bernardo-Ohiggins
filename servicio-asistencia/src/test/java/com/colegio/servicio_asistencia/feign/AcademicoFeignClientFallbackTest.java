package com.colegio.servicio_asistencia.feign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AcademicoFeignClientFallback - Pruebas Unitarias")
class AcademicoFeignClientFallbackTest {

    private AcademicoFeignClientFallback fallback;

    @BeforeEach
    void setUp() {
        fallback = new AcademicoFeignClientFallback();
    }

    @Test
    @DisplayName("buscarEstudiantePorId - retorna null cuando el servicio no está disponible")
    void buscarEstudiantePorId_deberiaRetornarNull() {
        Object resultado = fallback.buscarEstudiantePorId(1L);

        assertThat(resultado).isNull();
    }

    @Test
    @DisplayName("buscarApoderadosPorEstudiante - retorna lista vacía cuando el servicio no está disponible")
    void buscarApoderadosPorEstudiante_deberiaRetornarListaVacia() {
        List<Map<String, Object>> resultado = fallback.buscarApoderadosPorEstudiante(1L);

        assertThat(resultado).isEmpty();
    }
}

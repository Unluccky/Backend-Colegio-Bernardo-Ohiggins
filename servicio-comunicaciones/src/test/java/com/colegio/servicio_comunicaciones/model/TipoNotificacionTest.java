package com.colegio.servicio_comunicaciones.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("TipoNotificacion - Pruebas Unitarias")
class TipoNotificacionTest {

    @Test
    @DisplayName("valores - debe contener los 4 tipos esperados")
    void valores_deberiaContenerTiposEsperados() {
        TipoNotificacion[] valores = TipoNotificacion.values();

        assertThat(valores).hasSize(4);
        assertThat(valores).containsExactly(
            TipoNotificacion.ANOTACION,
            TipoNotificacion.ASISTENCIA,
            TipoNotificacion.NOTA,
            TipoNotificacion.MENSAJE
        );
    }

    @Test
    @DisplayName("valueOf - debe retornar el tipo correcto para cada nombre")
    void valueOf_deberiaRetornarTipoCorrecto() {
        assertThat(TipoNotificacion.valueOf("ANOTACION")).isEqualTo(TipoNotificacion.ANOTACION);
        assertThat(TipoNotificacion.valueOf("MENSAJE")).isEqualTo(TipoNotificacion.MENSAJE);
    }
}

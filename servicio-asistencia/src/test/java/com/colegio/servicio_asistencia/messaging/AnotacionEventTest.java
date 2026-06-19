package com.colegio.servicio_asistencia.messaging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AnotacionEvent - Pruebas Unitarias")
class AnotacionEventTest {

    @Test
    @DisplayName("builder - crea evento con todos los campos")
    void builder_deberiaCrearEventoCompleto() {
        LocalDate fecha = LocalDate.now();

        AnotacionEvent evento = AnotacionEvent.builder()
                .estudianteId(10L)
                .profesorId(3L)
                .descripcion("Anotación de prueba")
                .tipo("NEGATIVA")
                .fecha(fecha)
                .apoderadoId(1L)
                .build();

        assertThat(evento.getEstudianteId()).isEqualTo(10L);
        assertThat(evento.getProfesorId()).isEqualTo(3L);
        assertThat(evento.getDescripcion()).isEqualTo("Anotación de prueba");
        assertThat(evento.getTipo()).isEqualTo("NEGATIVA");
        assertThat(evento.getFecha()).isEqualTo(fecha);
        assertThat(evento.getApoderadoId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("equals y hashCode - eventos con mismos campos son iguales")
    void equals_hashCode_deberianSerConsistentes() {
        LocalDate fecha = LocalDate.now();

        AnotacionEvent evento1 = AnotacionEvent.builder()
                .estudianteId(10L).profesorId(3L).descripcion("Test")
                .tipo("NEGATIVA").fecha(fecha).apoderadoId(1L).build();

        AnotacionEvent evento2 = AnotacionEvent.builder()
                .estudianteId(10L).profesorId(3L).descripcion("Test")
                .tipo("NEGATIVA").fecha(fecha).apoderadoId(1L).build();

        assertThat(evento1).isEqualTo(evento2);
        assertThat(evento1.hashCode()).isEqualTo(evento2.hashCode());
    }

    @Test
    @DisplayName("noArgs - crea evento vacío y luego se asignan campos")
    void noArgsConstructor_deberiaCrearEventoVacio() {
        AnotacionEvent evento = new AnotacionEvent();
        evento.setEstudianteId(10L);
        evento.setDescripcion("Test");

        assertThat(evento.getEstudianteId()).isEqualTo(10L);
        assertThat(evento.getDescripcion()).isEqualTo("Test");
    }

    @Test
    @DisplayName("toString - no lanza excepción")
    void toString_deberiaFuncionar() {
        AnotacionEvent evento = AnotacionEvent.builder()
                .estudianteId(10L).descripcion("Test").build();

        assertThat(evento.toString()).contains("10").contains("Test");
    }
}

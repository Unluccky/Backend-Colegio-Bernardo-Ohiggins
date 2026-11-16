package com.colegio.servicio_academico.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CredentialSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void studentHashIsNotSerialized() throws Exception {
        Estudiante estudiante = Estudiante.builder().id(1L).nombre("Ana")
                .password("$2a$12$hash").build();

        String json = mapper.writeValueAsString(estudiante);

        assertThat(json).doesNotContain("password", "$2a$12$hash");
    }

    @Test
    void guardianAndNestedStudentHashesAreNotSerialized() throws Exception {
        Estudiante estudiante = Estudiante.builder().id(1L).nombre("Ana")
                .password("student-hash").build();
        Apoderado apoderado = Apoderado.builder().id(2L).nombre("Tutor")
                .contrasena("guardian-hash").estudiante(estudiante).build();

        String json = mapper.writeValueAsString(apoderado);

        assertThat(json).doesNotContain("contrasena", "guardian-hash", "password", "student-hash");
    }
}

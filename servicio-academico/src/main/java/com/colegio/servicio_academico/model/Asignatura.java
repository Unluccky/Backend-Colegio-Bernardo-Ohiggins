package com.colegio.servicio_academico.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "asignaturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Integer nivelCurso;

    @ManyToOne
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;
}
package com.colegio.servicio_academico.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "horarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @ManyToOne
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    @NotNull
    @Column(nullable = false)
    private Integer curso;

    @NotNull
    @Column(nullable = false)
    private Integer dia; // 1=Lunes, 2=Martes, 3=Miércoles, 4=Jueves, 5=Viernes

    @NotBlank
    @Column(nullable = false)
    private String horaInicio;

    @NotBlank
    @Column(nullable = false)
    private String horaFin;

    private String sala;
}

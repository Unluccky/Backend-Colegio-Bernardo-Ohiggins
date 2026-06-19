package com.colegio.servicio_academico.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "estudiantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String rut;

    private String email;

    private Integer curso;

    @NotBlank // Hacemos que sea obligatoria para el registro
    private String password; // <--- CAMBIO: Agregamos el campo de contraseña
}
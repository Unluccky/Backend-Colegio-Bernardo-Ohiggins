package com.colegio.servicio_academico.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "apoderados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Apoderado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @Column(unique = true, nullable = false)
    private String rut;

    private String email;
    @NotBlank // Hacemos que sea obligatoria para el registro
    private String contrasena; // <--- CAMBIO: Agregamos el campo de contraseña

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;
}
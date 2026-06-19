package com.colegio.servicio_asistencia.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "anotaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Anotacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long estudianteId;

    private Long profesorId;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private TipoAnotacion tipo;

    private LocalDate fecha;
}
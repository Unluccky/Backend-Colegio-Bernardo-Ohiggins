package com.colegio.servicio_asistencia.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnotacionEvent implements Serializable {
    private Long estudianteId;
    private Long profesorId;
    private String descripcion;
    private String tipo;
    private LocalDate fecha;
    private Long apoderadoId;
}
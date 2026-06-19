package com.colegio.servicio_comunicaciones.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    private String id;

    private Long destinatarioId;

    private String titulo;

    private String mensaje;

    private LocalDateTime fecha;

    private Boolean leida;

    private TipoNotificacion tipo;
}
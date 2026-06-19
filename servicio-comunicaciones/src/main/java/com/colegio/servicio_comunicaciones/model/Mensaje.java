package com.colegio.servicio_comunicaciones.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {

    @Id
    private String id;

    private Long remitenteId;

    private String remitenteTipo;

    private Long destinatarioId;

    private String destinatarioTipo;

    private String asunto;

    private String contenido;

    private LocalDateTime fechaEnvio;

    private Boolean leido;
}
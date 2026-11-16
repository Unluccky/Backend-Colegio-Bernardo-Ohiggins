package com.colegio.servicio_comunicaciones.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Document(collection = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {

    @Id
    private String id;

    @NotNull @Positive
    private Long remitenteId;

    @NotBlank
    private String remitenteTipo;

    @NotNull @Positive
    private Long destinatarioId;

    @NotBlank
    private String destinatarioTipo;

    @NotBlank
    private String asunto;

    @NotBlank
    private String contenido;

    private LocalDateTime fechaEnvio;

    private Boolean leido;
}

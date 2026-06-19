package com.colegio.servicio_comunicaciones.config;

import com.colegio.servicio_comunicaciones.model.Mensaje;
import com.colegio.servicio_comunicaciones.model.Notificacion;
import com.colegio.servicio_comunicaciones.model.TipoNotificacion;
import com.colegio.servicio_comunicaciones.repository.MensajeRepository;
import com.colegio.servicio_comunicaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final MensajeRepository mensajeRepo;
    private final NotificacionRepository notificacionRepo;

    @Override
    public void run(String... args) {
        if (mensajeRepo.count() > 0) {
            log.info("Base de datos ya contiene datos — se omite el seed.");
            return;
        }

        log.info("=== Sembrando datos de prueba en Servicio Comunicaciones ===");

        // IDs: profesor Carlos Muñoz = 1, profesor Laura González = 2, Ana Soto = 1, Pedro Ramírez = 2
        Long profeMate = 1L;
        Long profeLengua = 2L;
        Long ana = 1L;
        Long pedro = 2L;
        Long apoderadoMaria = 1L;

        // ── Mensajes ────────────────────────────────────────────────
        mensajeRepo.save(Mensaje.builder()
                .remitenteId(profeMate).remitenteTipo("PROFESOR").destinatarioId(ana).destinatarioTipo("ESTUDIANTE")
                .asunto("Resultados prueba de álgebra")
                .contenido("Hola Ana, te informo que tu prueba de álgebra fue corregida. Obtuviste un 5.5, ¡buen trabajo! Sigue así.")
                .fechaEnvio(LocalDateTime.now().minusDays(3)).leido(false).build());

        mensajeRepo.save(Mensaje.builder()
                .remitenteId(profeLengua).remitenteTipo("PROFESOR").destinatarioId(ana).destinatarioTipo("ESTUDIANTE")
                .asunto("Trabajo de lectura")
                .contenido("Ana, tu trabajo de lectura fue excelente. Te felicito por el esfuerzo.")
                .fechaEnvio(LocalDateTime.now().minusDays(2)).leido(true).build());

        mensajeRepo.save(Mensaje.builder()
                .remitenteId(profeMate).remitenteTipo("PROFESOR").destinatarioId(pedro).destinatarioTipo("ESTUDIANTE")
                .asunto("Prueba de álgebra")
                .contenido("Pedro, tu prueba de álgebra tiene un 4.0. Puedes mejorar, te recomiendo repasar los ejercicios de la unidad 2.")
                .fechaEnvio(LocalDateTime.now().minusDays(3)).leido(false).build());

        mensajeRepo.save(Mensaje.builder()
                .remitenteId(apoderadoMaria).remitenteTipo("APODERADO").destinatarioId(profeMate).destinatarioTipo("PROFESOR")
                .asunto("Consulta sobre notas de Ana")
                .contenido("Estimado profesor Carlos, quisiera saber si hay oportunidades de recuperación para las evaluaciones. Atte, María Soto.")
                .fechaEnvio(LocalDateTime.now().minusDays(1)).leido(false).build());
        log.info("  ✔ Mensajes: 4 creados");

        // ── Notificaciones ──────────────────────────────────────────
        notificacionRepo.save(Notificacion.builder()
                .destinatarioId(ana).titulo("Nueva nota disponible")
                .mensaje("Tu nota de la Prueba N°1 - Álgebra ya está disponible.")
                .fecha(LocalDateTime.now().minusDays(3)).leida(false)
                .tipo(TipoNotificacion.NOTA).build());

        notificacionRepo.save(Notificacion.builder()
                .destinatarioId(ana).titulo("Anotación positiva")
                .mensaje("Recibiste una anotación positiva por participación en clases.")
                .fecha(LocalDateTime.now().minusDays(3)).leida(false)
                .tipo(TipoNotificacion.ANOTACION).build());

        notificacionRepo.save(Notificacion.builder()
                .destinatarioId(ana).titulo("Asistencia registrada")
                .mensaje("Se registró tu asistencia del día de hoy como ATRASADO.")
                .fecha(LocalDateTime.now()).leida(false)
                .tipo(TipoNotificacion.ASISTENCIA).build());

        notificacionRepo.save(Notificacion.builder()
                .destinatarioId(pedro).titulo("Nueva nota disponible")
                .mensaje("Tu nota de la Prueba N°1 - Álgebra ya está disponible.")
                .fecha(LocalDateTime.now().minusDays(3)).leida(true)
                .tipo(TipoNotificacion.NOTA).build());

        notificacionRepo.save(Notificacion.builder()
                .destinatarioId(1L).titulo("Mensaje recibido")
                .mensaje("Tienes un nuevo mensaje de tu apoderado.")
                .fecha(LocalDateTime.now().minusDays(1)).leida(false)
                .tipo(TipoNotificacion.MENSAJE).build());
        log.info("  ✔ Notificaciones: 5 creadas");

        log.info("✅ Seed de Servicio Comunicaciones completado.");
    }
}

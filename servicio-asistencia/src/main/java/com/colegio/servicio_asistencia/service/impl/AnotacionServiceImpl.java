package com.colegio.servicio_asistencia.service.impl;

import com.colegio.servicio_asistencia.feign.AcademicoFeignClient;
import com.colegio.servicio_asistencia.messaging.AnotacionEvent;
import com.colegio.servicio_asistencia.messaging.AnotacionEventPublisher;
import com.colegio.servicio_asistencia.model.Anotacion;
import com.colegio.servicio_asistencia.model.TipoAnotacion;
import com.colegio.servicio_asistencia.repository.AnotacionRepository;
import com.colegio.servicio_asistencia.service.AnotacionService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnotacionServiceImpl implements AnotacionService {

    private final AnotacionRepository repo;
    private final AcademicoFeignClient academicoClient;
    private final AnotacionEventPublisher eventPublisher;

    @Override
    public List<Anotacion> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Anotacion buscarPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Anotacion no encontrada: " + id));
    }

    @Override
    public List<Anotacion> buscarPorEstudiante(Long estudianteId) {
        return repo.findByEstudianteId(estudianteId);
    }

    @Override
    public Anotacion guardar(Anotacion anotacion) {
        // OpenFeign + Circuit Breaker: valida que el estudiante exista en servicio-academico
        try {
            Object estudiante = academicoClient.buscarEstudiantePorId(anotacion.getEstudianteId());
            if (estudiante != null) {
                log.info("Estudiante {} validado correctamente", anotacion.getEstudianteId());
            } else {
                log.warn("No se pudo validar el estudiante {} (servicio-academico no disponible)", anotacion.getEstudianteId());
            }
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Estudiante no encontrado con id: " + anotacion.getEstudianteId());
        }

        Anotacion guardada = repo.save(anotacion);

        // RabbitMQ: publica evento solo si la anotacion es NEGATIVA
        if (TipoAnotacion.NEGATIVA.equals(guardada.getTipo())) {

            // Buscar apoderado del estudiante via Feign + Circuit Breaker
            Long apoderadoId = null;
            try {
                List<Map<String, Object>> apoderados = academicoClient
                        .buscarApoderadosPorEstudiante(guardada.getEstudianteId());
                if (apoderados != null && !apoderados.isEmpty()) {
                    apoderadoId = Long.valueOf(apoderados.get(0).get("id").toString());
                    log.info("Apoderado {} encontrado para estudiante {}", apoderadoId, guardada.getEstudianteId());
                } else {
                    log.warn("No se pudieron obtener apoderados para el estudiante {} (servicio no disponible)", guardada.getEstudianteId());
                }
            } catch (FeignException.NotFound e) {
                log.warn("Estudiante {} no tiene apoderados registrados", guardada.getEstudianteId());
            }

            AnotacionEvent evento = AnotacionEvent.builder()
                    .estudianteId(guardada.getEstudianteId())
                    .profesorId(guardada.getProfesorId())
                    .descripcion(guardada.getDescripcion())
                    .tipo(guardada.getTipo().name())
                    .fecha(guardada.getFecha())
                    .apoderadoId(apoderadoId)
                    .build();

            eventPublisher.publicar(evento);
        }

        return guardada;
    }

    @Override
    public Anotacion actualizar(Long id, Anotacion datos) {
        Anotacion existente = buscarPorId(id);
        existente.setDescripcion(datos.getDescripcion());
        existente.setTipo(datos.getTipo());
        existente.setFecha(datos.getFecha());
        existente.setProfesorId(datos.getProfesorId());
        return repo.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
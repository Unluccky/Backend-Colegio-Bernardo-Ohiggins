package com.colegio.servicio_asistencia.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AcademicoFeignClientFallback implements AcademicoFeignClient {

    @Override
    public Object buscarEstudiantePorId(Long id) {
        log.warn("Fallback: No se pudo obtener el estudiante {} (servicio-academico no disponible)", id);
        return null;
    }

    @Override
    public List<Map<String, Object>> buscarApoderadosPorEstudiante(Long estudianteId) {
        log.warn("Fallback: No se pudieron obtener apoderados del estudiante {} (servicio-academico no disponible)", estudianteId);
        return Collections.emptyList();
    }
}

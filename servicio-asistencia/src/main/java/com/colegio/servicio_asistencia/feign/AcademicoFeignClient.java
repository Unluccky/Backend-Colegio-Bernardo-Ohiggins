package com.colegio.servicio_asistencia.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Map;

@FeignClient(name = "servicio-academico", url = "${academico.service.url}", fallback = AcademicoFeignClientFallback.class)
public interface AcademicoFeignClient {

    @GetMapping("/api/estudiantes/{id}")
    Object buscarEstudiantePorId(@PathVariable("id") Long id);

    @GetMapping("/api/apoderados/estudiante/{estudianteId}")
    List<Map<String, Object>> buscarApoderadosPorEstudiante(@PathVariable("estudianteId") Long estudianteId);
}
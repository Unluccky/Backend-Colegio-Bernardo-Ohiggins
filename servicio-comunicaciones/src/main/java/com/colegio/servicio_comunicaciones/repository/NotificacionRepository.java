package com.colegio.servicio_comunicaciones.repository;

import com.colegio.servicio_comunicaciones.model.Notificacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface NotificacionRepository extends MongoRepository<Notificacion, String> {
    List<Notificacion> findByDestinatarioId(Long destinatarioId);
    List<Notificacion> findByLeida(Boolean leida);
}
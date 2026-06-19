package com.colegio.servicio_comunicaciones.repository;

import com.colegio.servicio_comunicaciones.model.Mensaje;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface MensajeRepository extends MongoRepository<Mensaje, String> {
    List<Mensaje> findByDestinatarioId(Long destinatarioId);
    List<Mensaje> findByRemitenteId(Long remitenteId);

    @Query("{ '$or': [ { 'remitenteId': ?0, 'remitenteTipo': ?1 }, { 'destinatarioId': ?0, 'destinatarioTipo': ?1 } ] }")
    List<Mensaje> findByUsuarioIdAndTipo(Long usuarioId, String usuarioTipo);
}
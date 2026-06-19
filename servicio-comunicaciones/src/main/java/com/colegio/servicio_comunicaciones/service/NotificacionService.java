package com.colegio.servicio_comunicaciones.service;

import com.colegio.servicio_comunicaciones.model.Notificacion;
import java.util.List;

public interface NotificacionService {
    List<Notificacion> listarTodos();
    Notificacion buscarPorId(String id);
    List<Notificacion> buscarPorDestinatario(Long destinatarioId);
    Notificacion guardar(Notificacion notificacion);
    void eliminar(String id);
}
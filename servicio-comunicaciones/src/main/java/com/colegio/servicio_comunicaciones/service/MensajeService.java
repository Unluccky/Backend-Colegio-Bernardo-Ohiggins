package com.colegio.servicio_comunicaciones.service;

import com.colegio.servicio_comunicaciones.model.Mensaje;
import java.util.List;

public interface MensajeService {
    List<Mensaje> listarTodos();
    Mensaje buscarPorId(String id);
    List<Mensaje> buscarPorDestinatario(Long destinatarioId);
    List<Mensaje> buscarPorUsuario(Long usuarioId, String usuarioTipo);
    Mensaje guardar(Mensaje mensaje);
    Mensaje actualizar(String id, Mensaje mensaje);
    void eliminar(String id);
}